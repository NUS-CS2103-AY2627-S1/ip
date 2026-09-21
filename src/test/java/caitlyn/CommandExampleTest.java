package caitlyn;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests that menu examples remain executable and safe to edit as commands evolve. */
class CommandExampleTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void examples_everyCommand_executesWithValidTaskState() {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        for (CommandExample example : CommandExample.values()) {
            List<Task> tasks = new ArrayList<>(List.of(new Todo("read book")));
            Command command = Parser.parse(example.getExample(), storage);
            assertDoesNotThrow(() -> command.execute(tasks, new Ui(message -> { })), example.name());
        }
    }

    @Test
    void selection_commandsWithArguments_preservesSyntaxWhenReplacingValue() {
        assertEquals("todo new value", replaceSelection(CommandExample.TODO));
        assertEquals("deadline new value /by 2027-01-25", replaceSelection(CommandExample.DEADLINE));
        assertEquals("event new value /from 2027-01-15 1400 /to 2027-01-15 1600",
                replaceSelection(CommandExample.EVENT));
        assertEquals("within new value /from 2027-01-15 /to 2027-01-25",
                replaceSelection(CommandExample.WITHIN));
        assertEquals("find new value", replaceSelection(CommandExample.FIND));
        assertEquals("mark new value", replaceSelection(CommandExample.MARK));
        assertEquals("unmark new value", replaceSelection(CommandExample.UNMARK));
        assertEquals("delete new value", replaceSelection(CommandExample.DELETE));
    }

    @Test
    void selection_commandsWithoutArguments_placesCaretAtEnd() {
        for (CommandExample example : List.of(CommandExample.LIST, CommandExample.BYE)) {
            assertEquals(example.getExample().length(), example.getSelectionStart());
            assertEquals(example.getSelectionStart(), example.getSelectionEnd());
        }
    }

    @Test
    void getTemplate_menuEntries_displaysSyntaxForEveryCommand() {
        assertEquals(List.of("todo <description>", "deadline <description> /by <date>",
                "event <description> /from <start> /to <end>", "within <description> /from <start> /to <end>",
                "list", "find <keyword>", "mark <task number>", "unmark <task number>", "delete <task number>", "bye"),
                Arrays.stream(CommandExample.values()).map(CommandExample::getTemplate).toList());
    }

    /** Simulates typing over the range selected when a menu example is inserted. */
    private String replaceSelection(CommandExample command) {
        return command.getExample().substring(0, command.getSelectionStart()) + "new value"
                + command.getExample().substring(command.getSelectionEnd());
    }
}
