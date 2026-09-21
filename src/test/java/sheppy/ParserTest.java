package sheppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sheppy.task.Deadline;
import sheppy.task.Event;
import sheppy.task.Task;
import sheppy.task.Todo;

/** Tests Sheppy's command and task parsing behavior. */
class ParserTest {
    /** Checks that recognized commands are mapped to their command types. */
    @Test
    void parseCommand_recognizesSupportedCommands() {
        assertEquals(CommandType.TODO, Parser.parseCommand("todo read book"));
        assertEquals(CommandType.DEADLINE, Parser.parseCommand("deadline report /by 2025-01-01"));
        assertEquals(CommandType.EVENT, Parser.parseCommand("event meeting /from 2pm /to 4pm"));
        assertEquals(CommandType.FIND, Parser.parseCommand("find book"));
        assertEquals(CommandType.SORT, Parser.parseCommand("sort"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommand("schedule meeting"));
    }

    /** Checks that the parser's non-null command assumption is documented. */
    @Test
    void parseCommand_nullCommandThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseCommand(null));
    }

    /** Checks that a todo command produces a Todo task. */
    @Test
    void parseTask_todoCommandCreatesTodo() throws SheppyException {
        Task task = Parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    /** Checks that deadline and event commands produce their task types. */
    @Test
    void parseTask_typedCommandsCreateTypedTasks() throws SheppyException {
        Task deadline = Parser.parseTask("deadline submit report /by 2019-10-15");
        Task event = Parser.parseTask("event project meeting /from Monday 2pm /to 4pm");

        assertInstanceOf(Deadline.class, deadline);
        assertEquals("[D][ ] submit report (by: Oct 15 2019)", deadline.toString());
        assertInstanceOf(Event.class, event);
        assertEquals("[E][ ] project meeting (from: Monday 2pm to: 4pm)", event.toString());
    }

    /** Checks that malformed deadline dates are rejected with SheppyException. */
    @Test
    void parseTask_invalidDeadlineDateThrowsSheppyException() {
        assertThrows(SheppyException.class,
                () -> Parser.parseTask("deadline report /by October 15"));
    }

    /** Checks that a find command requires a non-empty keyword. */
    @Test
    void parseFindKeyword_emptyKeywordThrowsSheppyException() {
        assertThrows(SheppyException.class, () -> Parser.parseFindKeyword("find"));
    }
}
