
import javax.naming.LimitExceededException
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class CircuitBreaker
{
    private final Lock lock = new ReentrantLock()

    private CircuitBreakerState state
    private int failures
    private int threshold
    private long timeout


    private int requestsByTime
    private int requestCount
    private long initialTime
    private long refillInterval


    CircuitBreaker(int threshold, long timeout, long requestPerMinute)
    {
        if (threshold < 1)
        {
            throw new ArithmeticException("threshold should be greater than 0")
        }

        if (timeout < 1)
        {
            throw new ArithmeticException("timeout should be greater than 0")
        }

        if (requestPerMinute < 1)
        {
            throw new ArithmeticException("requestPerMinute should be greater than 0")
        }

        this.threshold = threshold
        this.timeout = timeout

        this.requestCount = requestPerMinute
        this.requestsByTime = requestPerMinute
        this.refillInterval = 1000*60
        initialTime = System.currentTimeMillis()

        MoveToClosedState()
    }

    int getRequestCount() {
        return requestCount
    }

    int getFailures() {
        return failures
    }

    long getTimeout() {
        return timeout
    }

    int getThreshold() {
        return threshold
    }

    boolean getIsClosed() {
        return state.Update() instanceof  ClosedState
    }

    boolean getIsOpen() {
        return state.Update() instanceof  OpenState
    }

    boolean getIsHalfOpen() {
        return state.Update() instanceof  HalfOpenState
    }
    CircuitBreakerState MoveToClosedState()
    {
        state = new ClosedState(this)
        return state
    }

    CircuitBreakerState MoveToOpenState()
    {
        state = new OpenState(this)
        return state
    }

    CircuitBreakerState MoveToHalfOpenState()
    {
        state = new HalfOpenState(this)
        return state
    }

    void IncreaseFailureCount()
    {
        failures++
    }

    void ResetFailureCount()
    {
        failures = 0
    }

    boolean IsThresholdReached()
    {
        return failures >= threshold
    }

    private Exception exceptionFromLastAttemptCall = null

    Exception GetExceptionFromLastAttemptCall()
    {
        return exceptionFromLastAttemptCall
    }

    CircuitBreaker AttemptCall(def protectedCode, def caseException)
    {
        this.exceptionFromLastAttemptCall = null

        lock.lock()
        try {
            state.ProtectedCodeIsAboutToBeCalled()
            if (state instanceof OpenState)
            {
                return this
            }
        }
        finally {
            lock.unlock()
        }

        try {
            if (this.canAttemptCall()) {
                protectedCode()
            } else {
                throw new LimitExceededException("Cantidad de peticiones por minuto alcanzada")
            }
        }
        catch (Exception e)
        {
            this.exceptionFromLastAttemptCall = e

            lock.lock()
            try {
                state.ActUponException(e)
            }
            finally {
                lock.unlock()
                caseException()
            }
            return this // Stop execution of this method
        }

        lock.lock()
        try {
            state.ProtectedCodeHasBeenCalled()
        }
        finally {
            lock.unlock()
        }


        return this
    }
    

    private boolean canAttemptCall(){
        if(System.currentTimeMillis() >= this.initialTime + this.refillInterval){
            this.requestCount = requestsByTime
            this.initialTime = System.currentTimeMillis()
        }

        if(this.requestCount>0){
            this.requestCount--
            return true
        }
        return false
    }

}