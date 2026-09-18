package toothless;

/**
 * Provides platform-compatible symbols for console output.
 */
public final class DisplaySymbols {
    private static final String OPERATING_SYSTEM_PROPERTY = "os.name";
    private static final String WINDOWS_NAME_PREFIX = "Windows";
    private static final String STAR = "★";
    private static final String WINDOWS_DONE_MARK = "X";
    private static final String WINDOWS_DECORATIVE_MARK = "*";

    private DisplaySymbols() {
    }

    /**
     * Returns the completed-task marker for the current operating system.
     *
     * @return {@code X} on Windows, or a star on other operating systems.
     */
    public static String getDoneMark() {
        return getDoneMark(System.getProperty(OPERATING_SYSTEM_PROPERTY, ""));
    }

    /**
     * Returns the completed-task marker for the supplied operating-system name.
     *
     * @param operatingSystemName operating-system name to inspect.
     * @return {@code X} for Windows, or a star otherwise.
     */
    static String getDoneMark(String operatingSystemName) {
        return isWindows(operatingSystemName) ? WINDOWS_DONE_MARK : STAR;
    }

    /**
     * Returns the decorative marker for the current operating system.
     *
     * @return an asterisk on Windows, or a star on other operating systems.
     */
    public static String getDecorativeMark() {
        return getDecorativeMark(System.getProperty(OPERATING_SYSTEM_PROPERTY, ""));
    }

    /**
     * Returns the decorative marker for the supplied operating-system name.
     *
     * @param operatingSystemName operating-system name to inspect.
     * @return an asterisk for Windows, or a star otherwise.
     */
    static String getDecorativeMark(String operatingSystemName) {
        return isWindows(operatingSystemName) ? WINDOWS_DECORATIVE_MARK : STAR;
    }

    /**
     * Returns whether the supplied name represents a Windows operating system.
     *
     * @param operatingSystemName operating-system name to inspect.
     * @return true if the name begins with {@code Windows}.
     */
    private static boolean isWindows(String operatingSystemName) {
        return operatingSystemName.startsWith(WINDOWS_NAME_PREFIX);
    }
}
