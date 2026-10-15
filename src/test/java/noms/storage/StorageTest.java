package noms.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import noms.task.Deadline;
import noms.task.Event;
import noms.task.Task;
import noms.task.ToDo;

/**
 * Tests {@link Storage}, the persistence layer. These cases use a JUnit
 * {@link TempDir temporary directory} so no real save file is touched.
 *
 * Persistence is critical, core logic: it must round-trip every task type
 * without loss, tolerate a corrupted save file rather than crash on startup,
 * and preserve descriptions containing the {@code |}/{@code \} characters
 * used by the file format itself. Each of those properties is checked here.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    private Storage storageIn(Path dir) {
        return new Storage(dir.toString(), "noms.txt");
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws IOException {
        // First run on a fresh machine: no save file yet is not an error.
        assertEquals(List.of(), storageIn(tempDir).load());
    }

    @Test
    public void saveThenLoad_roundTripsAllTaskTypes() throws IOException {
        Storage storage = storageIn(tempDir);
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        deadline.markAsDone();
        List<Task> original = List.of(
                new ToDo("read book"),
                deadline,
                new Event("meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7)));

        storage.save(original);
        List<Task> loaded = storage.load();

        // Compare via the save-file form: it captures type, done-flag, and dates.
        assertEquals(fileForms(original), fileForms(loaded));
    }

    @Test
    public void saveThenLoad_preservesPipeAndBackslashInDescription() throws IOException {
        Storage storage = storageIn(tempDir);
        List<Task> original = List.of(
                new ToDo("buy milk | bread"),
                new Deadline("audit C:\\logs\\a\\|b", LocalDate.of(2019, 11, 8)));

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals("buy milk | bread", loaded.get(0).getDescription());
        assertEquals("audit C:\\logs\\a\\|b", loaded.get(1).getDescription());
    }

    @Test
    public void load_unparseableLine_isSkippedAndRecorded() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        Files.writeString(file,
                "T | 0 | good task\n"
                        + "GARBAGE LINE\n"
                        + "D | 1 | pay rent | 2019-12-15\n");
        Storage storage = storageIn(tempDir);

        List<Task> loaded = storage.load();

        // The two good tasks load; the bad line is skipped, not fatal.
        assertEquals(2, loaded.size());
        assertEquals("good task", loaded.get(0).getDescription());
        assertEquals("pay rent", loaded.get(1).getDescription());
        assertEquals(List.of("GARBAGE LINE"), storage.getSkippedLines());
    }

    @Test
    public void load_blankLines_areIgnored() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        Files.writeString(file, "\nT | 0 | read book\n\n");
        Storage storage = storageIn(tempDir);

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(List.of(), storage.getSkippedLines());
    }

    @Test
    public void save_emptyList_overwritesExistingFileWithEmptyContent() throws IOException {
        Storage storage = storageIn(tempDir);
        storage.save(List.of(new ToDo("read book")));

        storage.save(List.of());

        assertEquals("", Files.readString(tempDir.resolve("noms.txt")));
        assertEquals(List.of(), storage.load());
    }

    @Test
    public void save_existingFile_replacesOldTasks() throws IOException {
        Storage storage = storageIn(tempDir);
        storage.save(List.of(new ToDo("old task")));

        storage.save(List.of(new ToDo("replacement task")));

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("replacement task", loaded.get(0).getDescription());
    }

    @Test
    public void save_replacementFails_preservesExistingFileAndCleansTemporaryFile()
            throws IOException {
        Path saveFile = tempDir.resolve("noms.txt");
        String originalContent = "T | 0 | original task\n";
        Files.writeString(saveFile, originalContent);
        Storage failingStorage = new Storage(tempDir.toString(), "noms.txt") {
            @Override
            protected void replaceSaveFile(Path temporaryFile, Path targetFile)
                    throws IOException {
                throw new IOException("Simulated replacement failure");
            }
        };

        assertThrows(IOException.class,
                () -> failingStorage.save(List.of(new ToDo("replacement task"))));
        assertEquals(originalContent, Files.readString(saveFile));
        try (Stream<Path> directoryContents = Files.list(tempDir)) {
            assertEquals(List.of(saveFile), directoryContents.toList());
        }
    }

    @Test
    public void load_unknownTaskType_skipsAndRecordsLine() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        String unknownTask = "X | 0 | mysterious task";
        Files.writeString(file, unknownTask);
        Storage storage = storageIn(tempDir);

        assertEquals(List.of(), storage.load());
        assertEquals(List.of(unknownTask), storage.getSkippedLines());
    }

    @Test
    public void load_invalidCompletionFlag_skipsAndRecordsLine() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        String invalidTask = "T | 2 | read book";
        Files.writeString(file, invalidTask);
        Storage storage = storageIn(tempDir);

        assertEquals(List.of(), storage.load());
        assertEquals(List.of(invalidTask), storage.getSkippedLines());
    }

    @Test
    public void load_unexpectedExtraField_skipsAndRecordsLine() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        String invalidTask = "T | 0 | read book | unexpected";
        Files.writeString(file, invalidTask);
        Storage storage = storageIn(tempDir);

        assertEquals(List.of(), storage.load());
        assertEquals(List.of(invalidTask), storage.getSkippedLines());
    }

    @Test
    public void load_emptyDescription_skipsAndRecordsLine() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        String invalidTask = "T | 0 | ";
        Files.writeString(file, invalidTask);
        Storage storage = storageIn(tempDir);

        assertEquals(List.of(), storage.load());
        assertEquals(List.of(invalidTask), storage.getSkippedLines());
    }

    @Test
    public void load_duplicateDetails_skipsLaterRecordIgnoringCompletion() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        String duplicateTask = "T | 1 | read book";
        Files.writeString(file, "T | 0 | read book\n" + duplicateTask + "\n");
        Storage storage = storageIn(tempDir);

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(" ", loaded.get(0).getStatusIcon());
        assertEquals(List.of(duplicateTask), storage.getSkippedLines());
    }

    @Test
    public void load_invalidDate_skipsAndRecordsLine() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        String invalidDeadline = "D | 0 | submit report | tomorrow";
        Files.writeString(file, invalidDeadline);
        Storage storage = storageIn(tempDir);

        assertEquals(List.of(), storage.load());
        assertEquals(List.of(invalidDeadline), storage.getSkippedLines());
    }

    @Test
    public void load_eventWithInvalidDateRange_skipsAndRecordsLine() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        String invalidEvent = "E | 0 | trip | 2026-10-05 | 2026-10-01";
        Files.writeString(file, invalidEvent);
        Storage storage = storageIn(tempDir);

        assertEquals(List.of(), storage.load());
        assertEquals(List.of(invalidEvent), storage.getSkippedLines());
    }

    @Test
    public void load_malformedEscapedDescription_skipsAndRecordsLine() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        String malformedTask = "T | 0 | bad\\qescape";
        Files.writeString(file, malformedTask);
        Storage storage = storageIn(tempDir);

        assertEquals(List.of(), storage.load());
        assertEquals(List.of(malformedTask), storage.getSkippedLines());
    }

    @Test
    public void load_multipleSpoiledLines_recordsThemInOrder() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        String unknownTask = "X | 0 | mysterious task";
        String invalidDeadline = "D | 0 | submit report | tomorrow";
        Files.writeString(file, unknownTask + "\n" + invalidDeadline + "\n");
        Storage storage = storageIn(tempDir);

        storage.load();

        assertEquals(List.of(unknownTask, invalidDeadline), storage.getSkippedLines());
    }

    @Test
    public void load_secondTime_clearsPreviouslySkippedLines() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        Storage storage = storageIn(tempDir);
        Files.writeString(file, "GARBAGE LINE");
        storage.load();
        Files.writeString(file, "T | 0 | valid task");

        storage.load();

        assertEquals(List.of(), storage.getSkippedLines());
    }

    @Test
    public void getSkippedLines_returnedListCannotModifyInternalState() throws IOException {
        Path file = tempDir.resolve("noms.txt");
        Files.writeString(file, "GARBAGE LINE");
        Storage storage = storageIn(tempDir);
        storage.load();

        List<String> skippedLines = storage.getSkippedLines();
        skippedLines.clear();

        assertEquals(List.of("GARBAGE LINE"), storage.getSkippedLines());
    }

    /** Maps tasks to their save-file lines, for comparing task lists by content. */
    private static List<String> fileForms(List<Task> tasks) {
        return tasks.stream().map(Task::toFileFormat).toList();
    }
}
