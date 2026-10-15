package slotbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import slotbot.task.Deadline;
import slotbot.task.Event;
import slotbot.task.Task;
import slotbot.task.Todo;

public class ParserTest {

    @Test
    public void parseTaskNumber_validFirstTask_returnsZero() throws SlotBotException {
        assertEquals(0, Parser.parseTaskNumber("mark 1", 3));
    }

    @Test
    public void parseTaskNumber_validLastTask_returnsLastIndex() throws SlotBotException {
        assertEquals(2, Parser.parseTaskNumber("mark 3", 3));
    }

    @Test
    public void parseTaskNumber_surroundingWhitespace_returnsCorrectIndex() throws SlotBotException {
        assertEquals(1, Parser.parseTaskNumber("  mark   2  ", 3));
    }

    @Test
    public void parseTaskNumber_missingTaskNumber_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTaskNumber("mark", 3));
    }

    @Test
    public void parseTaskNumber_nonNumericTaskNumber_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTaskNumber("mark two", 3));
    }

    @Test
    public void parseTaskNumber_decimalTaskNumber_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTaskNumber("mark 1.5", 3));
    }

    @Test
    public void parseTaskNumber_zeroTaskNumber_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTaskNumber("mark 0", 3));
    }

    @Test
    public void parseTaskNumber_negativeTaskNumber_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTaskNumber("mark -1", 3));
    }

    @Test
    public void parseTaskNumber_tooLargeTaskNumber_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTaskNumber("mark 4", 3));
    }

    @Test
    public void parseTaskNumber_emptyTaskList_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTaskNumber("mark 1", 0));
    }

    @Test
    public void parseFindKeyword_validKeyword_returnsKeyword() throws SlotBotException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseFindKeyword("find"));
    }

    @Test
    public void parseFindKeyword_blankKeyword_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseCommandType_reminders_returnsReminders() {
        assertEquals(CommandType.REMINDERS, Parser.parseCommandType("reminders"));
    }

    @Test
    public void parseCommandType_surroundingWhitespaceAndArguments_returnsCommandType() {
        assertEquals(CommandType.TODO, Parser.parseCommandType("  todo   read book  "));
    }

    @Test
    public void parseCommandType_unknownCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("schedule meeting"));
    }

    @Test
    public void parseFindKeyword_multipleWords_returnsCompleteKeyword() throws SlotBotException {
        assertEquals("project meeting", Parser.parseFindKeyword("find project meeting"));
    }

    @Test
    public void parseTask_validTodo_returnsTodo() throws SlotBotException {
        Task task = Parser.parseTask("todo read book", CommandType.TODO);

        assertEquals(Todo.class, task.getClass());
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void parseTask_validDeadline_returnsDeadline() throws SlotBotException {
        Deadline deadline = (Deadline) Parser.parseTask(
                "deadline submit report /by 2026-09-18", CommandType.DEADLINE);

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 18), deadline.getDate());
    }

    @Test
    public void parseTask_validEvent_returnsEvent() throws SlotBotException {
        Event event = (Event) Parser.parseTask(
                "event meeting /from 2026-09-18 10:00 /to 2026-09-18 11:30",
                CommandType.EVENT);

        assertEquals("meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 18, 10, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 18, 11, 30), event.getTo());
    }

    @Test
    public void parseTask_missingDescriptions_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask("todo", CommandType.TODO));
        assertThrows(SlotBotException.class, () -> Parser.parseTask("deadline", CommandType.DEADLINE));
        assertThrows(SlotBotException.class, () -> Parser.parseTask("event", CommandType.EVENT));
    }

    @Test
    public void parseTask_missingDeadlineFields_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "deadline submit report", CommandType.DEADLINE));
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "deadline /by 2026-09-18", CommandType.DEADLINE));
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "deadline submit /by", CommandType.DEADLINE));
    }

    @Test
    public void parseTask_invalidDeadlineDate_exceptionThrown() {
        SlotBotException exception = assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "deadline submit /by 2026-02-30", CommandType.DEADLINE));

        assertEquals("The deadline date is invalid.\n"
                + "Use: deadline DESCRIPTION /by yyyy-MM-dd", exception.getMessage());
    }

    @Test
    public void parseTask_missingEventFields_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask("event meeting", CommandType.EVENT));
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "event meeting /from 2026-09-18 10:00", CommandType.EVENT));
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "event /from 2026-09-18 10:00 /to 2026-09-18 11:00", CommandType.EVENT));
    }

    @Test
    public void parseTask_invalidEventDate_exceptionThrown() {
        SlotBotException exception = assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "event meeting /from 2026-29-21 10:00 /to 2026-09-21 11:00",
                CommandType.EVENT));

        assertEquals("The event date or time is invalid.\n"
                + "Use: event DESCRIPTION /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm",
                exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithExtraSeparator_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "deadline submit /by 2026-09-18 /by 2026-09-19", CommandType.DEADLINE));
    }

    @Test
    public void parseTask_deadlineWithExtraSpacing_returnsDeadline() throws SlotBotException {
        Deadline deadline = (Deadline) Parser.parseTask(
                "deadline submit   /by   2026-09-18", CommandType.DEADLINE);

        assertEquals("submit", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 18), deadline.getDate());
    }

    @Test
    public void parseTask_descriptionWithStorageSeparator_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "todo unsafe | description", CommandType.TODO));
    }

    @Test
    public void parseTask_eventWithExtraSeparator_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "event meeting /from 2026-09-18 10:00 /from 2026-09-18 11:00 "
                        + "/to 2026-09-18 12:00",
                CommandType.EVENT));
    }

    @Test
    public void parseTask_eventWithExtraEndSeparator_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "event meeting /from 2026-09-18 10:00 /to 2026-09-18 11:00 "
                        + "/to 2026-09-18 12:00",
                CommandType.EVENT));
    }

    @Test
    public void parseTask_eventWithEqualTimes_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "event meeting /from 2026-09-18 10:00 /to 2026-09-18 10:00",
                CommandType.EVENT));
    }

    @Test
    public void parseTask_eventWithReversedTimes_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask(
                "event meeting /from 2026-09-18 11:00 /to 2026-09-18 10:00",
                CommandType.EVENT));
    }

    @Test
    public void parseTask_nonTaskCommand_exceptionThrown() {
        assertThrows(SlotBotException.class, () -> Parser.parseTask("list", CommandType.LIST));
    }
}
