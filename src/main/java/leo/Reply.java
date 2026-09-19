package leo;

/**
 * A single reply from Leo to one command, together with whether it was
 * an error message. Used by the GUI to show error replies differently
 * from normal ones, without {@link Leo} itself needing to know how -
 * or whether - errors are ever displayed differently; the console
 * ({@link Ui}) simply prints every reply the same way regardless.
 */
public final class Reply {
    private final String text;
    private final boolean isError;

    /**
     * Creates a new reply.
     *
     * @param text the reply text
     * @param isError whether this reply is an error message
     */
    public Reply(String text, boolean isError) {
        this.text = text;
        this.isError = isError;
    }

    /**
     * Returns the reply text.
     *
     * @return the reply text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns whether this reply is an error message.
     *
     * @return true if this reply is an error message, false otherwise
     */
    public boolean isError() {
        return isError;
    }
}
