package harvey;
/**
 * Signals that Harvey understood the user well enough to know the input is wrong,
 * but cannot carry it out — for example a {@code todo} with no description.
 * <p>
 * The message carried by this exception is written for the user to read, so it is
 * shown directly as Harvey's reply. Using a checked exception (one that extends
 * {@code Exception} rather than {@code RuntimeException}) means the compiler forces
 * every caller to deal with the failure, which is what we want for input mistakes:
 * they are expected, not bugs.
 */
public class HarveyException extends Exception {
    /**
     * Version stamp required of every exception, because exceptions can in principle be
     * saved to a file. Harvey never does that, so the value only needs to be present.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception carrying an explanation meant for the user.
     *
     * @param message what went wrong and, where useful, how to fix it.
     */
    public HarveyException(String message) {
        super(message);
    }
}
