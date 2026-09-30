package kong.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kong.task.Deadline;
import kong.task.Event;
import kong.task.Task;
import kong.task.Todo;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadTasksReturnsEmptyListForMissingFile() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void saveTasksWritesTasksThatCanBeLoadedFromNewNestedFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data/tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book", true));
        tasks.add(new Deadline("return book", LocalDate.parse("2019-10-15")));
        tasks.add(new Event("conference", true, LocalDate.parse("2019-10-14"), LocalDate.parse("2019-10-16")));

        storage.saveTasks(tasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertTrue(Files.exists(dataFile));
        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-10-15",
                "E | 1 | conference | 2019-10-14 | 2019-10-16"), Files.readAllLines(dataFile));
        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loadedTasks.get(1).toString());
        assertEquals("[E][X] conference (from: Oct 14 2019 to: Oct 16 2019)", loadedTasks.get(2).toString());
    }

    @Test
    void loadTasksLoadsOnlyValidTasksFromValidAndMalformedLines() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "T | 1 | read book",
                "D | invalid | return book | 2019-10-15",
                "E | 0 | conference | 2019-10-14 | 2019-10-16",
                "E | 0 | missing end | 2019-10-15",
                "X | 0 | mystery task"));
        Storage storage = new Storage(dataFile.toString());

        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Event.class, loadedTasks.get(1));
    }

    @Test
    void loadTasksSkipsDuplicateRecordsAndInvertedEventDates() throws IOException {
        Path dataFile = temporaryDirectory.resolve("corrupt_tasks.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "T | 1 | read book",
                "T | 0 | read book",
                "E | 0 | invalid event | 2019-10-20 | 2019-10-15",
                "D | 0 | non-existent date | 2019-02-30",
                "D | 0 | return book | 2019-10-15"));
        Storage storage = new Storage(dataFile.toString());

        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
    }

    @Test
    void loadTasksThrowsExceptionWhenFileIsDirectory() {
        Storage storage = new Storage(temporaryDirectory.toString());

        assertThrows(IOException.class, storage::loadTasks);
    }

    @Test
    void saveTasksThrowsExceptionWhenTargetIsDirectory() {
        Storage storage = new Storage(temporaryDirectory.toString());

        assertThrows(IOException.class, () -> storage.saveTasks(new ArrayList<>()));
    }

    @Test
    void loadTasks_skipsMalformedDoneStatusAndFieldCounts() throws IOException {
        Path dataFile = temporaryDirectory.resolve("malformed.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                " ",
                "T | 2 | invalid done status",
                "T | 0 | read book | extra field",
                "D | 0 | deadline | 2019-10-15 | extra",
                "E | 0 | event | 2019-10-15 | 2019-10-16 | extra",
                " | 0 | missing type",
                "T | 0 | ",
                "T | 1 | valid todo"));
        Storage storage = new Storage(dataFile.toString());

        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("[T][X] valid todo", loadedTasks.get(0).toString());
    }
}
