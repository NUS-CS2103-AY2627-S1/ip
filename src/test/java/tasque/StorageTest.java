package tasque;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tasque.task.Deadline;
import tasque.task.Event;
import tasque.task.Task;
import tasque.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    public void loadTasks_missingFile_returnsEmptyWithoutCreatingStorage() throws Exception {
        Path storagePath = this.tempDirectory.resolve("missing").resolve("tasks.txt");

        assertTrue(new Storage(storagePath.toString()).loadTasks().isEmpty());
        assertFalse(Files.exists(storagePath.getParent()));
    }

    @Test
    public void loadTasks_emptyOrBlankFile_returnsEmpty() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        for (String content : new String[] {"", "\n \t\n"}) {
            Files.writeString(storagePath, content);
            assertTrue(new Storage(storagePath.toString()).loadTasks().isEmpty());
            assertEquals(content, Files.readString(storagePath));
        }
    }

    @Test
    public void loadTasks_malformedRecord_reportsLineAndPreservesFile() throws Exception {
        String[][] invalidRecords = {
            {"T | 0", "expected 3 fields"},
            {"T | 0 | task | extra", "expected 3 fields"},
            {"D2 | 0 | dGFzaw==", "expected 4 fields"},
            {"D | 0 | task | 2026-09-16 |", "expected 4 fields"},
            {"E | 0 | task | 2026-09-16", "expected 5 fields"},
            {"E2 | 0 | dGFzaw== | start | end | extra", "expected 5 fields"},
            {"X | 0 | task", "unknown task type"},
            {"T3 | 0 | task", "unknown task type"},
            {"T | 2 | task", "completion marker"},
            {"T2 | | dGFzaw==", "completion marker"},
            {"T | 0 |   ", "description"},
            {"T2 | 0 |", "description"},
            {"D2 | 0 | ICA= | 2026-09-16", "description"},
            {"T2 | 0 | !!!", "Base64"},
            {"T2 | 0 | /w==", "UTF-8"},
            {"D | 0 | task | 2026-02-30", "deadline date"},
            {"D2 | 0 | dGFzaw== | tomorrow", "deadline date"},
            {"E | 0 | task | | Monday", "start and end"},
            {"E2 | 0 | dGFzaw== | Monday |", "start and end"}
        };
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        for (String[] record : invalidRecords) {
            Files.writeString(storagePath, "T | 1 | retained task\n \t\n" + record[0] + "\n");
            byte[] original = Files.readAllBytes(storagePath);
            Storage storage = new Storage(storagePath.toString());

            TasqueException exception = assertThrows(TasqueException.class, storage::loadTasks, record[0]);

            assertTrue(exception.getMessage().contains("line 3"), exception.getMessage());
            assertTrue(exception.getMessage().contains(record[1]), exception.getMessage());
            assertTrue(exception.getMessage().contains(storagePath.toString()));
            assertFalse(exception.getMessage().contains("OOPS!!!"));
            assertArrayEquals(original, Files.readAllBytes(storagePath));
        }
    }

    @Test
    public void loadTasks_existingDirectory_reportsReadFailure() throws Exception {
        Path storagePath = Files.createDirectory(this.tempDirectory.resolve("tasks.txt"));
        Storage storage = new Storage(storagePath.toString());

        TasqueException exception = assertThrows(TasqueException.class, storage::loadTasks);

        assertTrue(exception.getMessage().contains("could not be read"));
        assertTrue(Files.isDirectory(storagePath));
    }

    @Test
    public void saveAndLoadTasks_legacyEventTextAndDuplicates_preservesCompatibility() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "\nT | 0 | repeated\n \t\nT | 0 | repeated\n"
                + "E | 0 | meeting | Monday 2pm | Monday 4pm\n"
                + "E2 | 1 | dGFzaw== | 2026-02-30 | 2026-09-16\n"
                + "E | 0 | old range | 2026-08-17 | 2026-08-16\n");
        Storage storage = new Storage(storagePath.toString());

        List<Task> loaded = storage.loadTasks();
        assertEquals(List.of("[T][ ] repeated", "[T][ ] repeated",
                "[E][ ] meeting (from: Monday 2pm to: Monday 4pm)",
                "[E][X] task (from: 2026-02-30 to: 2026-09-16)",
                "[E][ ] old range (from: Aug 17 2026 to: Aug 16 2026)"),
                loaded.stream().map(Task::toString).toList());
        storage.saveTasks(loaded);
        assertTrue(Files.readString(storagePath).contains("E2 | 0 | bWVldGluZw== | Monday 2pm | Monday 4pm"));
        assertEquals(loaded.stream().map(Task::toString).toList(),
                storage.loadTasks().stream().map(Task::toString).toList());
    }

    @Test
    public void saveAndLoadTasks_descriptionContainsPipe_preservesDescription() throws Exception {
        Path storagePath = this.tempDirectory.resolve("data").resolve("tasque.txt");
        Storage storage = new Storage(storagePath.toString());

        storage.saveTasks(List.of(new Todo("read | book")));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("read | book", loadedTasks.get(0).getDescription());
    }

    @Test
    public void saveAndLoadTasks_unicodeDescriptions_preservesTextAndCompletion() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasks.txt");
        Storage storage = new Storage(storagePath.toString());
        List<String> descriptions = List.of("复习  Java | café 📚", "提交  报告 | résumé 📝", "讨论  项目 | 团队 🤝");
        Task deadline = new Deadline(descriptions.get(1), "2026-08-26");
        deadline.markAsDone();

        storage.saveTasks(List.of(new Todo(descriptions.get(0)), deadline,
                new Event(descriptions.get(2), "2026-08-28", "2026-08-29")));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(descriptions, loadedTasks.stream().map(Task::getDescription).toList());
        assertEquals(List.of(false, true, false), loadedTasks.stream().map(Task::isDone).toList());
    }

    @Test
    public void saveTasks_nonEmptyDestinationDirectory_preservesContentsAndCleansUp() throws Exception {
        Path storagePath = Files.createDirectory(this.tempDirectory.resolve("tasks.txt"));
        Path sentinelPath = storagePath.resolve("sentinel.txt");
        Files.writeString(sentinelPath, "keep original contents");
        byte[] original = Files.readAllBytes(sentinelPath);
        Storage storage = new Storage(storagePath.toString());

        TasqueException exception = assertThrows(TasqueException.class, () -> storage.saveTasks(
                List.of(new Todo("new task"))));

        assertEquals("I couldn't save your tasks.", exception.getMessage());
        assertTrue(Files.isDirectory(storagePath));
        assertArrayEquals(original, Files.readAllBytes(sentinelPath));
        try (Stream<Path> remainingFiles = Files.list(this.tempDirectory)) {
            assertEquals(List.of(storagePath), remainingFiles.toList());
        }
    }

    @Test
    public void loadTasks_legacyRecords_preservesExistingFormat() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasque.txt");
        Files.writeString(storagePath, "T | 0 | read book\n"
                + "D | 1 | submit report | 2026-08-28\n"
                + "E | 0 | team meeting | Monday 2pm | Monday 4pm\n");
        Storage storage = new Storage(storagePath.toString());

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Aug 28 2026)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] team meeting (from: Monday 2pm to: Monday 4pm)",
                loadedTasks.get(2).toString());
    }

    @Test
    public void saveTasks_mixedTaskTypes_writesExactEncodedRecords() throws Exception {
        Path storagePath = this.tempDirectory.resolve("tasque.txt");
        Storage storage = new Storage(storagePath.toString());
        Task deadline = new Deadline("return book", "2026-08-26");
        deadline.markAsDone();

        storage.saveTasks(List.of(new Todo("borrow book"), deadline,
                new Event("project meeting", "2026-08-28", "2026-08-29")));

        String expected = String.join(System.lineSeparator(),
                "T2 | 0 | Ym9ycm93IGJvb2s=",
                "D2 | 1 | cmV0dXJuIGJvb2s= | 2026-08-26",
                "E2 | 0 | cHJvamVjdCBtZWV0aW5n | 2026-08-28 | 2026-08-29", "");
        assertEquals(expected, Files.readString(storagePath));
        try (Stream<Path> remainingFiles = Files.list(this.tempDirectory)) {
            assertEquals(List.of(storagePath), remainingFiles.toList());
        }
    }
}
