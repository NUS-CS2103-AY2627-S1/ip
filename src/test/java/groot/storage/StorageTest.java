package groot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import groot.exception.GrootException;
import groot.task.Deadline;
import groot.task.Event;
import groot.task.Task;
import groot.task.Todo;
import groot.testutil.IsolatedProcess;

/**
 * Exercises the real storage format and filesystem failures without touching user data.
 */
public class StorageTest {
    @TempDir
    public Path directory;

    @Test
    public void loadTasks_missingFile_returnsEmptyWithoutCreatingData() throws Exception {
        assertEquals("", run("load"));
        assertFalse(Files.exists(directory.resolve("data")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "\n \n\t\n"})
    public void loadTasks_emptyOrBlankFile_returnsEmpty(String content) throws Exception {
        writeData(content);
        assertEquals("", run("load"));
    }

    @Test
    public void saveTasks_allTypesAndEscapes_roundTripsAndOverwrites() throws Exception {
        String expected = "T | 1 | read \\| 管道 \\\\ notes\n"
                + "D | 0 | report | 2028-02-29\n"
                + "E | 1 | meeting | Mon \\| 2pm | Tue \\\\ end\n";
        assertEquals(expected, run("save"));
        assertEquals(expected, Files.readString(directory.resolve("data/groot.txt")).replace("\r\n", "\n"));
        assertEquals(expected, run("load"));
        assertEquals("", run("empty"));
        assertEquals("", Files.readString(directory.resolve("data/groot.txt")));
        assertNoTemporaryFiles();
    }

    @Test
    public void loadTasks_legacyEscapesAndWhitespace_preservesLiteralCharacters() throws Exception {
        writeData("\n T | 0 | legacy \\q slash\\\nT | 1 | pipe\\|and\\\\slash\n");
        assertEquals("T | 0 | legacy \\\\q slash\\\\\nT | 1 | pipe\\|and\\\\slash\n", run("load"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "T", "T | 0", "T | 0 |", "T | 2 | task", "T | true | task", "Q | 0 | task",
        "T | 0 | task | extra", "D | 0 | task", "D | 0 | task |", "D | 0 | task | invalid",
        "D | 0 | task | 2025-02-29", "D | 0 | task | 2026-13-01",
        "E | 0 | task | Mon", "E | 0 | task | | Tue", "E | 0 | task | Mon |",
        "E | 0 | task | Mon | Tue | extra", " | 0 | task", "D | 0 | | 2026-01-01"
    })
    public void loadTasks_malformedData_reportsPhysicalLineAndPreservesFile(String invalidLine) throws Exception {
        String content = "T | 0 | valid\n\n" + invalidLine + "\n";
        writeData(content);
        String expected = "Oops! I couldn't load your tasks because line 3 in "
                + Path.of("data", "groot.txt") + " is invalid.\n";
        assertEquals(expected, run("load"));
        assertEquals(content, Files.readString(directory.resolve("data/groot.txt")));
    }

    @Test
    public void loadTasks_directoryInsteadOfFile_reportsReadFailure() throws Exception {
        Files.createDirectories(directory.resolve("data/groot.txt"));
        assertEquals("Oops! I couldn't read your saved tasks.\n", run("load"));
    }

    @Test
    public void saveTasks_directoryCreationFails_preservesBlockingFile() throws Exception {
        Files.writeString(directory.resolve("data"), "keep me");
        assertEquals("Oops! I couldn't save your tasks. Your last change was not applied.\n", run("save"));
        assertEquals("keep me", Files.readString(directory.resolve("data")));
    }

    @Test
    public void saveTasks_replacementFails_cleansTemporaryFileAndPreservesDestination() throws Exception {
        Files.createDirectories(directory.resolve("data/groot.txt"));
        Files.writeString(directory.resolve("data/groot.txt/keep.txt"), "keep me");
        assertEquals("Oops! I couldn't save your tasks. Your last change was not applied.\n", run("save"));
        assertEquals("keep me", Files.readString(directory.resolve("data/groot.txt/keep.txt")));
        assertNoTemporaryFiles();
    }

    private void writeData(String content) throws Exception {
        Files.createDirectories(directory.resolve("data"));
        Files.writeString(directory.resolve("data/groot.txt"), content);
    }

    private String run(String action) throws Exception {
        return IsolatedProcess.run(directory, StorageScenario.class, "", action);
    }

    private void assertNoTemporaryFiles() throws Exception {
        try (var paths = Files.list(directory.resolve("data"))) {
            assertTrue(paths.noneMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }

    /**
     * Runs real storage operations in a dedicated working directory.
     */
    public static class StorageScenario {
        /**
         * Prints loaded records or a user-facing storage error.
         */
        public static void main(String[] args) throws Exception {
            assertNotNull(new Storage());
            try {
                if (args[0].equals("save")) {
                    Todo todo = new Todo("read | 管道 \\ notes");
                    todo.markAsDone();
                    Event event = new Event("meeting", "Mon | 2pm", "Tue \\ end");
                    event.markAsDone();
                    Storage.saveTasks(List.of(todo, new Deadline("report", LocalDate.of(2028, 2, 29)), event));
                } else if (args[0].equals("empty")) {
                    Storage.saveTasks(List.of());
                }
                for (Task task : Storage.loadTasks()) {
                    System.out.println(task.toDataString());
                }
            } catch (GrootException error) {
                assertNotNull(error.getCause());
                System.out.println(error.getMessage());
            }
        }
    }
}
