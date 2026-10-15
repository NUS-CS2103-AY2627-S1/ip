package nob.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nob.exception.NobException;
import nob.task.Task;

/**
 * Tests conversion of user command text into Nob task objects and task numbers.
 */
public class ParserTest {
    /**
     * Verifies that a valid to-do command creates the expected task.
     */
    @Test
    public void parseTodo_validDescription_todoCreated() throws NobException {
        Task task = Parser.parseTodo("todo read book");

        assertEquals("[T] read book [ ]", task.toString());
    }

    /**
     * Verifies that a to-do command without a description is rejected.
     */
    @Test
    public void parseTodo_missingDescription_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () -> Parser.parseTodo("todo   "));

        assertEquals("Use: todo DESCRIPTION\n(eg., todo borrow book)", exception.getMessage());
    }

    /**
     * Verifies that the dispatcher contract rejects the wrong command type.
     */
    @Test
    public void parseTodo_wrongCommand_assertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTodo("event meeting"));
    }

    @Test
    public void parseTodo_nullCommand_assertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTodo(null));
    }

    /**
     * Verifies that a complete deadline command creates the expected task.
     */
    @Test
    public void parseDeadline_validDetails_deadlineCreated() throws NobException {
        Task task = Parser.parseDeadline("deadline return book /by Friday");

        assertEquals("[D: Friday] return book [ ]", task.toString());
    }

    /**
     * Verifies that a deadline command without a description is rejected.
     */
    @Test
    public void parseDeadline_missingDescription_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () ->
                Parser.parseDeadline("deadline /by Friday"));

        assertEquals("Description should not be empty.\n"
                + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                + "(eg., deadline return book /by Sunday)", exception.getMessage());
    }

    /**
     * Verifies that a deadline delimiter with missing surrounding spaces is rejected.
     */
    @Test
    public void parseDeadline_malformedDelimiter_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () ->
                Parser.parseDeadline("deadline return book /byFriday"));

        assertEquals("Check that there is a space before and after '/by'.\n"
                + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                + "(eg., deadline return book /by Sunday)", exception.getMessage());
    }

    @Test
    public void parseDeadline_missingDelimiter_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () ->
                Parser.parseDeadline("deadline return book Friday"));

        assertEquals("Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                + "(eg., deadline return book /by Sunday)", exception.getMessage());
    }

    @Test
    public void parseDeadline_missingTime_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () ->
                Parser.parseDeadline("deadline return book /by "));

        assertEquals("Check that there is a space before and after '/by'.\n"
                + "Use: deadline DESCRIPTION /by DATE_OR_TIME\n"
                + "(eg., deadline return book /by Sunday)", exception.getMessage());
    }

    @Test
    public void parseDeadline_wrongCommand_assertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseDeadline("todo read book"));
    }

    /**
     * Verifies that a complete event command creates the expected task.
     */
    @Test
    public void parseEvent_validDetails_eventCreated() throws NobException {
        Task task = Parser.parseEvent("event team sync /from Mon 2pm /to 4pm");

        assertEquals("[E: Mon 2pm to 4pm] team sync [ ]", task.toString());
    }

    @Test
    public void parseEvent_endDateTimeBeforeStart_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () ->
                Parser.parseEvent("event team sync /from 2019-12-02 1900 /to 2019-12-02 1800"));

        assertEquals("Event end time should not be before its start time.\n"
                + "Use: event DESCRIPTION /from START /to END", exception.getMessage());
    }

    @Test
    public void parseEvent_endTimeBeforeStart_exceptionThrown() {
        NobException twelveHourException = assertThrows(NobException.class, () ->
                Parser.parseEvent("event team sync /from 5pm /to 4pm"));
        NobException twentyFourHourException = assertThrows(NobException.class, () ->
                Parser.parseEvent("event team sync /from 17:00 /to 16:00"));

        String expectedMessage = "Event end time should not be before its start time.\n"
                + "Use: event DESCRIPTION /from START /to END";
        assertEquals(expectedMessage, twelveHourException.getMessage());
        assertEquals(expectedMessage, twentyFourHourException.getMessage());
    }

    @Test
    public void parseEvent_endEqualsStart_eventCreated() throws NobException {
        Task task = Parser.parseEvent("event instant sync /from 5pm /to 5pm");

        assertEquals("[E: 5pm to 5pm] instant sync [ ]", task.toString());
    }

    @Test
    public void parseEvent_endAfterStart_eventCreated() throws NobException {
        Task task = Parser.parseEvent("event team sync /from 2019-12-02 1800 /to 2019-12-03 0900");

        assertEquals("[E: Dec 02 2019, 6:00PM to Dec 03 2019, 9:00AM] team sync [ ]",
                task.toString());
    }

    /**
     * Verifies that an event command without a description is rejected.
     */
    @Test
    public void parseEvent_missingDescription_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () ->
                Parser.parseEvent("event /from Mon 2pm /to 4pm"));

        assertEquals("Description should not be empty.\n"
                + "Use: event DESCRIPTION /from START /to END\n"
                + "(eg., event project meeting /from Mon 2pm /to 4pm)", exception.getMessage());
    }

    /**
     * Verifies that event delimiters with missing surrounding spaces are rejected.
     */
    @Test
    public void parseEvent_malformedDelimiter_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () ->
                Parser.parseEvent("event team sync /fromMon 2pm /to 4pm"));

        assertEquals("Check that there is a space before and after '/from' and '/to'.\n"
                + "Use: event DESCRIPTION /from START /to END\n"
                + "(eg., event project meeting /from Mon 2pm /to 4pm)", exception.getMessage());
    }

    @Test
    public void parseEvent_missingDelimiters_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () ->
                Parser.parseEvent("event team sync Monday Tuesday"));

        assertEquals("Use: event DESCRIPTION /from START /to END\n"
                + "(eg., event project meeting /from Mon 2pm /to 4pm)", exception.getMessage());
    }

    @Test
    public void parseEvent_missingStartOrEndTime_exceptionThrown() {
        NobException missingStartException = assertThrows(NobException.class, () ->
                Parser.parseEvent("event team sync /from  /to Tuesday"));
        NobException missingEndException = assertThrows(NobException.class, () ->
                Parser.parseEvent("event team sync /from Monday /to "));

        String expectedMessage = "Check that there is a space before and after '/from' and '/to'.\n"
                + "Use: event DESCRIPTION /from START /to END\n"
                + "(eg., event project meeting /from Mon 2pm /to 4pm)";
        assertEquals(expectedMessage, missingStartException.getMessage());
        assertEquals(expectedMessage, missingEndException.getMessage());
    }

    @Test
    public void parseEvent_wrongCommand_assertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseEvent("todo read book"));
    }

    /**
     * Verifies that task numbers are parsed even when surrounded by extra spaces.
     */
    @Test
    public void parseTaskNumber_numberWithExtraSpaces_numberReturned() throws NobException {
        assertEquals(12, Parser.parseTaskNumber("mark   12  "));
    }

    /**
     * Verifies that a non-numeric task number is rejected.
     */
    @Test
    public void parseTaskNumber_nonNumericValue_exceptionThrown() {
        NobException exception = assertThrows(NobException.class, () -> Parser.parseTaskNumber("delete two"));

        assertEquals("Please enter a valid task number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_emptyOrOutOfRangeValue_exceptionThrown() {
        NobException emptyException = assertThrows(NobException.class, () -> Parser.parseTaskNumber("mark "));
        NobException largeException = assertThrows(NobException.class, () ->
                Parser.parseTaskNumber("unmark 999999999999999999999"));

        assertEquals("Please enter a valid task number.", emptyException.getMessage());
        assertEquals("Please enter a valid task number.", largeException.getMessage());
    }

    @Test
    public void parseTaskNumber_wrongCommand_assertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTaskNumber("todo 1"));
        assertThrows(AssertionError.class, () -> Parser.parseTaskNumber(null));
    }
}
