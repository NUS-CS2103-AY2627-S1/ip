package esther.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import esther.exception.EstherException;
import esther.task.Deadline;
import esther.task.Event;
import esther.task.Task;
import esther.task.Todo;

/**
 * Tests command-to-task parsing behavior.
 */
public class ParserTest {
    @Test
    public void parseTaskIndex_negativeTaskCount_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTaskIndex("mark 1", "mark", -1));
    }

    @Test
    public void parseTask_validTodo_returnsTodo() throws EstherException {
        Task task = Parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("T | 0 | read book", task.toDataString());
    }

    @Test
    public void parseTask_validDeadline_returnsDeadlineWithParsedDate()
            throws EstherException {
        Task task = Parser.parseTask(
                "deadline return book /by 2026-08-30");

        assertInstanceOf(Deadline.class, task);
        assertEquals(
                "D | 0 | return book | 2026-08-30",
                task.toDataString());
    }

    @Test
    public void parseTask_validEvent_returnsEvent() throws EstherException {
        Task task = Parser.parseTask(
                "event project meeting /from 2pm /to 4pm");

        assertInstanceOf(Event.class, task);
        assertEquals(
                "E | 0 | project meeting | 2pm | 4pm",
                task.toDataString());
    }

    @Test
    public void parseTask_todoWithoutDescription_throwsException() {
        assertThrows(EstherException.class, () -> Parser.parseTask("todo"));
    }

    @Test
    public void parseTask_deadlineWithoutByMarker_throwsException() {
        assertThrows(EstherException.class, () -> Parser.parseTask(
                "deadline return book 2026-08-30"));
    }

    @Test
    public void parseTask_deadlineWithInvalidDate_throwsException() {
        assertThrows(EstherException.class, () -> Parser.parseTask(
                "deadline return book /by 30-08-2026"));
    }

    @Test
    public void parseTask_deadlineWithRepeatedByMarker_throwsClearException() {
        EstherException exception = assertThrows(
                EstherException.class, () -> Parser.parseTask(
                        "deadline return book /by 2026-08-30 /by 2026-09-01"));

        assertEquals(
                "One deadline, one /by. You gave me too many!",
                exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithMarkerPrefixInDescription_returnsDeadline()
            throws EstherException {
        Task task = Parser.parseTask(
                "deadline visit /bypass archive /by 2026-09-30");

        assertEquals(
                "D | 0 | visit /bypass archive | 2026-09-30",
                task.toDataString());
    }

    @Test
    public void parseTask_eventWithoutEnd_throwsException() {
        assertThrows(EstherException.class, () -> Parser.parseTask(
                "event project meeting /from 2pm"));
    }

    @Test
    public void parseTask_eventWithRepeatedFromMarker_throwsClearException() {
        EstherException exception = assertThrows(
                EstherException.class, () -> Parser.parseTask(
                        "event meeting /from Monday /from Tuesday /to Friday"));

        assertEquals(
                "I only need one /from. I cant time-travel bestie!",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithRepeatedToMarker_throwsClearException() {
        EstherException exception = assertThrows(
                EstherException.class, () -> Parser.parseTask(
                        "event meeting /from Monday /to Friday /to Saturday"));

        assertEquals("One /to is enough, promise!", exception.getMessage());
    }

    @Test
    public void parseTask_eventWithMarkerPrefixInDescription_returnsEvent()
            throws EstherException {
        Task task = Parser.parseTask(
                "event check /today plan /from Monday /to Friday");

        assertEquals(
                "E | 0 | check /today plan | Monday | Friday",
                task.toDataString());
    }

    @Test
    public void parseTask_eventWithSameStartAndEndDate_throwsException() {
        EstherException exception = assertThrows(
                EstherException.class, () -> Parser.parseTask(
                        "event holiday /from 2026-09-12 /to 2026-09-12"));

        assertEquals(
                "Bestie, this event must end after it starts. "
                        + "I'm organised, not a time traveller!",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithEndDateBeforeStartDate_throwsException() {
        assertThrows(EstherException.class, () -> Parser.parseTask(
                "event holiday /from 2026-09-13 /to 2026-09-12"));
    }

    @Test
    public void parseTask_unknownCommand_throwsException() {
        EstherException exception = assertThrows(
                EstherException.class, () -> Parser.parseTask("remind read book"));

        assertEquals(
                "Bestie, you're not speaking my language.",
                exception.getMessage());
    }

    @Test
    public void parseFindKeyword_validCommand_returnsKeyword()
            throws EstherException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    public void parseFindKeyword_withoutKeyword_throwsException() {
        assertThrows(EstherException.class, () -> Parser.parseFindKeyword("find"));
    }
}
