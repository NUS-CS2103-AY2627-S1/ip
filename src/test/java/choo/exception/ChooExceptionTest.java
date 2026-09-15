package choo.exception;

/**
 * Checks the chatbot-specific checked exception contract.
 */
public class ChooExceptionTest {
    /**
     * Verifies that a CHOO error is an {@link Exception} and preserves its
     * user-facing explanation.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Exception exception = new ChooException("A todo needs a description.");

        if (!"A todo needs a description.".equals(exception.getMessage())) {
            throw new AssertionError("ChooException did not preserve its message");
        }
    }
}
