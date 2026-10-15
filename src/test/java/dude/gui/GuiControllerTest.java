package dude.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dude.command.duplicate.DuplicateResolution;
import dude.command.duplicate.DuplicateResolutionHandler;
import dude.storage.Storage;

/**
 * Tests GUI command execution without requiring a JavaFX toolkit.
 */
public class GuiControllerTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_updatesTaskListAndKeepsFindIndexes() {
        GuiController controller = createController();
        controller.loadTasks();

        controller.execute("todo buy milk");
        controller.execute("todo read book");
        GuiResponse response = controller.execute("find book");

        assertFalse(response.error());
        assertTrue(response.text().contains("2.[T][ ] read book"));
        assertFalse(response.text().contains("1.[T][ ] read book"));
    }

    @Test
    public void execute_returnsStyledErrorResultForInvalidCommand() {
        GuiController controller = createController();

        GuiResponse response = controller.execute("mark missing");

        assertTrue(response.error());
        assertTrue(response.text().contains("task number for mark"));
        assertFalse(response.text().contains("─"));
        assertFalse(response.text().contains("\\u001B"));
    }

    @Test
    public void execute_missingTodoDetailsUsesGuiUsageWithoutPrefix() {
        GuiController controller = createController();

        GuiResponse response = controller.execute("todo");

        assertTrue(response.error());
        assertEquals("Hmm, the task details for todo that part is missing. "
                + "I need non-empty text.\nTry: todo <task details>", response.text());
        assertFalse(response.text().contains("Try: Usage:"));
    }

    @Test
    public void execute_invalidDateUsesGuiUsageWithoutPrefix() {
        GuiController controller = createController();

        GuiResponse response = controller.execute("on 2025-23-12");

        assertTrue(response.error());
        assertEquals("Hmm, the date for on \"2025-23-12\" isn't valid. "
                + "I need yyyy-MM-dd.\nTry: on <yyyy-MM-dd>", response.text());
        assertFalse(response.text().contains("Try: Usage:"));
    }

    @Test
    public void execute_unknownCommandListsEverySupportedCommand() {
        GuiController controller = createController();

        GuiResponse response = controller.execute("unknown");

        assertTrue(response.error());
        assertEquals("Hmm, I don't recognize \"unknown\" as a command. "
                + "Try one of: bye, list, find, on, mark, unmark, delete, todo, deadline, event, undo.",
                response.text());
        assertFalse(response.exit());
    }

    @Test
    public void execute_byeRequestsGuiExit() {
        GuiController controller = createController();

        GuiResponse response = controller.execute("bye");

        assertFalse(response.error());
        assertTrue(response.exit());
        assertTrue(response.text().contains("Catch you later"));
    }

    @Test
    public void commandPaletteKeepsUsageVisibleWhileEnteringArguments() {
        List<String> usages = List.of(
                "Usage: todo <task details>",
                "Usage: deadline <description> /by <yyyy-MM-dd [HHmm]>",
                "Usage: event <description> /from <yyyy-MM-dd [HHmm]> /to <yyyy-MM-dd [HHmm]>");

        assertEquals(List.of("Usage: deadline <description> /by <yyyy-MM-dd [HHmm]>"),
                MainWindowController.filterCommandUsages("deadline ", usages));
        assertEquals(List.of("Usage: deadline <description> /by <yyyy-MM-dd [HHmm]>"),
                MainWindowController.filterCommandUsages("deadline submit report", usages));
    }

    @Test
    public void execute_cancelledDuplicateReturnsNoResponseAndKeepsTask() {
        GuiController controller = createController(conflict -> DuplicateResolution.cancel());
        controller.execute("todo read book");

        GuiResponse response = controller.execute("todo read book");

        assertFalse(response.error());
        assertFalse(response.exit());
        assertEquals("", response.text());
        assertEquals(1, controller.getTasks().size());

        GuiResponse undoResponse = controller.execute("undo");

        assertEquals("Done, dude — I rolled back:\n  add task.", undoResponse.text());
        assertEquals(0, controller.getTasks().size());
    }

    @Test
    public void execute_persistsTaskChanges() throws Exception {
        GuiController controller = createController();
        controller.loadTasks();
        controller.execute("todo write tests");

        Storage storage = new Storage(temporaryDirectory.resolve("dude.jsonl"),
                temporaryDirectory.resolve("dude.txt"));

        assertTrue(storage.loadTasks().get(0).getDescription().equals("write tests"));
    }

    private GuiController createController() {
        return createController(conflict -> DuplicateResolution.add());
    }

    private GuiController createController(DuplicateResolutionHandler handler) {
        Storage storage = new Storage(temporaryDirectory.resolve("dude.jsonl"),
                temporaryDirectory.resolve("dude.txt"));
        return new GuiController(storage, handler);
    }
}
