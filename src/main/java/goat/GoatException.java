package goat;

/**
 * An error that Goat can explain to the user in plain language.
 *
 * It extends Exception rather than RuntimeException so that it is a
 * *checked* exception: the compiler forces every caller to either handle
 * it or declare it, which makes it impossible to forget one.
 *
 * The message passed in should read as a complete sentence, since it is
 * shown to the user directly after the "OOPS!!! " prefix.
 */
public class GoatException extends Exception {
    /**
     * Creates an exception carrying an explanation for the user.
     *
     * @param message what went wrong, phrased for the user to read
     */
    public GoatException(String message) {
        super(message);
    }
}
