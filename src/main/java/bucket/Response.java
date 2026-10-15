package bucket;

/**
 * One reply from the chatbot, together with whether it reports a problem.
 *
 * The reply used to be a bare String, which left the GUI no way to tell a refusal
 * apart from a confirmation without inspecting the wording. Carrying the fact
 * alongside the text lets errors be styled to stand out, and keeps the front ends
 * from having to recognise phrases like "OOPS!!!".
 *
 * @param text Wording to show the user.
 * @param isError True if this reply tells the user something went wrong.
 */
public record Response(String text, boolean isError) {

    /**
     * Returns an ordinary reply.
     *
     * @param text Wording to show the user.
     * @return Reply marked as not an error.
     */
    public static Response of(String text) {
        assert text != null : "a reply needs text, not null";
        return new Response(text, false);
    }

    /**
     * Returns a reply that reports a problem, which front ends may highlight.
     *
     * @param text Wording to show the user.
     * @return Reply marked as an error.
     */
    public static Response error(String text) {
        assert text != null : "a reply needs text, not null";
        return new Response(text, true);
    }
}
