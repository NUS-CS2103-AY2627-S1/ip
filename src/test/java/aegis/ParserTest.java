package aegis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import aegis.task.Deadline;
import aegis.task.Event;
import aegis.task.Task;
import aegis.task.Todo;

public class ParserTest {

    @Test
    public void parseTodo_validDescription_returnsTodo() throws AegisException {
        Command command = new Parser().parse("todo borrow book");
        assertEquals("todo", command.getCommandName());
        Task task = command.getTask();
        assertInstanceOf(Todo.class, task);
        assertEquals("borrow book", task.getDescription());
    }

    @Test
    public void parseTodo_mixedCaseCommand_returnsTodo() throws AegisException {
        Command command = new Parser().parse("Todo borrow book");
        assertEquals("todo", command.getCommandName());
        assertInstanceOf(Todo.class, command.getTask());
    }

    @Test
    public void parseTodo_emptyDescription_throwsAegisException() {
        AegisException e = assertThrows(AegisException.class, () ->
                new Parser().parse("todo   "));
        assertEquals("A todo needs a tiny bit of description magic.", e.getMessage());
    }

    @Test
    public void parseTodo_leadingAndMultipleSpaces_returnsTrimmedTodo() throws AegisException {
        Command command = new Parser().parse("   todo    borrow    book   ");
        Task task = command.getTask();
        assertInstanceOf(Todo.class, task);
        assertEquals("borrow book", task.getDescription());
    }

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws AegisException {
        Command command = new Parser().parse("deadline return book /by 2026-08-22");
        Task task = command.getTask();
        assertInstanceOf(Deadline.class, task);
        assertTrue(task.toString().contains("return book"));
    }

    @Test
    public void parseDeadline_invalidDate_throwsAegisException() {
        AegisException e = assertThrows(AegisException.class, () ->
                new Parser().parse("deadline return book /by 22-08-2026"));
        assertEquals("Dates need the YYYY-MM-DD disguise.", e.getMessage());
    }

    @Test
    public void parseEvent_validInput_returnsEvent() throws AegisException {
        Command command = new Parser().parse("event meeting /from 2026-08-22 /to 2026-08-23");
        Task task = command.getTask();
        assertInstanceOf(Event.class, task);
        assertTrue(task.toString().contains("meeting"));
    }

    @Test
    public void parseEvent_invalidDate_throwsAegisException() {
        AegisException e = assertThrows(AegisException.class, () ->
                new Parser().parse("event meeting /from 22-08-2026 /to 23-08-2026"));
        assertEquals("Dates need the YYYY-MM-DD disguise.", e.getMessage());
    }

    @Test
    public void parseEvent_missingDescription_throwsAegisException() {
        AegisException e = assertThrows(AegisException.class, () ->
                new Parser().parse("event /from 2026-08-22 /to 2026-08-23"));
        assertEquals("An event needs a description!", e.getMessage());
    }

    @Test
    public void parseEvent_startDateSameAsEndDate_throwsAegisException() {
        AegisException e = assertThrows(AegisException.class, () ->
                new Parser().parse("event meeting /from 2026-08-22 /to 2026-08-22"));
        assertEquals("Event tasks need a /from date before the /to date.", e.getMessage());
    }

}
