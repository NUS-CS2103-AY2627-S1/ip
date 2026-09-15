package monkey.parser;

import org.junit.jupiter.api.Test;

import monkey.command.AddCommand;
import monkey.command.Command;
import monkey.command.DeleteCommand;
import monkey.command.ExitCommand;
import monkey.command.FindCommand;
import monkey.command.ListCommand;
import monkey.command.MarkCommand;
import monkey.command.SnoozeCommand;
import monkey.command.TodoCommand;
import monkey.command.UnknownCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseAction_supportedCommand_createsMatchingAction() {
        assertInstanceOf(ExitCommand.class, parser.parseAction("bye"));
        assertInstanceOf(DeleteCommand.class, parser.parseAction("delete 1"));
        assertInstanceOf(TodoCommand.class, parser.parseAction("todo read"));
        assertInstanceOf(ListCommand.class, parser.parseAction("list"));
        assertInstanceOf(FindCommand.class, parser.parseAction("find read"));
        assertInstanceOf(MarkCommand.class, parser.parseAction("mark 1"));
        assertInstanceOf(MarkCommand.class, parser.parseAction("unmark 1"));
        assertInstanceOf(AddCommand.class, parser.parseAction("event class /from 2pm /to 3pm"));
        assertInstanceOf(AddCommand.class, parser.parseAction("deadline submit /by 2099-01-01"));
        assertInstanceOf(SnoozeCommand.class, parser.parseAction("snooze 1 2099-02-01"));
    }

    @Test
    void parseAction_unknownOrInvalidNoArgumentCommand_createsUnknownAction() {
        assertInstanceOf(UnknownCommand.class, parser.parseAction("dance"));
        assertInstanceOf(UnknownCommand.class, parser.parseAction("list now"));
        assertInstanceOf(UnknownCommand.class, parser.parseAction("bye now"));
    }

    @Test
    void parse_whitespaceAroundCommand_normalizesArguments() {
        ParsedCommand parsed = parser.parse("  todo   buy    milk  ");

        assertEquals(Command.TODO, parsed.command());
        assertEquals("buy milk", parsed.arguments());
    }

    @Test
    void parseArguments_nullInput_returnsEmptyArguments() {
        assertEquals("", parser.parseArguments(null, Command.TODO));
    }

    @Test
    void parseCommand_nullAndUnknownInput_returnsUnknown() {
        assertEquals(Command.UNKNOWN, parser.parseCommand(null));
        assertEquals(Command.UNKNOWN, parser.parseCommand("deadline-ish task"));
    }

    @Test
    void commandKeyword_knownAndUnknownCommands_returnsConfiguredKeyword() {
        assertEquals("todo", Command.TODO.getKeyword());
        assertEquals("", Command.UNKNOWN.getKeyword());
        assertEquals(Command.MARK, Command.fromInput("  mark\t1"));
    }
}
