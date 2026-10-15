/**
 * Compatibility launcher for IDE configurations that still refer to {@code Es}.
 * The application implementation lives in the {@code es} package.
 */
public class Es {
    /** Delegates to the packaged application entry point. */
    public static void main(String[] args) {
        es.Es.main(args);
    }
}
