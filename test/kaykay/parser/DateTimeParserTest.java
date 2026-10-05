package kaykay.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import kaykay.parser.DateTimeParser;

/**
 * Focused checks for Kaykay's standard date/time parser and formatter.
 *
 * This project does not currently use a test framework, so the class is run
 * directly and throws an assertion error when a check fails.
 */
public final class DateTimeParserTest {
    private DateTimeParserTest() {
        // Prevent instantiation of this test utility class.
    }

    /** Runs all focused date/time checks. */
    public static void main(String[] args) {
        parsesValidDateTime();
        formatsDateTimeUsingTheStandardFormat();
        rejectsInvalidDateTimeValues();
        rejectsValuesWithIncorrectSpacing();
        System.out.println("All DateTimeParser tests passed.");
    }

    /** Checks that valid input becomes the expected LocalDateTime value. */
    private static void parsesValidDateTime() {
        LocalDateTime actual = DateTimeParser.parse("25 12 2026 18:30");
        LocalDateTime expected = LocalDateTime.of(2026, 12, 25, 18, 30);
        assertEquals(expected, actual, "valid date/time should be parsed");
    }

    /** Checks that output always uses two-digit fields and 24-hour time. */
    private static void formatsDateTimeUsingTheStandardFormat() {
        LocalDateTime value = LocalDateTime.of(2026, 1, 5, 6, 7);
        assertEquals("05 01 2026 06:07", DateTimeParser.format(value),
                "date/time should use the standard format");
    }

    /** Checks invalid calendar, clock, and free-form values. */
    private static void rejectsInvalidDateTimeValues() {
        assertThrows("31 02 2026 10:00");
        assertThrows("25 12 2026 24:00");
        assertThrows("25 12 2026 18:60");
        assertThrows("2026-12-25 18:30");
        assertThrows("Friday");
    }

    /** Checks that leading or trailing spaces are not silently accepted. */
    private static void rejectsValuesWithIncorrectSpacing() {
        assertThrows(" 25 12 2026 18:30");
        assertThrows("25 12 2026 18:30 ");
    }

    /** Verifies that parsing a value throws the expected exception. */
    private static void assertThrows(String value) {
        try {
            DateTimeParser.parse(value);
            throw new AssertionError("expected parsing to fail for: " + value);
        } catch (DateTimeParseException exception) {
            // Expected result.
        }
    }

    /** Verifies equality and reports the check being performed when it fails. */
    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + "; expected " + expected + " but got " + actual);
        }
    }
}
