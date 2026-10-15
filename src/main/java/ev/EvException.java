package ev;

/**
 * Something EV cannot do, described in a message meant to be shown to the user.
 *
 * <p>Every message is written so that it can be printed as it is: it says what went
 * wrong and, where useful, shows an example of what to type instead. Those two halves
 * are passed as separate lines rather than being joined by hand at each throw site.
 */
public class EvException extends Exception {

    /**
     * Creates an exception carrying a message for the user.
     *
     * @param lines the lines of the message, in the order they should be shown. They are
     *     joined with line breaks, so a single-line message is simply one argument.
     */
    public EvException(String... lines) {
        super(String.join("\n", lines));
    }
}
