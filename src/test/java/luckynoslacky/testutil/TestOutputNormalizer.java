package luckynoslacky.testutil;

/**
 * Normalizes platform-dependent portions of process output for integration
 * tests.
 */
public final class TestOutputNormalizer {
    private TestOutputNormalizer() {
    }

    /**
     * Normalizes line endings, incidental trailing whitespace, and localized
     * date/time names while preserving other output content.
     *
     * @param output process output to normalize
     * @return normalized output
     */
    public static String normalize(String output) {
        String normalized = normalizeLineEndings(output);
        normalized = normalized.replaceAll("(?m)[ \\t]+$", "");
        normalized = normalized.replaceAll(
                "(?i)\\b(?:mon|tue|wed|thu|fri|sat|sun)\\b", "DAY");
        normalized = normalized.replaceAll(
                "(?i)\\b(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\\b",
                "MONTH");
        normalized = normalized.replaceAll(
                "(?i)(?<![a-z])(?:am|pm)\\b", "MERIDIEM");
        return normalized.endsWith("\n")
                ? normalized.substring(0, normalized.length() - 1)
                : normalized;
    }

    /**
     * Converts Windows and legacy Mac line endings to Unix line endings.
     *
     * @param output text to normalize
     * @return text with Unix line endings
     */
    public static String normalizeLineEndings(String output) {
        return output.replace("\r\n", "\n")
                .replace('\r', '\n');
    }
}
