package luckynoslacky.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests normalization of platform-dependent integration-test output. */
class TestOutputNormalizerTest {
    /** Verifies mixed line endings and one final line ending are normalized. */
    @Test
    void normalize_mixedLineEndingsAndTrailingLineEnding_returnsUnixText() {
        assertEquals(
                "first line\nsecond line",
                TestOutputNormalizer.normalize("first line\r\nsecond line\r"));
    }

    /** Verifies line-ending normalization preserves a final newline. */
    @Test
    void normalizeLineEndings_mixedLineEndings_preservesAllNewlines() {
        assertEquals(
                "first line\nsecond line\n",
                TestOutputNormalizer.normalizeLineEndings(
                        "first line\r\nsecond line\r"));
    }

    /** Verifies supported date and time names are replaced with stable tokens. */
    @Test
    void normalize_dateAndTimeNames_replacesLocaleDependentTokens() {
        assertEquals(
                "DAY MONTH 25 2026, 2.00MERIDIEM",
                TestOutputNormalizer.normalize("Tue Aug 25 2026, 2.00pm"));
    }

    /** Verifies unrelated output remains unchanged. */
    @Test
    void normalize_unrelatedText_preservesExactContent() {
        String output = "August Monday 2026-08-25 14.00\nmessage body";

        assertEquals(output, TestOutputNormalizer.normalize(output));
    }

    /** Verifies incidental trailing spaces do not break output comparisons. */
    @Test
    void normalize_trailingWhitespace_removesIncidentalLinePadding() {
        assertEquals(
                "first line\nsecond line",
                TestOutputNormalizer.normalize("first line  \nsecond line\t"));
    }
}
