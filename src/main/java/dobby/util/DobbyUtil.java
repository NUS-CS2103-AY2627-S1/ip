package dobby.util;

/**
 * Provides general-purpose string formatting for Dobby task descriptions.
 */
public final class DobbyUtil {
    private DobbyUtil() {
    }

    /** Wraps text in square brackets. */
    public static String encloseBracket(String text) {
        return "[" + text + "]";
    }
}
