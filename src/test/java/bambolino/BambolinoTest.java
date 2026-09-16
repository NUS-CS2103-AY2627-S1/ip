package bambolino;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import bambolino.exception.BambolinoException;
import bambolino.storage.Storage;
import bambolino.task.Task;
import bambolino.task.TaskList;
import bambolino.ui.Ui;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests command execution and recovery through the same entry point used by the GUI. */
class BambolinoTest {
    @TempDir
    Path directory;
    private Storage storage;
    private TaskList tasks;
    private List<String> output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        storage = new Storage(directory.resolve("tasks.txt"));
        tasks = new TaskList();
        output = new ArrayList<>();
        ui = new Ui(output::add);
    }

    @Test
    void processCommand_taskLifecycle_persistsEveryChange() throws Exception {
        run("todo read book");
        run("deadline return book /by 2024-02-29");
        run("event meeting /from Mon 2pm /to 4pm");
        assertEquals(3, storage.load().size());
        assertTrue(output.contains("Now you have 3 tasks in the list."));
        run("mark 2");
        assertEquals("X", storage.load().get(1).getStatusIcon());
        run("unmark 2");
        assertEquals(" ", storage.load().get(1).getStatusIcon());
        run("delete 1");
        assertEquals("return book", storage.load().getFirst().getDescription());
        assertEquals(2, tasks.size());
        output.clear();
        run("list");
        assertTrue(output.contains("1.[D][ ] return book (by: Feb 29 2024)"));
        assertTrue(output.contains("2.[E][ ] meeting (from: Mon 2pm to: 4pm)"));
    }

    @Test
    void processCommand_find_preservesOriginalNumbers() throws Exception {
        run("todo unrelated");
        run("todo Read BOOK");
        output.clear();
        run("find book");
        assertTrue(output.contains("2.[T][ ] Read BOOK"));
        assertFalse(output.stream().anyMatch(line -> line.contains("unrelated")));
        output.clear();
        run("find absent");
        assertTrue(output.contains("No matching tasks found."));
    }

    @TestFactory
    Stream<DynamicTest> processCommand_invalidInput_leavesTasksAndFileUnchanged() {
        return Stream.of("", " \t ", "unknown", "bye extra", "list extra", "find", "todo",
                "deadline book", "deadline /by 2024-01-01", "deadline book /by",
                "deadline book /by 2023-02-29", "deadline book /by tomorrow",
                "deadline book /by 2024-01-01 /by 2024-01-02", "event meeting",
                "event /from 2pm /to 4pm", "event meeting /from /to 4pm",
                "event meeting /from 2pm /to", "event meeting /to 4pm /from 2pm",
                "event meeting /to 1pm /from 2pm /to 4pm",
                "event meeting /from 1pm /from 2pm /to 4pm",
                "event meeting /from 2pm /to 4pm /to 5pm",
                "mark", "unmark", "delete", "mark abc", "unmark 1.5", "delete 1 2",
                "mark 9999999999999999", "mark 0", "delete -1", "unmark 2")
                .map(command -> DynamicTest.dynamicTest("Reject: [" + command + "]", () -> {
                    // Dynamic tests share their factory's instance, so reset state for every case.
                    setUp();
                    run("todo original");
                    output.clear();
                    BambolinoException error = assertThrows(BambolinoException.class, () -> run(command));
                    assertFalse(error.getMessage().isBlank());
                    assertEquals(List.of("[T][ ] original"), tasks.asList().stream().map(Task::toString).toList());
                    assertEquals("[T][ ] original", storage.load().getFirst().toString());
                    assertTrue(output.isEmpty());
                }));
    }

    @Test
    void processCommand_whitespaceAndAliases_addValidTasks() throws Exception {
        run("  T\tread book  ");
        run("D book\t/by\t2024-02-29");
        run("E meeting\t/from\t2pm\t/to\t4pm");
        assertEquals(3, tasks.size());
        run("M 1");
        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    void loadTasks_readFailure_warnsAndStartsEmpty() {
        TaskList loaded = Bambolino.loadTasks(new Storage(directory), ui);
        assertTrue(loaded.isEmpty());
        assertTrue(output.getFirst().contains("Saving is disabled to protect your existing data."));
    }

    @Test
    void loadTasks_corruptRecord_warnsThroughActiveUiAndKeepsValidTasks() throws IOException {
        Files.write(directory.resolve("tasks.txt"), List.of("bad", "T|0|Ym9vaw=="));
        TaskList loaded = Bambolino.loadTasks(storage, ui);
        assertEquals("book", loaded.get(0).getDescription());
        assertEquals(2, output.size());
        assertTrue(output.getFirst().startsWith("Warning: Ignored a corrupted task"));
    }

    @Test
    void processCommand_afterIncompleteLoad_rejectsAllMutations() throws Exception {
        Path file = directory.resolve("tasks.txt");
        String original = "bad\nT|0|Ym9vaw==\n";
        Files.writeString(file, original);
        tasks = Bambolino.loadTasks(storage, ui);
        for (String command : List.of("todo new", "deadline book /by 2026-10-15",
                "event meeting /from 2pm /to 3pm", "mark 1", "unmark 1", "delete 1")) {
            BambolinoException error = assertThrows(BambolinoException.class, () -> run(command));
            assertTrue(error.getMessage().contains("Saving is disabled"));
            assertEquals("[T][ ] book", tasks.get(0).toString());
            assertEquals(1, tasks.size());
            assertEquals(original, Files.readString(file));
        }
        run("list");
        run("find book");
    }

    @Test
    void loadTasks_savedFile_restoresTasks() throws Exception {
        run("todo retained");
        assertEquals("retained", Bambolino.loadTasks(storage, ui).get(0).getDescription());
    }

    @Test
    void processCommand_saveFailure_reportsErrorWithoutSuccessMessage() {
        Storage failingStorage = new Storage(directory) {
            @Override
            public void save(List<Task> savedTasks) throws IOException {
                throw new IOException("Simulated write failure");
            }
        };
        BambolinoException error = assertThrows(BambolinoException.class,
                () -> Bambolino.processCommand("todo book", tasks, failingStorage, ui));
        assertEquals("I couldn't save your tasks. Please check the data folder.", error.getMessage());
        assertTrue(output.isEmpty());
    }

    private void run(String command) throws BambolinoException {
        Bambolino.processCommand(command, tasks, storage, ui);
    }
}
