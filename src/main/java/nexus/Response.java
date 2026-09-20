package nexus;

/** Describes text and presentation behavior produced by a Nexus command. */
public record Response(String text, boolean isError, boolean shouldExit) {
    /** Creates a normal response that keeps Nexus open. */
    public static Response success(String text) {
        return new Response(text, false, false);
    }

    /** Creates a validation or environment error response. */
    public static Response error(String text) {
        return new Response(text, true, false);
    }

    /** Creates the farewell response that closes Nexus after display. */
    public static Response exit(String text) {
        return new Response(text, false, true);
    }
}
