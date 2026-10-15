package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import james.exception.UserInputException;

/**
 * Unit tests for {@link Task}.
 */
public class TaskTest {

    // ==========================================
    // Constructor & Field Tests
    // ==========================================

    /**
     * Tests that a null description passed to the constructor defaults to an empty string.
     */
    @Test
    public void constructor_nullDescription_setsEmptyDescription() {
        Task task = new Task(null);
        assertEquals("", task.getDescription());
        assertFalse(task.isDone());
    }

    /**
     * Tests that leading and trailing whitespace in the description is trimmed.
     */
    @Test
    public void constructor_withWhitespace_trimsDescription() {
        Task task = new Task("  wash dishes  ");
        assertEquals("wash dishes", task.getDescription());
        assertFalse(task.isDone());
    }

    // ==========================================
    // Mark & Unmark Tests
    // ==========================================

    /**
     * Tests that markDone marks the task as completed and sets mark status icon to [X].
     */
    @Test
    public void markDone_unmarkedTask_marksTaskAsDone() {
        Task task = new Task("wash dishes");
        task.markDone();
        assertTrue(task.isDone());
        assertEquals("[X]", task.getMark());
    }

    /**
     * Tests that markNotDone unmarks a completed task and sets mark status icon to [ ].
     */
    @Test
    public void markNotDone_markedTask_marksTaskAsNotDone() {
        Task task = new Task("wash dishes");
        task.markDone();
        task.markNotDone();
        assertFalse(task.isDone());
        assertEquals("[ ]", task.getMark());
    }

    // ==========================================
    // Serialization (toFileString & toString) Tests
    // ==========================================

    /**
     * Tests that toFileString returns the standard format for an unmarked task.
     */
    @Test
    public void toFileString_unmarkedTask_returnsUnmarkedFormat() {
        Task task = new Task("wash dishes");
        assertEquals("0 | wash dishes", task.toFileString());
    }

    /**
     * Tests that toFileString returns the standard format with status 1 for a marked task.
     */
    @Test
    public void toFileString_markedTask_returnsMarkedFormat() {
        Task task = new Task("wash dishes");
        task.markDone();
        assertEquals("1 | wash dishes", task.toFileString());
    }

    /**
     * Tests that toString returns the display string with [ ] for an unmarked task.
     */
    @Test
    public void toString_unmarkedTask_returnsFormattedString() {
        Task task = new Task("wash dishes");
        assertEquals("[ ] wash dishes", task.toString());
    }

    /**
     * Tests that toString returns the display string with [X] for a marked task.
     */
    @Test
    public void toString_markedTask_returnsFormattedString() {
        Task task = new Task("wash dishes");
        task.markDone();
        assertEquals("[X] wash dishes", task.toString());
    }

    // ==========================================
    // Deserialization (fromFileString) Tests
    // ==========================================

    /**
     * Tests that fromFileString parses a valid unmarked ToDo serialized line.
     */
    @Test
    public void fromFileString_validTodoUnmarked_returnsUnmarkedToDo() throws UserInputException {
        Task task = Task.fromFileString("T | 0 | read book");
        assertInstanceOf(ToDo.class, task);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    /**
     * Tests that fromFileString parses a valid marked ToDo serialized line.
     */
    @Test
    public void fromFileString_validTodoMarked_returnsMarkedToDo() throws UserInputException {
        Task task = Task.fromFileString("T | 1 | read book");
        assertInstanceOf(ToDo.class, task);
        assertEquals("read book", task.getDescription());
        assertTrue(task.isDone());
    }

    /**
     * Tests that fromFileString parses a valid unmarked Deadline serialized line.
     */
    @Test
    public void fromFileString_validDeadlineUnmarked_returnsUnmarkedDeadline() throws UserInputException {
        Task task = Task.fromFileString("D | 0 | return book | 2026-06-06");
        assertInstanceOf(Deadline.class, task);
        Deadline deadline = (Deadline) task;
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.parse("2026-06-06"), deadline.getBy());
        assertFalse(deadline.isDone());
    }

    /**
     * Tests that fromFileString parses a valid marked Deadline serialized line.
     */
    @Test
    public void fromFileString_validDeadlineMarked_returnsMarkedDeadline() throws UserInputException {
        Task task = Task.fromFileString("D | 1 | return book | 2026-06-06");
        assertInstanceOf(Deadline.class, task);
        Deadline deadline = (Deadline) task;
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.parse("2026-06-06"), deadline.getBy());
        assertTrue(deadline.isDone());
    }

    /**
     * Tests that fromFileString parses a valid unmarked Event serialized line.
     */
    @Test
    public void fromFileString_validEventUnmarked_returnsUnmarkedEvent() throws UserInputException {
        Task task = Task.fromFileString("E | 0 | project meeting | 2026-08-06 | 2026-08-08");
        assertInstanceOf(Event.class, task);
        Event event = (Event) task;
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.parse("2026-08-06"), event.getFrom());
        assertEquals(LocalDate.parse("2026-08-08"), event.getTo());
        assertFalse(event.isDone());
    }

    /**
     * Tests that fromFileString parses a valid marked Event serialized line.
     */
    @Test
    public void fromFileString_validEventMarked_returnsMarkedEvent() throws UserInputException {
        Task task = Task.fromFileString("E | 1 | project meeting | 2026-08-06 | 2026-08-08");
        assertInstanceOf(Event.class, task);
        Event event = (Event) task;
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.parse("2026-08-06"), event.getFrom());
        assertEquals(LocalDate.parse("2026-08-08"), event.getTo());
        assertTrue(event.isDone());
    }

    /**
     * Tests that passing a null serialized string to fromFileString throws UserInputException.
     */
    @Test
    public void fromFileString_nullString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString(null));
    }

    /**
     * Tests that passing an empty serialized string to fromFileString throws UserInputException.
     */
    @Test
    public void fromFileString_emptyString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("   "));
    }

    /**
     * Tests that serialized strings with fewer than the minimum number of delimiter sections throw UserInputException.
     */
    @Test
    public void fromFileString_insufficientParts_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("T | 0"));
    }

    /**
     * Tests that an invalid done status (other than 0 or 1) throws UserInputException.
     */
    @Test
    public void fromFileString_invalidDoneStatus_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("T | 2 | read book"));
    }

    /**
     * Tests that an empty description field in a serialized todo throws UserInputException.
     */
    @Test
    public void fromFileString_emptyTodoDescription_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("T | 0 |   "));
    }

    /**
     * Tests that a serialized deadline missing its due date field throws UserInputException.
     */
    @Test
    public void fromFileString_corruptedDeadlineMissingFields_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("D | 0 | return book"));
    }

    /**
     * Tests that a serialized deadline with an invalid date string throws UserInputException.
     */
    @Test
    public void fromFileString_corruptedDeadlineInvalidDate_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("D | 0 | return book | invalid-date"));
    }

    /**
     * Tests that a serialized event missing date fields throws UserInputException.
     */
    @Test
    public void fromFileString_corruptedEventMissingFields_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("E | 0 | meeting | 2026-08-06"));
    }

    /**
     * Tests that a serialized event with invalid date format throws UserInputException.
     */
    @Test
    public void fromFileString_corruptedEventInvalidDate_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString(
                "E | 0 | meeting | 2026-08-06 | invalid-date"));
    }

    /**
     * Tests that an unrecognized task type indicator throws UserInputException.
     */
    @Test
    public void fromFileString_unknownTaskType_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("X | 0 | unknown"));
    }

    @Test
    public void fromFileString_todoWithDelimiter_preservesDescription() throws UserInputException {
        Task task = Task.fromFileString(" T | 1 |  read | discuss  ");

        assertInstanceOf(ToDo.class, task);
        assertEquals("read | discuss", task.getDescription());
        assertTrue(task.isDone());
    }

    @Test
    public void fromFileString_controlCharacters_rejectsAllTaskTypesBeforeTrimming() {
        for (String description : new String[]{"unsafe" + (char) 27 + "[2Jrecord", "unsafe\trecord",
            "unsafe" + (char) 0, "unsafe" + (char) 127, "unsafe" + (char) 133}) {
            for (String line : new String[]{"T | 0 | " + description,
                "D | 0 | " + description + " | 2026-09-20",
                "E | 0 | " + description + " | 2026-09-20 | 2026-09-21"}) {
                UserInputException exception = assertThrows(UserInputException.class,
                        () -> Task.fromFileString(line));
                assertEquals("Stored tasks cannot contain control characters.", exception.getMessage());
            }
        }
    }

    @Test
    public void fromFileString_deadlineWithBlankDescription_preservesErrorMessage() {
        UserInputException exception = assertThrows(UserInputException.class,
                () -> Task.fromFileString("D | 0 |   | 2026-06-06"));

        assertEquals("Deadline description cannot be empty in storage.", exception.getMessage());
    }

    @Test
    public void fromFileString_deadlineWithBlankDate_preservesErrorMessage() {
        UserInputException exception = assertThrows(UserInputException.class,
                () -> Task.fromFileString("D | 0 | return book |   "));

        assertEquals("Deadline date cannot be empty in storage.", exception.getMessage());
    }

    @Test
    public void fromFileString_eventWithBlankDescription_preservesErrorMessage() {
        UserInputException exception = assertThrows(UserInputException.class,
                () -> Task.fromFileString("E | 0 |   | 2026-08-06 | 2026-08-08"));

        assertEquals("Event description cannot be empty in storage.", exception.getMessage());
    }

    @Test
    public void fromFileString_eventWithEitherDateBlank_preservesErrorMessage() {
        String[] lines = {
            "E | 0 | meeting |   | 2026-08-08",
            "E | 0 | meeting | 2026-08-06 |   "
        };
        for (String line : lines) {
            UserInputException exception = assertThrows(UserInputException.class,
                    () -> Task.fromFileString(line));

            assertEquals("Event times cannot be empty in storage.", exception.getMessage());
        }
    }

    @Test
    public void fromFileString_eventWithInvalidStartDate_preservesErrorMessage() {
        UserInputException exception = assertThrows(UserInputException.class,
                () -> Task.fromFileString("E | 0 | meeting | invalid-date | 2026-08-08"));

        assertEquals("Corrupt event date in storage from:invalid-date to: 2026-08-08", exception.getMessage());
    }

    @Test
    public void fromFileString_invalidStatusAndUnknownType_reportsStatusFirst() {
        UserInputException exception = assertThrows(UserInputException.class,
                () -> Task.fromFileString("X | 2 | unknown"));

        assertEquals("Invalid completion status in storage: 2", exception.getMessage());
    }
}
