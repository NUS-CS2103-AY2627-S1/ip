package luigibot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import luigibot.task.Deadline;
import luigibot.task.Event;
import luigibot.task.Task;
import luigibot.task.TaskList;
import luigibot.task.Todo;
import luigibot.ui.Ui;

/**
 * Tests task persistence through {@link Storage} using temporary files.
 */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingSaveFile_emptyListReturned() {
        Storage storage = new Storage(this.temporaryDirectory.resolve("data/tasks.txt").toString());

        List<Task> loadedTasks = storage.load(new Ui());

        assertEquals(List.of(), loadedTasks);
    }

    @Test
    public void save_mixedTaskList_parentDirectoryCreatedAndExactDataWritten() throws IOException {
        Path savePath = this.temporaryDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(savePath.toString());
        Todo todo = new Todo("read book");
        todo.mark();
        TaskList tasks = new TaskList(List.of(
                todo,
                new Deadline("return book", "2026-08-27 1800"),
                new Event("meeting", "2026-08-27 1400", "2026-08-27 1600")));

        storage.save(tasks, new Ui());

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-08-27 1800",
                "E | 0 | meeting | 2026-08-27 1400 | 2026-08-27 1600"),
                Files.readAllLines(savePath));
    }

    @Test
    public void load_validTaskData_allTypesAndStatusesRestored() throws IOException {
        Path savePath = this.temporaryDirectory.resolve("data/tasks.txt");
        Files.createDirectories(savePath.getParent());
        Files.write(savePath, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-08-27 1800",
                "E | 1 | meeting | 2026-08-27 1400 | 2026-08-27 1600"));
        Storage storage = new Storage(savePath.toString());

        List<Task> loadedTasks = storage.load(new Ui());

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-08-27 1800",
                "E | 1 | meeting | 2026-08-27 1400 | 2026-08-27 1600"),
                loadedTasks.stream().map(Task::toFileString).toList());
    }

    @Test
    public void load_malformedLines_validTasksLoadedAndInvalidTasksSkipped() throws IOException {
        Path savePath = this.temporaryDirectory.resolve("data/tasks.txt");
        Files.createDirectories(savePath.getParent());
        Files.write(savePath, List.of(
                "T | 0 | read book",
                "X | 0 | unknown task",
                "T | 2 | invalid status",
                "D | 0 | missing date",
                "E | 0 | backwards meeting | 2026-08-27 1600 | 2026-08-27 1400",
                "D | 1 | return book | 2026-08-27 1800"));
        Storage storage = new Storage(savePath.toString());

        List<Task> loadedTasks = storage.load(new Ui());

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2026-08-27 1800"),
                loadedTasks.stream().map(Task::toFileString).toList());
    }
}
