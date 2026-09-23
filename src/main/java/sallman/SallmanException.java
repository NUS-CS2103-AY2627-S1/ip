package sallman;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Signals that the user's input could not be carried out, for a reason worth
 * explaining to them.
 * <p>
 * This is a checked exception, so the compiler makes sure every place that can
 * reject input is handled somewhere rather than reaching the user as a stack
 * trace.
 */
public class SallmanException extends Exception {

    /**
     * Advice shown under the message, such as an example of the correct
     * command. Empty when the message speaks for itself.
     */
    private final String[] hints;

    /**
     * Creates an exception carrying an explanation and any advice on fixing it.
     * <p>
     * Taking the hints as varargs means one constructor covers a bare message,
     * a message with an example, and a message needing more than one line of
     * advice, rather than an overload for each.
     *
     * @param message what went wrong, phrased for the user
     * @param hints   zero or more lines of advice, one per line of output
     */
    public SallmanException(String message, String... hints) {
        super(message);
        this.hints = hints.clone();
    }

    /**
     * Returns this problem as the lines of a chatbot reply.
     *
     * @return the message, followed by any hints
     */
    public String[] toLines() {
        return Stream.concat(Stream.of(getMessage()), Arrays.stream(hints))
                .toArray(String[]::new);
    }
}
