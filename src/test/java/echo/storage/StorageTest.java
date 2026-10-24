package echo.storage; // same package as the class being tested

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import echo.task.Deadline;
import echo.task.Task;
import echo.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void saveThenLoad_mixedTasks_roundTripsExactly() {
        Storage storage = new Storage(tempDir.resolve("echo.txt").toString());
        Todo doneTodo = new Todo("read book");
        doneTodo.markAsDone();
        List<Task> tasks = List.of(doneTodo, new Deadline("return book", LocalDate.of(2019, 6, 6)));

        storage.save(tasks);
        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", loaded.get(1).toString());
    }

    @Test
    public void load_corruptedLineAmongValidOnes_skipsOnlyTheCorruptedLine() throws IOException {
        Path saveFile = tempDir.resolve("echo.txt");
        Files.write(saveFile, List.of(
                "T | 0 | read book",
                "X | 0 | this line has an unknown task type",
                "T | 0 | write essay"));
        Storage storage = new Storage(saveFile.toString());

        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[T][ ] write essay", loaded.get(1).toString());
    }

    @Test
    public void load_missingFileAndFolder_createsThemAndReturnsEmptyList() {
        Path saveFile = tempDir.resolve("nested/dir/echo.txt");
        Storage storage = new Storage(saveFile.toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
        assertTrue(Files.exists(saveFile));
    }

    @Test
    public void archive_nonEmptyList_appendsToArchiveFile() throws IOException {
        Storage storage = new Storage(tempDir.resolve("echo.txt").toString());
        List<Task> tasks = List.of(new Todo("read book"), new Todo("write essay"));

        storage.archive(tasks);

        Path archiveFile = tempDir.resolve("archive.txt");
        List<String> lines = Files.readAllLines(archiveFile);
        assertEquals(List.of("T | 0 | read book", "T | 0 | write essay"), lines);
    }

    @Test
    public void archive_calledTwice_accumulatesAcrossCalls() throws IOException {
        Storage storage = new Storage(tempDir.resolve("echo.txt").toString());

        storage.archive(List.of(new Todo("first task")));
        storage.archive(List.of(new Todo("second task")));

        Path archiveFile = tempDir.resolve("archive.txt");
        List<String> lines = Files.readAllLines(archiveFile);
        assertEquals(List.of("T | 0 | first task", "T | 0 | second task"), lines);
    }
}
