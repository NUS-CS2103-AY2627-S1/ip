package anby;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import anby.task.Todo;

public class ParserTest {
    @Test
    public void parseCommand_fullCommand_returnsCommand() {
        Parser parser = new Parser();

        assertEquals(Command.TODO, parser.parseCommand("todo"));
        assertEquals(Command.LIST, parser.parseCommand("list"));
        assertEquals(Command.MARK, parser.parseCommand("mark"));
        assertEquals(Command.UNMARK, parser.parseCommand("unmark"));
        assertEquals(Command.DELETE, parser.parseCommand("delete"));
        assertEquals(Command.BYE, parser.parseCommand("bye"));
        assertEquals(Command.DEADLINE, parser.parseCommand("deadline"));
        assertEquals(Command.EVENT, parser.parseCommand("event"));
        assertEquals(Command.FIND, parser.parseCommand("find"));
        assertEquals(Command.BURGER, parser.parseCommand("burger"));
    }

    @Test
    public void parseCommand_alias_returnsCommand() {
        Parser parser = new Parser();

        assertEquals(Command.LIST, parser.parseCommand("ls"));
        assertEquals(Command.MARK, parser.parseCommand("m"));
        assertEquals(Command.UNMARK, parser.parseCommand("um"));
        assertEquals(Command.DELETE, parser.parseCommand("del"));
        assertEquals(Command.BYE, parser.parseCommand("q"));
        assertEquals(Command.TODO, parser.parseCommand("t"));
        assertEquals(Command.DEADLINE, parser.parseCommand("d"));
        assertEquals(Command.EVENT, parser.parseCommand("e"));
        assertEquals(Command.FIND, parser.parseCommand("f"));
    }

    @Test
    public void parseCommand_upperCaseCommand_returnsCommand() {
        Parser parser = new Parser();

        assertEquals(Command.TODO, parser.parseCommand("TODO"));
    }

    @Test
    public void parseCommand_unknownCommand_throwsIllegalArgumentException() {
        Parser parser = new Parser();

        assertThrows(IllegalArgumentException.class, () -> parser.parseCommand("sandwich"));
    }

    @Test
    public void splitInput_commandWithArguments_returnsCommandAndArguments() {
        Parser parser = new Parser();

        assertArrayEquals(new String[] {"todo", "eat burger now"}, parser.splitInput("todo eat burger now"));
    }

    @Test
    public void parseEvent_validEvent_returnsEventParts() throws AnbyException {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("event eat burgers for 3 meals /from 2010-01-01 /to 2026-08-28");

        assertArrayEquals(new String[] {"eat burgers for 3 meals", "2010-01-01", "2026-08-28"},
                parser.parseEvent(parts));
    }

    @Test
    public void parseEvent_missingFrom_throwsAnbyException() {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("event eat burgers /to 2026-08-28");

        assertThrows(AnbyException.class, () -> parser.parseEvent(parts));
    }

    @Test
    public void parseEvent_missingTo_throwsAnbyException() {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("event eat burgers /from 2010-01-01");

        assertThrows(AnbyException.class, () -> parser.parseEvent(parts));
    }

    @Test
    public void parseDeadline_validDeadline_returnsDeadlineParts() throws AnbyException {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("deadline eat burger /by 2026-09-18");

        assertArrayEquals(new String[] {"eat burger", "2026-09-18"}, parser.parseDeadline(parts));
    }

    @Test
    public void parseDeadline_missingBy_throwsAnbyException() {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("deadline eat burger");

        assertThrows(AnbyException.class, () -> parser.parseDeadline(parts));
    }

    @Test
    public void parseTodo_validTodo_returnsTrimmedDescription() throws AnbyException {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("todo   eat burger   ");

        assertEquals("eat burger", parser.parseTodo(parts));
    }

    @Test
    public void parseTodo_missingDescription_throwsAnbyException() {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("todo");

        assertThrows(AnbyException.class, () -> parser.parseTodo(parts));
    }

    @Test
    public void parseFind_validKeyword_returnsTrimmedKeyword() throws AnbyException {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("find   burger   ");

        assertEquals("burger", parser.parseFind(parts));
    }

    @Test
    public void parseFind_missingKeyword_throwsAnbyException() {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("find");

        assertThrows(AnbyException.class, () -> parser.parseFind(parts));
    }

    @Test
    public void parseTaskNumber_validTaskNumber_returnsTaskNumber() throws AnbyException {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("mark 1");

        assertEquals("1", parser.parseTaskNumber(parts, "missing"));
    }

    @Test
    public void parseTaskNumber_missingTaskNumber_throwsAnbyException() {
        Parser parser = new Parser();
        String[] parts = parser.splitInput("mark");

        assertThrows(AnbyException.class, () -> parser.parseTaskNumber(parts, "missing"));
    }

    @Test
    public void add_multipleTasks_addsAllTasks() {
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("eat burger"), new Todo("drink tea"));

        assertEquals(2, tasks.size());
    }
}
