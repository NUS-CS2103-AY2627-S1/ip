package bingus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import bingus.command.Command;
import bingus.command.CommandType;
import bingus.command.ExitCommand;
import bingus.command.FindCommand;
import bingus.command.ListCommand;
import bingus.exception.BingusException;
import bingus.task.TaskList;
import bingus.task.Todo;

/**
 * Tests command routing and validation performed by {@link Parser}.
 */
class ParserTest {
    /**
     * Verifies that a bye command is parsed as an exit command.
     */
    @Test
    void parseBye_returnsExitCommand() {
        Parser parser = new Parser();

        Command command = parser.parse("bye", new TaskList());

        assertInstanceOf(ExitCommand.class, command);
        assertEquals(CommandType.EXIT, command.getType());
    }

    /**
     * Verifies that a valid date-filtered list command is parsed as a list command.
     */
    @Test
    void parseList_validDate_returnsListCommand() {
        Parser parser = new Parser();

        Command command = parser.parse("list 2026-09-20", new TaskList());

        assertInstanceOf(ListCommand.class, command);
        assertEquals(CommandType.LIST, command.getType());
    }

    @Test
    void parseFind_validKeyword_returnsFindCommand() {
        Parser parser = new Parser();

        assertInstanceOf(FindCommand.class, parser.parse("find book", new TaskList()));
    }

    @Test
    void parseFind_missingKeyword_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parse("find", new TaskList()));
    }

    @Test
    void parseBye_extraArgument_throwsHelpfulException() {
        Parser parser = new Parser();

        BingusException exception = assertThrows(
                BingusException.class, () -> parser.parse("bye later", new TaskList()));

        assertEquals("Bingus cannot use extra details with `bye`.\nTry `bye`.", exception.getMessage());
    }

    @Test
    void parseMark_multipleTaskIds_throwsHelpfulException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write tests")));

        BingusException exception = assertThrows(
                BingusException.class, () -> parser.parse("mark 1 2", tasks));

        assertEquals("I can only mark one task at a time.\nTry `mark [TASK_NUMBER]`.", exception.getMessage());
    }

    /**
     * Verifies that commands requiring a task number report their command-specific guidance.
     */
    @Test
    void parseTaskCommands_missingTaskId_throwsHelpfulException() {
        Parser parser = new Parser();

        BingusException markException = assertThrows(
                BingusException.class, () -> parser.parse("mark", new TaskList()));
        BingusException unmarkException = assertThrows(
                BingusException.class, () -> parser.parse("unmark", new TaskList()));
        BingusException deleteException = assertThrows(
                BingusException.class, () -> parser.parse("delete", new TaskList()));

        assertEquals("Which task should I mark?\nTry `mark [TASK_NUMBER]`.", markException.getMessage());
        assertEquals("Which task should I unmark?\nTry `unmark [TASK_NUMBER]`.", unmarkException.getMessage());
        assertEquals("Which task should I remove?\nTry `delete [TASK_NUMBER]`.", deleteException.getMessage());
    }

    @Test
    void parseUnmark_multipleTaskIds_throwsHelpfulException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write tests")));

        BingusException exception = assertThrows(
                BingusException.class, () -> parser.parse("unmark 1 2", tasks));

        assertEquals("I can only unmark one task at a time.\nTry `unmark [TASK_NUMBER]`.", exception.getMessage());
    }

    @Test
    void parseDelete_multipleTaskIds_throwsHelpfulException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write tests")));

        BingusException exception = assertThrows(
                BingusException.class, () -> parser.parse("delete 1 2", tasks));

        assertEquals("I can only remove one task at a time.\nTry `delete [TASK_NUMBER]`.", exception.getMessage());
    }

    @Test
    void parseList_multipleDates_throwsHelpfulException() {
        Parser parser = new Parser();

        BingusException exception = assertThrows(
                BingusException.class, () -> parser.parse("list 2026-09-20 2026-09-21", new TaskList()));

        assertEquals("I can only use one date to filter the list.\n"
                + "Try `list [yyyy-MM-dd]`.", exception.getMessage());
    }
}
