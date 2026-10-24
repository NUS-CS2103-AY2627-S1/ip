package shannon;

/**
 * One reply from the chatbot: the words to show, and whether they report a problem.
 * <p>
 * {@link Shannon#getResponse(String)} used to hand back a bare string, which told the caller
 * what to say but not how to say it. The GUI needs the second half of that: a complaint about a
 * mistyped command should not look like a cheerful confirmation. Rather than have the window
 * guess &mdash; by searching the text for words like "sorry", which would break the moment the
 * wording changed &mdash; the chatbot states it outright here.
 * <p>
 * A {@code record} is used because this type is nothing but its two values: Java then writes the
 * constructor, the {@code text()} and {@code isError()} accessors, {@code equals}, {@code hashCode}
 * and {@code toString} for us. A plain class with two final fields would behave the same; it would
 * just be twenty more lines saying so.
 *
 * @param text    what the chatbot says, ready to be shown as-is.
 * @param isError whether the text explains something that went wrong.
 */
public record Response(String text, boolean isError) {

    /**
     * Returns an ordinary reply, one that reports success.
     *
     * @param text what the chatbot says.
     * @return the reply, marked as not being an error.
     */
    public static Response of(String text) {
        return new Response(text, false);
    }

    /**
     * Returns a reply that explains something that went wrong, so the window can set it apart.
     *
     * @param text the explanation to show the user.
     * @return the reply, marked as an error.
     */
    public static Response ofError(String text) {
        return new Response(text, true);
    }
}
