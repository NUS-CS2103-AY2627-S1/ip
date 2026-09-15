package larp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests command parsing and validation performed by {@link Parser}.
 */
public class ParserTest {
    @Test
    public void parseFindKeyword_validKeyword_returnsKeyword() throws LarpException {
        assertEquals("read book", Parser.parseFindKeyword("find read book"));
        assertEquals("book", Parser.parseFindKeyword("find   book   "));
    }

    @Test
    public void parseFindKeyword_missingKeyword_throwsLarpException() {
        assertThrows(LarpException.class, () -> Parser.parseFindKeyword("find"));
        assertThrows(LarpException.class, () -> Parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseFindKeyword_reservedStorageCharacter_throwsLarpException() {
        assertThrows(LarpException.class, () -> Parser.parseFindKeyword("find milk | bread"));
    }

    @Test
    public void parseTask_todoCommand_returnsTodo() throws LarpException {
        Task task = Parser.parseTask("todo read book");

        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    public void parseTask_deadlineCommand_returnsDeadline() throws LarpException {
        Task task = Parser.parseTask("deadline submit report /by 2026-08-31");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 31), deadline.getBy());
    }

    @Test
    public void parseTask_deadlineWithFlexibleSpacing_returnsDeadline() throws LarpException {
        Task task = Parser.parseTask("deadline submit report   /by   2026-08-31");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 31), deadline.getBy());
    }

    @Test
    public void parseTask_eventCommand_returnsEvent() throws LarpException {
        Task task = Parser.parseTask("event project meeting /from 2pm /to 3pm");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("3pm", event.getTo());
    }

    @Test
    public void parseTask_missingTodoDescription_throwsLarpException() {
        assertThrows(LarpException.class, () -> Parser.parseTask("todo"));
        assertThrows(LarpException.class, () -> Parser.parseTask("todo   "));
    }

    @Test
    public void parseTask_invalidDeadlineDate_throwsLarpException() {
        assertThrows(LarpException.class,
                () -> Parser.parseTask("deadline submit report /by 2026-02-30"));
    }

    @Test
    public void parseTask_incompleteEvent_throwsLarpException() {
        assertThrows(LarpException.class,
                () -> Parser.parseTask("event project meeting /from 2pm"));
        assertThrows(LarpException.class,
                () -> Parser.parseTask("event project meeting /from 2pm /to "));
    }

    @Test
    public void parseTask_reservedStorageCharacter_throwsLarpException() {
        assertThrows(LarpException.class, () -> Parser.parseTask("todo buy milk | bread"));
        assertThrows(LarpException.class,
                () -> Parser.parseTask("event demo /from 2pm | online /to 3pm"));
    }

    @Test
    public void parseTask_unknownCommand_throwsLarpException() {
        assertThrows(LarpException.class, () -> Parser.parseTask("remind read book"));
    }

    @Test
    public void parseTaskIndex_validTaskNumber_returnsZeroBasedIndex() throws LarpException {
        assertEquals(0, Parser.parseTaskIndex("mark 1", "mark", 3));
        assertEquals(2, Parser.parseTaskIndex("delete 3", "delete", 3));
    }

    @Test
    public void parseTaskIndex_invalidTaskNumber_throwsLarpException() {
        assertThrows(LarpException.class,
                () -> Parser.parseTaskIndex("mark", "mark", 3));
        assertThrows(LarpException.class,
                () -> Parser.parseTaskIndex("mark first", "mark", 3));
        assertThrows(LarpException.class,
                () -> Parser.parseTaskIndex("mark 1 later", "mark", 3));
        assertThrows(LarpException.class,
                () -> Parser.parseTaskIndex("mark 0", "mark", 3));
        assertThrows(LarpException.class,
                () -> Parser.parseTaskIndex("mark 4", "mark", 3));
    }
}
