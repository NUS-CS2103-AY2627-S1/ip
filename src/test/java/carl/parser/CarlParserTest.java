package carl.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carl.commands.ByeCommand;
import carl.commands.DeadlineCommand;
import carl.commands.DeleteCommand;
import carl.commands.DueCommand;
import carl.commands.EventCommand;
import carl.commands.FindCommand;
import carl.commands.HelpCommand;
import carl.commands.ListCommand;
import carl.commands.MarkCommand;
import carl.commands.SortCommand;
import carl.commands.TodoCommand;
import carl.commands.UnmarkCommand;
import carl.exceptions.CarlCommandException;
import carl.exceptions.CarlUnknownCommandException;

public class CarlParserTest {
    private CarlParser parser;

    @BeforeEach
    public void setUp() {
        parser = new CarlParser();
    }

    @Test
    public void parseCommands_extraWhitespace_parsesCommand() throws Exception {
        assertInstanceOf(TodoCommand.class, parser.parseCommands("   todo    write   report   "));
    }

    @Test
    public void parseCommands_everySupportedCommand_returnsExpectedCommandType() throws Exception {
        assertInstanceOf(ByeCommand.class, parser.parseCommands("BYE"));
        assertInstanceOf(HelpCommand.class, parser.parseCommands("help"));
        assertInstanceOf(ListCommand.class, parser.parseCommands("list"));
        assertInstanceOf(MarkCommand.class, parser.parseCommands("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parseCommands("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parseCommands("delete 1"));
        assertInstanceOf(TodoCommand.class, parser.parseCommands("todo task"));
        assertInstanceOf(DeadlineCommand.class,
                parser.parseCommands("deadline task /by 2026-10-01 1800"));
        assertInstanceOf(EventCommand.class,
                parser.parseCommands("event task /from 2026-10-01 1800 /to 2026-10-01 1900"));
        assertInstanceOf(DueCommand.class, parser.parseCommands("due 2026-10-01"));
        assertInstanceOf(DueCommand.class, parser.parseCommands("due"));
        assertInstanceOf(FindCommand.class, parser.parseCommands("find task"));
        assertInstanceOf(SortCommand.class, parser.parseCommands("sort"));
    }

    @Test
    public void parseCommands_blankInput_throwsCommandException() {
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("   "));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands(null));
    }

    @Test
    public void parseCommands_unknownCommand_throwsUnknownCommandException() {
        assertThrows(CarlUnknownCommandException.class, () -> parser.parseCommands("launch"));
    }

    @Test
    public void parseCommands_argumentFreeCommandWithExtraArguments_throwsCommandException() {
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("list extra"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("help extra"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("bye now"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("sort descending"));
    }

    @Test
    public void parseCommands_repeatedDeadlineMarker_throwsCommandException() {
        assertThrows(CarlCommandException.class, () ->
                parser.parseCommands("deadline report /by 2026-10-01 1800 /by 2026-10-02 1800"));
    }

    @Test
    public void parseCommands_incompleteDeadline_throwsCommandException() {
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("deadline report"));
        assertThrows(CarlCommandException.class, () ->
                parser.parseCommands("deadline /by 2026-10-01 1800"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("deadline report /by"));
        assertThrows(CarlCommandException.class, () ->
                parser.parseCommands("deadline report/by 2026-10-01 1800"));
    }

    @Test
    public void parseCommands_eventWithEqualStartAndEnd_throwsCommandException() {
        assertThrows(CarlCommandException.class, () ->
                parser.parseCommands("event meeting /from 2026-10-01 1800 /to 2026-10-01 1800"));
    }

    @Test
    public void parseCommands_invalidEventMarkersOrRange_throwCommandException() {
        assertThrows(CarlCommandException.class, () ->
                parser.parseCommands("event task /from 2026-10-01 1900"));
        assertThrows(CarlCommandException.class, () ->
                parser.parseCommands("event task /to 2026-10-01 1900"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands(
                "event task /from 2026-10-01 1800 /from 2026-10-01 1830 /to 2026-10-01 1900"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands(
                "event task /from 2026-10-01 1800 /to 2026-10-01 1900 /to 2026-10-01 2000"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands(
                "event task /to 2026-10-01 1900 /from 2026-10-01 1800"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands(
                "event task /from 2026-10-01 1900 /to 2026-10-01 1800"));
    }

    @Test
    public void parseCommands_nonExistentDate_throwsCommandException() {
        assertThrows(CarlCommandException.class, () ->
                parser.parseCommands("deadline report /by 2026-02-30 1800"));
    }

    @Test
    public void parseCommands_nonPositiveTaskNumber_throwsCommandException() {
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("mark 0"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("mark -1"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("mark 1.5"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("mark"));
        assertThrows(CarlCommandException.class, () ->
                parser.parseCommands("mark 999999999999999999999999"));
    }

    @Test
    public void parseCommands_missingOrUnsafeTextArguments_throwCommandException() {
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("todo"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("todo unsafe | task"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("find"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("find unsafe | keyword"));
    }

    @Test
    public void parseCommands_invalidDueDate_throwsCommandException() {
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("due 2026-02-30"));
        assertThrows(CarlCommandException.class, () -> parser.parseCommands("due tomorrow"));
    }
}
