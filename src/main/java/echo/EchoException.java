package echo;

/**
 * Represents an input error that E.C.H.O. can explain to the user.
 */
public class EchoException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input error with the given explanation.
     *
     * @param message User-facing explanation of the error.
     */
    public EchoException(String message) {
        super(message);
    }
}
