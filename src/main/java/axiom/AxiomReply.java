package axiom;

/**
 * Represents a reply from AXIOM to display in the user interface.
 */
public class AxiomReply {
    private final String message;
    private final boolean isError;

    private AxiomReply(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    /**
     * Returns a successful reply.
     *
     * @param message Reply text.
     * @return Successful reply.
     */
    public static AxiomReply success(String message) {
        return new AxiomReply(message, false);
    }

    /**
     * Returns an error reply.
     *
     * @param message Error description.
     * @return Error reply.
     */
    public static AxiomReply error(String message) {
        return new AxiomReply(message, true);
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return isError;
    }
}
