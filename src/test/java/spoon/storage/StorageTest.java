package spoon.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import spoon.exception.FileCorruptedException;
import spoon.task.Deadline;
import spoon.task.Event;
import spoon.task.Task;
import spoon.task.ToDo;

public class StorageTest {

    @TempDir
    Path tempFolder;

    /**
     * Helper to write lines directly into a file within the temporary directory.
     */
    private File createTestFileWithContent(String fileName, String... lines) throws IOException {
        File file = tempFolder.resolve(fileName).toFile();
        try (FileWriter fw = new FileWriter(file)) {
            for (String line : lines) {
                fw.write(line + System.lineSeparator());
            }
        }
        return file;
    }

    @Nested
    class ConstructorTests {

        @Test
        void constructor_nullOrBlankPath_throwsAssertionError() {
            assertThrows(AssertionError.class, () -> new Storage(null));
            assertThrows(AssertionError.class, () -> new Storage(""));
            assertThrows(AssertionError.class, () -> new Storage("   "));
        }
    }

    @Nested
    class LoadTests {

        @Test
        void load_nonExistentFile_returnsEmptyList() throws Exception {
            String nonExistentPath = tempFolder.resolve("non_existent.txt").toString();
            Storage storage = new Storage(nonExistentPath);

            ArrayList<Task> tasks = storage.load();
            assertTrue(tasks.isEmpty());
        }

        @Test
        void load_validTasks_returnsPopulatedList() throws Exception {
            File testFile = createTestFileWithContent("valid.txt",
                    "T | 0 | read book",
                    "T | 1 | return book",
                    "D | 0 | return assignment | 01/01/0001",
                    "E | 1 | project meeting | 01/01/0001 0001 | 02/01/0001 0101"
            );
            Storage storage = new Storage(testFile.getAbsolutePath());

            ArrayList<Task> tasks = storage.load();
            assertEquals(4, tasks.size());

            assertInstanceOf(ToDo.class, tasks.get(0));
            assertTrue(tasks.get(0).format().startsWith("T | 0 |"));

            assertInstanceOf(ToDo.class, tasks.get(1));
            assertTrue(tasks.get(1).format().startsWith("T | 1 |"));

            assertInstanceOf(Deadline.class, tasks.get(2));
            assertTrue(tasks.get(2).format().startsWith("D | 0 |"));

            assertInstanceOf(Event.class, tasks.get(3));
            assertTrue(tasks.get(3).format().startsWith("E | 1 |"));
        }

        @Test
        void load_duplicateTasksInFile_throwsFileCorruptedException() throws Exception {
            File testFile = createTestFileWithContent("duplicates.txt",
                    "T | 0 | duplicate task",
                    "T | 0 | duplicate task"
            );
            Storage storage = new Storage(testFile.getAbsolutePath());

            assertThrows(FileCorruptedException.class, storage::load);
        }

        @Test
        void load_emptyLineInFile_throwsFileCorruptedException() throws Exception {
            File testFile = createTestFileWithContent("empty_line.txt",
                    "T | 0 | valid task",
                    "",
                    "T | 0 | another task"
            );
            Storage storage = new Storage(testFile.getAbsolutePath());

            assertThrows(FileCorruptedException.class, storage::load);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "X | 0 | invalid type",
                "T | 2 | wrong status value",
                "T | 0 |   ",                      // Blank task description
                "T | 0",                            // Missing description parameter
                "D | 0 | deadline without by",      // Missing deadline by date
                "D | 0 | deadline |   ",            // Blank deadline by date
                "E | 0 | event | 01/01/0001",       // Missing event end date
                "E | 0 | event | 01/01/0001 |   "   // Blank event end date
        })
        void load_corruptedFormatLines_throwsFileCorruptedException(String corruptedLine) throws Exception {
            File testFile = createTestFileWithContent("corrupted.txt", corruptedLine);
            Storage storage = new Storage(testFile.getAbsolutePath());

            assertThrows(FileCorruptedException.class, storage::load);
        }
    }

    @Nested
    class SaveTests {

        @Test
        void save_nullTasksList_throwsAssertionError() {
            Storage storage = new Storage(tempFolder.resolve("save_test.txt").toString());
            assertThrows(AssertionError.class, () -> storage.save(null));
        }

        @Test
        void save_validTasks_writesCorrectFormatToFile() throws Exception {
            Path targetFile = tempFolder.resolve("saved_tasks.txt");
            Storage storage = new Storage(targetFile.toString());

            ArrayList<Task> tasks = new ArrayList<>();
            ToDo todo = new ToDo("buy milk");
            todo.complete();
            tasks.add(todo);

            Deadline deadline = new Deadline("submit essay", "01/01/0001");
            tasks.add(deadline);

            Event event = new Event("orientation", "01/01/0001 0001", "02/01/0001 0101");
            tasks.add(event);

            storage.save(tasks);

            List<String> writtenLines = Files.readAllLines(targetFile);
            assertEquals(3, writtenLines.size());
            assertEquals(todo.format(), writtenLines.get(0));
            assertEquals(deadline.format(), writtenLines.get(1));
            assertEquals(event.format(), writtenLines.get(2));
        }

        @Test
        void save_createsParentDirectoriesIfMissing() throws Exception {
            Path nestedFilePath = tempFolder.resolve("subfolder").resolve("deep").resolve("spoon.txt");
            Storage storage = new Storage(nestedFilePath.toString());

            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(new ToDo("test parent dirs"));

            storage.save(tasks);

            assertTrue(Files.exists(nestedFilePath));
        }
    }
}
