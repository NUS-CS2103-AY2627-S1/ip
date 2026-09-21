package melodie.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import melodie.MelodieException;
import melodie.task.Deadline;
import melodie.task.Event;
import melodie.task.Task;
import melodie.task.Todo;

/**
 * Tests parsing and applying field-specific task updates.
 */
public class UpdateParserTest {
    private static final Clock WEDNESDAY_CLOCK =
            Clock.fixed(Instant.parse("2026-09-09T12:00:00Z"), ZoneOffset.UTC);

    @Test
    public void parseUpdate_validArguments_returnsParsedUpdate() throws MelodieException {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        ParsedCommand command = parser.parse("update 2 /description submit final report");

        ParsedUpdate update = parser.parseUpdate(command.getArguments());

        assertEquals(Command.UPDATE, command.getCommand());
        assertEquals(1, update.getTaskIndex());
        assertEquals(ParsedUpdate.Field.DESCRIPTION, update.getField());
        assertEquals("submit final report", update.getValue());
    }

    @Test
    public void parseUpdatedTask_description_preservesTypeDateAndStatus()
            throws MelodieException {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        LocalDateTime originalDueDate = LocalDateTime.of(2026, 12, 2, 18, 0);
        Deadline originalTask = new Deadline("submit draft", originalDueDate);
        originalTask.mark();
        ParsedUpdate update = parser.parseUpdate("1 /description submit final report");

        Task actualTask = parser.parseUpdatedTask(originalTask, update);

        Deadline actualDeadline = assertInstanceOf(Deadline.class, actualTask);
        assertEquals("submit final report", actualDeadline.getDescription());
        assertEquals(originalDueDate, actualDeadline.getDueDateTime());
        assertTrue(actualDeadline.isCompleted());
    }

    @Test
    public void parseUpdatedTask_deadlineDate_acceptsNaturalDate() throws MelodieException {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        Deadline originalTask = new Deadline(
                "submit report",
                LocalDateTime.of(2026, 12, 2, 18, 0));
        ParsedUpdate update = parser.parseUpdate("1 /by tomorrow 0900");

        Task actualTask = parser.parseUpdatedTask(originalTask, update);

        Deadline actualDeadline = assertInstanceOf(Deadline.class, actualTask);
        assertEquals("submit report", actualDeadline.getDescription());
        assertEquals(
                LocalDateTime.of(2026, 9, 10, 9, 0),
                actualDeadline.getDueDateTime());
    }

    @Test
    public void parseUpdatedTask_eventEnd_preservesOtherFields() throws MelodieException {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 14, 14, 0);
        Event originalTask = new Event(
                "project meeting",
                startDateTime,
                LocalDateTime.of(2026, 9, 14, 16, 0));
        ParsedUpdate update = parser.parseUpdate("1 /to Mon 1700");

        Task actualTask = parser.parseUpdatedTask(originalTask, update);

        Event actualEvent = assertInstanceOf(Event.class, actualTask);
        assertEquals("project meeting", actualEvent.getDescription());
        assertEquals(startDateTime, actualEvent.getStartDateTime());
        assertEquals(
                LocalDateTime.of(2026, 9, 14, 17, 0),
                actualEvent.getEndDateTime());
    }

    @Test
    public void parseUpdatedTask_fieldNotApplicable_throwsMelodieException()
            throws MelodieException {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        Todo originalTask = new Todo("read book");
        ParsedUpdate update = parser.parseUpdate("1 /by tomorrow 1800");

        MelodieException exception =
                assertThrows(MelodieException.class, () ->
                    parser.parseUpdatedTask(originalTask, update));

        assertEquals(
                "The /by field cannot be updated for this task type :(",
                exception.getMessage());
    }

    @Test
    public void parseUpdatedTask_eventEndBeforeStart_throwsMelodieException()
            throws MelodieException {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        Event originalTask = new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 14, 14, 0),
                LocalDateTime.of(2026, 9, 14, 16, 0));
        ParsedUpdate update = parser.parseUpdate("1 /to today 1800");

        MelodieException exception =
                assertThrows(MelodieException.class, () ->
                    parser.parseUpdatedTask(originalTask, update));

        assertEquals("The event cannot end before it starts :(", exception.getMessage());
    }

    @Test
    public void parseUpdate_missingValue_throwsMelodieException() {
        Parser parser = new Parser(WEDNESDAY_CLOCK);

        MelodieException exception =
                assertThrows(MelodieException.class, () ->
                    parser.parseUpdate("1 /description"));

        assertEquals("Please enter a valid update command :(\n"
                + "    Format: update <task number> "
                + "</description|/by|/from|/to> <new value>",
                exception.getMessage());
    }
}
