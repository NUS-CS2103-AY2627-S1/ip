package mono.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import mono.exception.WrongFormatException;

/**
 * Tests the parsing and validation of task IDs.
 */
public class TaskIdParserTest {
    private static final String INVALID_ID_MESSAGE = "Task ID must be a positive integer.";

    @Test
    public void parse_positiveInteger_returnsTaskId() throws WrongFormatException {
        assertEquals(1, TaskIdParser.parse("1"));
        assertEquals(Integer.MAX_VALUE, TaskIdParser.parse(String.valueOf(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_positiveIntegerWithWhitespace_returnsTaskId() throws WrongFormatException {
        assertEquals(42, TaskIdParser.parse("  42  "));
    }

    @Test
    public void parse_positiveIntegerWithSignAndLeadingZeros_returnsTaskId()
            throws WrongFormatException {
        assertEquals(7, TaskIdParser.parse("+007"));
    }

    @Test
    public void parse_emptyInput_throwsRequiredIdException() {
        WrongFormatException exception = assertThrows(
                WrongFormatException.class, () -> TaskIdParser.parse(""));

        assertEquals("Task ID is required.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void parse_whitespaceOnlyInput_throwsRequiredIdException() {
        WrongFormatException exception = assertThrows(
                WrongFormatException.class, () -> TaskIdParser.parse(" \t\n "));

        assertEquals("Task ID is required.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void parse_nullInput_throwsRequiredIdException() {
        WrongFormatException exception = assertThrows(
                WrongFormatException.class, () -> TaskIdParser.parse(null));

        assertEquals("Task ID is required.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void parse_zero_throwsInvalidIdException() {
        WrongFormatException exception = assertThrows(
                WrongFormatException.class, () -> TaskIdParser.parse("0"));

        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void parse_negativeInteger_throwsInvalidIdException() {
        WrongFormatException exception = assertThrows(
                WrongFormatException.class, () -> TaskIdParser.parse("-1"));

        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void parse_alphabeticInput_throwsInvalidIdExceptionWithCause() {
        assertNonNumericInputRejected("one");
    }

    @Test
    public void parse_decimalInput_throwsInvalidIdExceptionWithCause() {
        assertNonNumericInputRejected("1.5");
    }

    @Test
    public void parse_multipleTaskIds_throwsInvalidIdExceptionWithCause() {
        assertNonNumericInputRejected("1 2");
    }

    @Test
    public void parse_integerAboveSupportedRange_throwsInvalidIdExceptionWithCause() {
        assertNonNumericInputRejected("2147483648");
    }

    @Test
    public void parse_integerBelowSupportedRange_throwsInvalidIdExceptionWithCause() {
        assertNonNumericInputRejected("-2147483649");
    }

    /**
     * Verifies the common exception contract for text that {@link Integer#parseInt(String)}
     * cannot parse.
     */
    private void assertNonNumericInputRejected(String input) {
        WrongFormatException exception = assertThrows(
                WrongFormatException.class, () -> TaskIdParser.parse(input));

        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
        assertInstanceOf(NumberFormatException.class, exception.getCause());
    }
}
