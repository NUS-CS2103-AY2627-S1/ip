package todd;

/** Carries reply text, display categories, and an explicit application exit request. */
public record Response(String text, boolean error, boolean help, boolean exit) {
}
