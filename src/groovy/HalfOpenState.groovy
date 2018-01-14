
public class HalfOpenState extends CircuitBreakerState
{
    public HalfOpenState(CircuitBreaker circuitBreaker) {
        super(circuitBreaker);
    }
    @Override
    public void ActUponException(Exception e)
    {
        super.ActUponException(e);
        circuitBreaker.MoveToOpenState();
    }

    @Override
    public void ProtectedCodeHasBeenCalled()
    {
        super.ProtectedCodeHasBeenCalled(); //superclase mueve circuitBreaker a closedState
        circuitBreaker.MoveToClosedState();
    }
}
