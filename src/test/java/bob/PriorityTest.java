package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.task.Priority;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Exercises priority parsing, command execution, display, and persistent storage together.
 */
class PriorityTest {
    @TempDir
    Path directory;

    @Test
    void parse_allTaskTypesAndPriorities_roundTripsThroughStorage() throws Exception {
        for (String input : List.of("todo read | review \\ notes",
                "deadline submit /by 2026-09-09",
                "event meeting /from 2026-09-09 1400 /to 2026-09-09 1600")) {
            for (Priority priority : Priority.values()) {
                TaskList tasks = new TaskList();
                Storage storage = new Storage(directory.resolve("tasks.txt"));
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Ui ui = new Ui(new PrintStream(output));
                Parser.parse(input + " /priority " + priority).execute(tasks, ui, storage);
                Task task = tasks.get(0);
                assertEquals(priority, task.getPriority());
                assertTrue(output.toString().contains(task.toString()));
                Parser.parse("mark 1").execute(tasks, ui, storage);
                Task restored = storage.load().get(0);
                assertEquals(priority, restored.getPriority());
                assertTrue(restored.isDone());
                assertEquals(task.toString(), restored.toString());
                assertEquals(task.toStorageString(), restored.toStorageString());
                Parser.parse("unmark 1").execute(tasks, ui, storage);
                assertEquals(priority, storage.load().get(0).getPriority());
            }
        }
    }

    @Test
    void parse_omittedAndExplicitNone_preserveLegacyDisplayAndStorage() throws Exception {
        for (String suffix : List.of("", " /priority none")) {
            TaskList tasks = new TaskList();
            Storage storage = new Storage(directory.resolve("tasks.txt"));
            Parser.parse("todo read book" + suffix).execute(tasks,
                    new Ui(new PrintStream(new ByteArrayOutputStream())), storage);
            assertEquals(Priority.NONE, tasks.get(0).getPriority());
            assertEquals("[T][ ] read book", tasks.get(0).toString());
            assertEquals("T | 0 | read book", tasks.get(0).toStorageString());
        }
    }

    @Test
    void parse_invalidPriority_rejectsWithGuidance() {
        for (String suffix : List.of("", "urgent", "HIGH", "high extra", "high /priority low")) {
            for (String input : List.of("todo read", "deadline read /by 2026-09-09",
                    "event read /from 2026-09-09 1400 /to 2026-09-09 1600")) {
                BobException error = assertThrows(BobException.class, () ->
                        Parser.parse(input + " /priority " + suffix));
                assertEquals("pls end the task with /priority low, moderate, high, or none", error.getMessage());
            }
        }
        assertThrows(BobException.class, () -> Parser.parse("todo /priority high"));
        assertThrows(BobException.class, () ->
                Parser.parse("deadline read /priority high /by 2026-09-09"));
    }

    @Test
    void load_legacyRecords_defaultsToNone() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.write(file, List.of("T | 1 | read", "D | 0 | submit | 2026-09-09",
                "E | 0 | meeting | 2026-09-09 1400 | 2026-09-09 1600"));
        for (Task task : new Storage(file).load()) {
            assertEquals(Priority.NONE, task.getPriority());
        }
    }

    @Test
    void load_invalidPriority_reportsMalformedData() throws Exception {
        Path file = directory.resolve("tasks.txt");
        for (String label : List.of("urgent", "", "high | low")) {
            Files.writeString(file, "T | 0 | read | " + label);
            StorageException error = assertThrows(StorageException.class, () -> new Storage(file).load());
            assertEquals("invalid data on line 1", error.getMessage());
        }
    }

    @Test
    void parse_priorityPrefixInsideDescription_preservesText() throws Exception {
        TaskList tasks = new TaskList();
        Parser.parse("todo read /priority-guide /priority high").execute(tasks,
                new Ui(new PrintStream(new ByteArrayOutputStream())),
                new Storage(directory.resolve("tasks.txt")));
        assertEquals("[T][ ][priority: high] read /priority-guide", tasks.get(0).toString());
        assertTrue(tasks.get(0).containsKeyword("/priority-guide"));
    }
}
