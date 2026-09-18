package mochi.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(strings = {"todo", "todo   ", "todo unsafe\ntext", "todo unsafe\ttext",
        "deadline report", "deadline /by 2026-09-18", "deadline report /by",
        "deadline report /by 2026-02-30", "deadline report /by 2025-02-29",
        "deadline report /by 2026-13-01", "deadline report /by 2026-9-1",
        "deadline report /by 2026-09-18 /by 2026-09-19", "deadline report/by 2026-09-18",
        "event meeting", "event meeting /from noon", "event meeting /to evening /from noon",
        "event /from noon /to evening", "event meeting /from /to evening", "event meeting /from noon /to",
        "event meeting /from noon /from lunch /to evening", "event meeting /from noon /to evening /to night",
        "event meeting /from noon | extra /to evening", "event meeting /from noon /to evening\nextra",
        "event meeting /from noon /today evening", "event meeting /from noon /to noon",
        "event meeting /from 2026-09-19 /to 2026-09-18", "event meeting /from 2026-09-18 /to 2026-09-18",
        "event meeting /from 2026-02-30 /to 2026-03-01", "event meeting /from 2026-09-18 /to 2026-09-31"})
    void parseTask_invalidFields_rejectWithoutRuntimeException(String input) {
        assertThrows(MochiException.class, () -> Parser.parseTask(input, Parser.parseCommand(input)));
    }

    @Test
    void parseTask_whitespaceAndLeapDay_preservesValidDetails() throws MochiException {
        assertEquals("[D][ ] report (by: Feb 29 2028)",
                Parser.parseTask("  deadline\t report  /by\t2028-02-29  ", Command.DEADLINE).toString());
        assertEquals("[E][ ] trip (from: 2026-09-18 to: 2026-09-19)",
                Parser.parseTask("event trip /from 2026-09-18 /to 2026-09-19", Command.EVENT).toString());
        assertEquals("[E][ ] read /today notes (from: noon to: evening)",
                Parser.parseTask("event read /today notes /from noon /to evening", Command.EVENT).toString());
        assertThrows(MochiException.class, () -> Parser.parseTask("list", Command.LIST));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark", "mark two", "mark 0", "mark -1", "mark 4", "mark 1 2",
        "mark 2147483648", "mark -2147483648", "mark 1.5"})
    void parseTaskIndex_invalidNumber_rejectsInput(String input) {
        assertThrows(MochiException.class, () -> Parser.parseTaskIndex(input, Command.MARK, 3));
    }

    @Test
    void parseTaskIndex_boundariesAndWhitespace_returnZeroBasedIndex() throws MochiException {
        assertEquals(0, Parser.parseTaskIndex("  delete\t1  ", Command.DELETE, 3));
        assertEquals(2, Parser.parseTaskIndex("unmark 3", Command.UNMARK, 3));
        assertThrows(MochiException.class, () -> Parser.parseTaskIndex("mark 1", Command.MARK, 0));
        assertEquals("read book", Parser.parseFindKeyword("  find   read book  "));
    }
}
