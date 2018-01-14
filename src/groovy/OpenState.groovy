

class OpenState extends CircuitBreakerState
{
    private final long openDateTime

    OpenState(CircuitBreaker circuitBreaker)
    {
        super(circuitBreaker)
        openDateTime = System.currentTimeMillis()
    }

    @Override
    CircuitBreaker ProtectedCodeIsAboutToBeCalled()
    {
        super.ProtectedCodeIsAboutToBeCalled()
        this.Update()
        return super.circuitBreaker
    }

    @Override
    CircuitBreakerState Update()
    {
        super.Update()
         if (System.currentTimeMillis() >= openDateTime + super.circuitBreaker.getTimeout())
        {
            return circuitBreaker.MoveToHalfOpenState()
        }
        return this
    }
}
