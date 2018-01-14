

abstract class CircuitBreakerState
{
    protected final CircuitBreaker circuitBreaker

    protected CircuitBreakerState(CircuitBreaker circuitBreaker)
    {
        this.circuitBreaker = circuitBreaker
    }

    CircuitBreaker ProtectedCodeIsAboutToBeCalled()
    {
        return this.circuitBreaker
    }

    void ProtectedCodeHasBeenCalled() { }

    void ActUponException(Exception e) {
        circuitBreaker.IncreaseFailureCount()
    }

    CircuitBreakerState Update()
    {
        return this
    }
}