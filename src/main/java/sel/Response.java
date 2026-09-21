package sel;

/**
 * A reply from Sel together with whether it reports a problem with the
 * user's command.
 *
 * <p>Previously {@code Sel.getResponse} returned a plain {@code String},
 * which left the GUI no way of knowing whether a reply was an error. A
 * small record is the simplest way to carry that extra bit of information
 * without the GUI having to inspect the message text. (A richer design
 * would use one class per command result, but that is more machinery than
 * this chatbot needs.)
 *
 * @param text the message to show to the user.
 * @param isError whether the message reports a command Sel could not carry out.
 */
public record Response(String text, boolean isError) {

    /**
     * Creates a normal (non-error) response.
     *
     * @param text the message to show to the user.
     * @return a response that is not an error.
     */
    public static Response of(String text) {
        return new Response(text, false);
    }

    /**
     * Creates an error response, shown in the GUI as a highlighted bubble.
     *
     * @param text the message to show to the user.
     * @return a response flagged as an error.
     */
    public static Response ofError(String text) {
        return new Response(text, true);
    }
}
