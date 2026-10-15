package nutrisoy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import nutrisoy.command.Command;
import nutrisoy.command.DeadlineCommand;
import nutrisoy.command.DeleteCommand;
import nutrisoy.command.EventCommand;
import nutrisoy.command.ExitCommand;
import nutrisoy.command.FindCommand;
import nutrisoy.command.ListCommand;
import nutrisoy.command.MarkCommand;
import nutrisoy.command.TagCommand;
import nutrisoy.command.TodoCommand;
import nutrisoy.command.UnmarkCommand;
import nutrisoy.command.UntagCommand;
import nutrisoy.exception.DukeException;

/** Verifies command dispatch, whitespace handling, and rejection of unsupported input. */
public class ParserTest {
    @TestFactory
    public Stream<DynamicTest> parse_eachCommand_dispatchesWithMixedCaseAndWhitespace() {
        Map<String, Class<? extends Command>> commands = Map.ofEntries(
                Map.entry("todo Read Book", TodoCommand.class),
                Map.entry("deadline Report /by 2026-09-20", DeadlineCommand.class),
                Map.entry("event Trip /from 2026-09-20 /to 2026-09-21", EventCommand.class),
                Map.entry("list", ListCommand.class), Map.entry("bye", ExitCommand.class),
                Map.entry("mark 1", MarkCommand.class), Map.entry("unmark 1", UnmarkCommand.class),
                Map.entry("delete 1", DeleteCommand.class), Map.entry("find Book", FindCommand.class),
                Map.entry("tag 1 work", TagCommand.class), Map.entry("untag 1 work", UntagCommand.class));
        return commands.entrySet().stream().map(entry -> dynamicTest(entry.getKey(), () -> {
            String[] parts = entry.getKey().split(" ", 2);
            String input = " \t" + parts[0].toUpperCase(Locale.ROOT)
                    + (parts.length == 2 ? "\t  " + parts[1] : "") + "  ";
            assertEquals(entry.getValue(), Parser.parse(input).getClass());
        }));
    }

    @TestFactory
    public Stream<DynamicTest> parse_invalidCommands_throwsHelpfulException() {
        return Stream.of("", "\t", "unknown", "list extra", "bye now", "tag", "untag",
                "tag 1 work extra", "untag 1 #work")
                .map(input -> dynamicTest("invalid: " + input, () -> {
                    DukeException error = assertThrows(DukeException.class, () -> Parser.parse(input));
                    assertTrue(!error.getMessage().isBlank());
                }));
    }

    @Test
    public void parse_validTodoCommand_success() throws DukeException {
        Command cmd = Parser.parse("todo read book");
        assertTrue(cmd instanceof TodoCommand);
    }

    @Test
    public void parse_validExitCommand_success() throws DukeException {
        Command cmd = Parser.parse("bye");
        assertTrue(cmd instanceof ExitCommand);
    }

    @Test
    public void parse_validTagCommands_success() throws DukeException {
        assertTrue(Parser.parse("tag 1 fun") instanceof TagCommand);
        assertTrue(Parser.parse("untag 1 fun") instanceof UntagCommand);
    }

    @Test
    public void parse_invalidCommand_exceptionThrown() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("invalidCommandWord");
        });
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        assertThrows(DukeException.class, () -> {
            Parser.parse("   ");
        });
    }

    @Test
    public void parse_malformedTagCommand_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("tag 1"));
        assertThrows(DukeException.class, () -> Parser.parse("untag 1 fun extra"));
    }
}
