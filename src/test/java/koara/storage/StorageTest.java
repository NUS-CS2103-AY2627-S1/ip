package koara.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import koara.exception.KoaraException;
import koara.task.Deadline;
import koara.task.Event;
import koara.task.TaskList;
import koara.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDirectory;

    @Test
    public void load_missingFile_returnsEmptyTaskList() throws KoaraException {
        Storage storage = new Storage(tempDirectory.resolve("missing.txt"));

        TaskList tasks = storage.load();

        assertEquals(0, tasks.getSize());
    }

    @Test
    public void saveAndLoad_mixedTasks_preservesData() throws KoaraException {
        Path filePath = tempDirectory.resolve("data").resolve("koara.txt");
        Storage storage = new Storage(filePath);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        tasks.add(new Event("project meeting", LocalDate.of(2019, 12, 2), LocalDate.of(2019, 12, 3)));
        tasks.mark(1);

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertTrue(Files.exists(filePath));
        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2019-10-15",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03"
        ), loadedTasks.toDataLines());
    }

    @Test
    public void load_invalidData_throwsKoaraException() throws IOException {
        Path filePath = tempDirectory.resolve("koara.txt");
        Files.writeString(filePath, "invalid task data");
        Storage storage = new Storage(filePath);

        assertThrows(KoaraException.class, storage::load);
    }

    @Test
    public void load_duplicateOrReversedEventData_throwsKoaraException() throws IOException {
        Path filePath = tempDirectory.resolve("koara.txt");
        Storage storage = new Storage(filePath);

        Files.writeString(filePath, "T | 0 | read book\nT | 1 | read book");
        assertThrows(KoaraException.class, storage::load);

        Files.writeString(filePath, "E | 0 | trip | 2026-09-14 | 2026-09-13");
        assertThrows(KoaraException.class, storage::load);
    }

    @Test
    public void save_pathIsDirectory_throwsKoaraException() throws IOException {
        Path directoryPath = Files.createDirectory(tempDirectory.resolve("koara.txt"));
        Storage storage = new Storage(directoryPath);

        assertThrows(KoaraException.class, () -> storage.save(new TaskList()));
    }

    @Test
    public void load_invalidTaskVariants_throwKoaraException() throws IOException {
        Path filePath = tempDirectory.resolve("koara.txt");
        Storage storage = new Storage(filePath);
        List<String> invalidLines = List.of(
                "X | 0 | task",
                "T | 2 | task",
                "T | 0 | ",
                "T | 0 | task | extra",
                "D | 0 | deadline",
                "D | 0 | deadline | tomorrow",
                "E | 0 | event | 2026-09-13",
                "E | 0 | event | 2026-09-13 | tomorrow"
        );

        for (String invalidLine : invalidLines) {
            Files.writeString(filePath, invalidLine);
            assertThrows(KoaraException.class, storage::load);
        }
    }

    @Test
    public void save_replacesExistingFileAndRemovesTemporaryFile() throws Exception {
        Path filePath = tempDirectory.resolve("data").resolve("koara.txt");
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, "old data");
        Storage storage = new Storage(filePath);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("new task"));

        storage.save(tasks);

        assertEquals(List.of("T | 0 | new task"), Files.readAllLines(filePath));
        try (Stream<Path> savedFiles = Files.list(filePath.getParent())) {
            assertEquals(1, savedFiles.count());
        }
    }

    @Test
    public void constructor_nullPath_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Storage(null));
    }
}
