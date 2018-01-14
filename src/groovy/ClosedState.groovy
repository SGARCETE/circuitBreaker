

public class ClosedState extends CircuitBreakerState
{
    public ClosedState(CircuitBreaker circuitBreaker) {
        super(circuitBreaker);
        circuitBreaker.ResetFailureCount();
    }

    @Override
    public void ActUponException(Exception e)
    {
        super.ActUponException(e);
        if (circuitBreaker.IsThresholdReached())
        {
            circuitBreaker.MoveToOpenState();
        }
    }
}