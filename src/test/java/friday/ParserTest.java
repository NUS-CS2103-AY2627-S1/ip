package friday;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import friday.task.Deadline;
import friday.task.Event;
import friday.task.Task;
import friday.task.Todo;

/**
 * Tests the conversion of user input and saved file lines into task objects.
 */
class ParserTest {

    /**
     * Tests that a valid todo command creates a todo with the expected save format.
     *
     * @throws FridayException if parsing fails unexpectedly
     */
    @Test
    void parseTask_validTodo_returnsTodoWithDescription() throws FridayException {
        Task task = Parser.parseTask("todo", "read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("T | 0 | read book", task.toFileString());
    }

    /**
     * Tests that a valid deadline command accepts date and time input.
     *
     * @throws FridayException if parsing fails unexpectedly
     */
    @Test
    void parseTask_validDeadline_correctlyAcceptsDateAndTime() throws FridayException {
        Task task = Parser.parseTask("deadline", "return book /by 2/12/2025 1800");

        assertInstanceOf(Deadline.class, task);
        assertEquals("D | 0 | return book | 2025-12-02 1800", task.toFileString());
    }

    /**
     * Tests that a valid event command accepts a start and end date and time.
     *
     * @throws FridayException if parsing fails unexpectedly
     */
    @Test
    void parseTask_validEvent_correctlyAcceptsStartAndEndTimes() throws FridayException {
        Task task = Parser.parseTask("event", "project meeting /from 3/12/2025 1400 /to 3/12/2025 1600");

        assertInstanceOf(Event.class, task);
        assertEquals("E | 0 | project meeting | 2025-12-03 1400 | 2025-12-03 1600", task.toFileString());
    }

    /**
     * Tests that event end times must be after start times.
     */
    @Test
    void parseTask_eventEndNotAfterStart_throwsFridayException() {
        assertThrows(FridayException.class, () -> Parser.parseTask("event",
                "project meeting /from 3/12/2025 1600 /to 3/12/2025 1400"));
        assertThrows(FridayException.class, () -> Parser.parseTask("event",
                "project meeting /from 3/12/2025 1400 /to 3/12/2025 1400"));
    }

    /**
     * Tests that recurring task information is restored from saved task lines.
     *
     * @throws FridayException if parsing fails unexpectedly
     */
    @Test
    void parseSavedTask_recurringDeadline_restoresRepeatFrequency() throws FridayException {
        Task task = Parser.parseSavedTask("D | 0 | submit report | 2025-12-02 1800 | repeat:weekly");

        assertEquals("D | 0 | submit report | 2025-12-02 1800 | repeat:weekly", task.toFileString());
        assertEquals("[D][ ] submit report (repeats: weekly) (by: Dec 02 2025 6:00pm)", task.toString());
    }

    /**
     * Tests that missing task details cause a Friday exception.
     */
    @Test
    void parseTask_missingRequiredDetails_throwsFridayException() {
        assertThrows(FridayException.class, () -> Parser.parseTask("todo", ""));
        assertThrows(FridayException.class, () -> Parser.parseTask("deadline", "return book"));
        assertThrows(FridayException.class, () -> Parser.parseTask("event", "project meeting /from 3/12/2025 1400"));
    }

    /**
     * Tests that find keywords are trimmed and cannot be blank.
     *
     * @throws FridayException if parsing fails unexpectedly
     */
    @Test
    void parseKeyword_validAndBlankInputs_returnsTrimmedKeywordOrThrows() throws FridayException {
        assertEquals("book", Parser.parseKeyword(" book "));
        assertThrows(FridayException.class, () -> Parser.parseKeyword(""));
        assertThrows(FridayException.class, () -> Parser.parseKeyword("   "));
    }

    @Test
    void getCommandAndDetails_irregularWhitespace_returnsNormalizedParts() {
        assertEquals("deadline", Parser.getCommand("  deadline   return book /by 2/12/2025 1800  "));
        assertEquals("return book /by 2/12/2025 1800", Parser.getDetails(
                "  deadline   return book /by 2/12/2025 1800  "));
    }

    @Test
    void parseTask_repeatedOrEmptyMarkers_throwsFridayException() {
        assertThrows(FridayException.class, () -> Parser.parseTask(
                "deadline", "return book /by 2/12/2025 1800 /by 3/12/2025 1800"));
        assertThrows(FridayException.class, () -> Parser.parseTask("deadline", "return book /by"));
        assertThrows(FridayException.class, () -> Parser.parseTask(
                "event", "meeting /from 2/12/2025 1800 /to"));
    }

    @Test
    void parseSavedTask_invalidStatusOrExtraFields_throwsFridayException() {
        assertThrows(FridayException.class, () -> Parser.parseSavedTask("T | yes | read book"));
        assertThrows(FridayException.class, () -> Parser.parseSavedTask("T | 0 | read book | unexpected"));
        assertThrows(FridayException.class, () -> Parser.parseSavedTask("D | 0 | task | 2025-12-02 1800 | extra"));
    }
}
