package mochi.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import mochi.command.Command;
import mochi.exception.MochiException;
import mochi.task.Deadline;
import mochi.task.Event;
import mochi.task.Task;
import mochi.task.Todo;

class ParserTest {
    @Test
    void parseTask_validCommands_createCorrectTaskTypes() throws MochiException {
        Task todo = Parser.parseTask("todo borrow book", Command.TODO);
        Task deadline = Parser.parseTask("deadline submit report /by 2026-08-30", Command.DEADLINE);
        Task event = Parser.parseTask("event meeting /from 2pm /to 4pm", Command.EVENT);

        assertInstanceOf(Todo.class, todo);
        assertEquals("[T][ ] borrow book", todo.toString());
        assertInstanceOf(Deadline.class, deadline);
        assertEquals("[D][ ] submit report (by: Aug 30 2026)", deadline.toString());
        assertInstanceOf(Event.class, event);
        assertEquals("[E][ ] meeting (from: 2pm to: 4pm)", event.toString());
    }

    @Test
    void parseTask_invalidCommands_throwSpecificErrors() {
        assertThrows(MochiException.class, () -> Parser.parseTask("todo", Command.TODO));
        assertThrows(MochiException.class,
                () -> Parser.parseTask("deadline report /by Friday", Command.DEADLINE));
        assertThrows(MochiException.class,
                () -> Parser.parseTask("event meeting /from 2pm", Command.EVENT));
    }

    @Test
    void parseTaskIndex_validAndInvalidNumbers_behaveCorrectly() throws MochiException {
        assertEquals(1, Parser.parseTaskIndex("mark 2", Command.MARK, 3));
        assertThrows(MochiException.class, () -> Parser.parseTaskIndex("mark", Command.MARK, 3));
        assertThrows(MochiException.class, () -> Parser.parseTaskIndex("mark two", Command.MARK, 3));
        assertThrows(MochiException.class, () -> Parser.parseTaskIndex("mark 4", Command.MARK, 3));
    }

    @Test
    void parseFindKeyword_presentAndMissingKeyword_behaveCorrectly() throws MochiException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
        assertThrows(MochiException.class, () -> Parser.parseFindKeyword("find"));
    }

    @Test
    void parseCommand_extraWhitespace_stillRecognisesCommand() {
        assertEquals(Command.TODO, Parser.parseCommand("  todo read book"));
        assertEquals(Command.LIST, Parser.parseCommand("list\t"));
        assertEquals(Command.UNKNOWN, Parser.parseCommand("   "));
    }

    @Test
    void parseTask_duplicateMarkersAndUnsafeDescription_throwActionableErrors() {
        MochiException duplicateDeadline = assertThrows(MochiException.class,
                () -> Parser.parseTask("deadline report /by 2026-08-30 /by 2026-09-01", Command.DEADLINE));
        MochiException duplicateEvent = assertThrows(MochiException.class,
                () -> Parser.parseTask("event meeting /from 2pm /to 4pm /to 5pm", Command.EVENT));
        MochiException unsafeDescription = assertThrows(MochiException.class,
                () -> Parser.parseTask("todo save | backup", Command.TODO));

        assertEquals("A deadline must contain '/by' only once.", duplicateDeadline.getMessage());
        assertEquals("An event must contain '/from' and '/to' only once each.", duplicateEvent.getMessage());
        assertEquals("Task descriptions cannot contain the '|' character.", unsafeDescription.getMessage());
    }
}
