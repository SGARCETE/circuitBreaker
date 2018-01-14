
class CircuitBreakerTest extends GroovyTestCase {

    void testGetRequestCount() {
        def circuitBreaker = new CircuitBreaker(3,10,60)
        def codeToExecute = {print("1")}

        for(int i = 0; i<3;i++) {
            circuitBreaker.AttemptCall({codeToExecute},{})
        }

        assert circuitBreaker.requestCount == 57
    }

    void testGetFailures() {
        def circuitBreaker = new CircuitBreaker(5,10,60)
        def codeToExecute = {throw new Exception("exception")}

        for(int i = 0; i<3;i++) {
            circuitBreaker.AttemptCall(codeToExecute,{})
        }

        assert circuitBreaker.failures == 3
    }

    void testGetIsClosed() {
        def circuitBreaker = new CircuitBreaker(5,10,60)
        def codeToExecute = {print("testGetIsClosed")}

        assertTrue(circuitBreaker.getIsClosed())

        for(int i = 0; i<3;i++) {
            circuitBreaker.AttemptCall(codeToExecute,{})
        }

        assertTrue(circuitBreaker.getIsClosed())
    }

    void testGetIsOpen() {
        def circuitBreaker = new CircuitBreaker(1,100,1)
        def codeToExecute = {print("testGetIsOpen")}

        assertTrue(circuitBreaker.getIsClosed())

        for(int i = 0; i<3;i++) {
            circuitBreaker.AttemptCall(codeToExecute,{})
        }

        assertTrue(circuitBreaker.getIsOpen())
    }

    //void testGetIsHalfOpen() {
    //}

    void testMoveToClosedState() {
        def circuitBreaker = new CircuitBreaker(1, 100, 1)
        def codeToExecute = { print("testGetIsOpen") }

        for (int i = 0; i < 3; i++) {
            circuitBreaker.AttemptCall(codeToExecute, {})
        }

        assertTrue(circuitBreaker.getIsOpen())

        circuitBreaker.MoveToClosedState()

        assertTrue(circuitBreaker.getIsClosed())
    }

    void testMoveToOpenState() {
        def circuitBreaker = new CircuitBreaker(1, 100, 1)

        circuitBreaker.MoveToOpenState()

        assertTrue(circuitBreaker.getIsOpen())
    }

    void testMoveToHalfOpenState() {
        def circuitBreaker = new CircuitBreaker(1, 100, 1)

        circuitBreaker.MoveToHalfOpenState()

        assertTrue(circuitBreaker.getIsHalfOpen())
    }

    void testIncreaseFailureCount() {
        def circuitBreaker = new CircuitBreaker(1, 100, 1)

        circuitBreaker.AttemptCall({throw new Exception("testIncreaseFailureCount")},{})

        assert circuitBreaker.getFailures() == 1

        circuitBreaker.IncreaseFailureCount()

        assert circuitBreaker.getFailures() == 2
    }

    void testResetFailureCount() {
        def circuitBreaker = new CircuitBreaker(1, 100, 10)

        circuitBreaker.AttemptCall({throw new Exception("testResetFailureCount")}, {})

        assert circuitBreaker.getFailures() == 1

        circuitBreaker.ResetFailureCount()

        assert circuitBreaker.getFailures() == 0
    }

    void testIsThresholdReached() {
        def circuitBreaker = new CircuitBreaker(1, 100, 10)

        circuitBreaker.AttemptCall({throw new Exception("testResetFailureCount")}, {})

        assertTrue(circuitBreaker.IsThresholdReached())
    }

    void testAttemptCall() {
        def circuitBreaker = new CircuitBreaker(1, 100, 10)
        def testAux = 0

        circuitBreaker.AttemptCall({testAux = 1}, {})

        assert testAux == 1

    }
}
