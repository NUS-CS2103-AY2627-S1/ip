package windy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import windy.command.CommandContext;
import windy.command.Parser;
import windy.command.UndoHistory;
import windy.storage.Storage;
import windy.task.Task;
import windy.task.TaskList;
import windy.ui.Ui;

/** Tests single-step history through application responses and storage failures. */
public class UndoRedoTest {
    @TempDir
    Path tempDirectory;

    @Test
    public void taskChanges_restoreCompleteOrderedSnapshots() {
        Windy windy = createWindy();
        for (String input : List.of("todo A", "deadline B /by 2026-9-7",
                "event C /from 2026-9-7 /to 2026-9-8")) {
            String before = windy.getResponse("list");
            windy.getResponse(input);
            String after = windy.getResponse("list");
            assertEquals("Undone the last task change.", windy.getResponse("undo"));
            assertEquals(before, windy.getResponse("list"));
            assertEquals("Redone the last task change.", windy.getResponse("redo"));
            assertEquals(after, windy.getResponse("list"));
        }
        windy.getResponse("mark 2");
        String marked = windy.getResponse("list");
        windy.getResponse("delete 2");
        String deleted = windy.getResponse("list");
        windy.getResponse("undo");
        assertEquals(marked, windy.getResponse("list"));
        windy.getResponse("redo");
        assertEquals(deleted, windy.getResponse("list"));
        windy.getResponse("undo");
        windy.getResponse("unmark 2");
        String unmarked = windy.getResponse("list");
        windy.getResponse("undo");
        assertEquals(marked, windy.getResponse("list"));
        windy.getResponse("redo");
        assertEquals(unmarked, windy.getResponse("list"));
    }

    @Test
    public void history_preservesOnlyLatestChangeAndInvalidatesRedoOnChange() {
        Windy windy = createWindy();
        windy.getResponse("todo A");
        windy.getResponse("todo B");
        windy.getResponse("undo");
        assertEquals("No task change to undo.", windy.getResponse("undo"));
        assertTrue(windy.getResponse("list").contains("A"));
        assertFalse(windy.getResponse("list").contains("B"));
        windy.getResponse("redo");
        assertEquals("No task change to redo.", windy.getResponse("redo"));
        windy.getResponse("undo");
        windy.getResponse("todo C");
        assertEquals("No task change to redo.", windy.getResponse("redo"));
        windy.getResponse("undo");
        assertFalse(windy.getResponse("list").contains("C"));
    }

    @Test
    public void unrelatedCommands_preserveUndoAndRedo() {
        Windy windy = createWindy();
        windy.getResponse("todo A");
        windy.getResponse("mark 1");
        for (String command : List.of("mark 1", "list", "find A", "date 2026-9-7",
                "nonsense", "delete 99", "undo extra", "redo extra")) {
            windy.getResponse(command);
        }
        assertEquals("Undone the last task change.", windy.getResponse("undo"));
        assertTrue(windy.getResponse("list").contains("[T][ ] A"));
        for (String command : List.of("unmark 1", "list", "find A", "date 2026-9-7", "delete 99")) {
            windy.getResponse(command);
        }
        assertEquals("Redone the last task change.", windy.getResponse("redo"));
        assertTrue(windy.getResponse("list").contains("[T][X] A"));
    }

    @Test
    public void invalidCommandsAndUnchangedCompletion_doNotCreateHistory() {
        Windy windy = createWindy();
        windy.getResponse("todo A");
        String original = windy.getResponse("list");

        assertEquals("Task description cannot contain '|'.", windy.getResponse("todo buy | milk"));
        assertEquals("Task number must be between 1 and 1.", windy.getResponse("delete 2"));
        assertEquals(original, windy.getResponse("list"));

        windy.getResponse("mark 1");
        String marked = windy.getResponse("list");
        windy.getResponse("mark 1");
        assertEquals("Undone the last task change.", windy.getResponse("undo"));
        assertEquals(original, windy.getResponse("list"));

        windy.getResponse("unmark 1");
        assertEquals("Redone the last task change.", windy.getResponse("redo"));
        assertEquals(marked, windy.getResponse("list"));
    }

    @Test
    public void parsingAndRestart_matchSpecifiedResponsesAndPersistence() {
        Windy windy = createWindy();
        assertEquals("No task change to undo.", windy.getResponse("UNDO"));
        assertEquals("No task change to redo.", windy.getResponse("ReDo"));
        assertEquals("Invalid format. Please use: undo", windy.getResponse("undo 1"));
        assertEquals("Invalid format. Please use: redo", windy.getResponse("redo 1"));
        windy.getResponse("todo A");
        windy.getResponse("undo");
        Windy restarted = createWindy();
        assertFalse(restarted.getResponse("list").contains("A"));
        assertEquals("No task change to redo.", restarted.getResponse("redo"));
        windy.getResponse("redo");
        restarted = createWindy();
        assertTrue(restarted.getResponse("list").contains("A"));
        assertEquals("No task change to undo.", restarted.getResponse("undo"));
    }

    @Test
    public void failedSaves_preserveMemoryHistoryAndStoredData() throws Exception {
        FailingStorage storage = new FailingStorage(tempDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList(new ArrayList<>());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CommandContext context = new CommandContext(tasks, new Ui(new PrintStream(output)),
                storage, new UndoHistory());
        execute("todo A", tasks, context);
        String saved = Files.readString(storage.path);
        storage.shouldFail = true;
        for (String command : List.of("todo B", "mark 1", "delete 1", "undo")) {
            output.reset();
            execute(command, tasks, context);
            assertEquals("     Could not save tasks to '" + storage.path
                    + "': simulated failure. The change was not applied."
                    + System.lineSeparator(), output.toString());
            assertEquals(saved, Files.readString(storage.path));
            assertEquals(List.of("T | 0 | A"), tasks.getTasks().stream().map(Task::toDataString).toList());
        }
        storage.shouldFail = false;
        execute("undo", tasks, context);
        storage.shouldFail = true;
        output.reset();
        execute("redo", tasks, context);
        assertEquals("     Could not save tasks to '" + storage.path
                + "': simulated failure. The change was not applied."
                + System.lineSeparator(), output.toString());
        assertEquals(0, tasks.getNumTasks());
        assertEquals("", Files.readString(storage.path));
        execute("todo C", tasks, context);
        storage.shouldFail = false;
        execute("redo", tasks, context);
        assertEquals(saved, Files.readString(storage.path));
        assertEquals(1, tasks.getNumTasks());
    }

    private void execute(String input, TaskList tasks, CommandContext context) throws Exception {
        Parser.parseCommand(input, tasks.getNumTasks()).execute(context);
    }

    private Windy createWindy() {
        return new Windy(tempDirectory.resolve("tasks.txt").toString());
    }

    /** Provides deterministic save failures without depending on filesystem permissions. */
    private static class FailingStorage extends Storage {
        private final Path path;
        private boolean shouldFail;

        FailingStorage(Path path) {
            super(path.toString());
            this.path = path;
        }

        @Override
        public void saveTasks(List<Task> tasks) throws IOException {
            if (shouldFail) {
                throw new IOException("simulated failure");
            }
            super.saveTasks(tasks);
        }
    }
}
