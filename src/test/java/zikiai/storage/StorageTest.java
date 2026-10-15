package zikiai.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zikiai.exception.ZikiaiException;
import zikiai.task.Deadline;
import zikiai.task.Event;
import zikiai.task.Priority;
import zikiai.task.Task;
import zikiai.task.TaskList;
import zikiai.task.Todo;

/**
 * Tests loading and saving task priorities in {@link Storage}.
 */
class StorageTest {
    @TempDir
    private Path directory;

    @Test
    void load_missingFile_emptyListReturned() throws ZikiaiException {
        List<Task> tasks = new Storage(directory.resolve("missing/tasks.txt")).load();

        assertEquals(0, tasks.size());
    }

    @Test
    void load_blankLines_blankLinesIgnored() throws IOException, ZikiaiException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "\n[T][ ] | read book\n   \n");

        List<Task> tasks = new Storage(file).load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).getDescription());
    }

    @Test
    void save_missingParentDirectories_directoriesAndFileCreated()
            throws IOException, ZikiaiException {
        Path file = directory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(file);

        storage.save(new TaskList(List.of(new Todo("read book"))));

        assertEquals(List.of("[T][ ] | read book"), Files.readAllLines(file));
    }

    @Test
    void save_emptyTaskList_existingContentsTruncated()
            throws IOException, ZikiaiException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "[T][ ] | stale task\n");

        new Storage(file).save(new TaskList());

        assertEquals(List.of(), Files.readAllLines(file));
    }

    @Test
    void load_legacyTaskWithoutPriority_nonePriorityLoaded()
            throws IOException, ZikiaiException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "[T][X] | read book\n");

        List<Task> tasks = new Storage(file).load();

        assertEquals(1, tasks.size());
        assertEquals(Priority.NONE, tasks.get(0).getPriority());
        assertEquals("[T][X] read book", tasks.get(0).getDescription());
    }

    @Test
    void saveAndLoad_prioritizedTaskTypes_statePreserved()
            throws IOException, ZikiaiException {
        Todo todo = new Todo("read book");
        todo.setPriority(Priority.HIGH);
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 20));
        deadline.setPriority(Priority.MEDIUM);
        Event event = new Event("meeting", "2pm", "4pm");
        event.setPriority(Priority.LOW);
        event.markAsDone();
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file);

        storage.save(new TaskList(List.of(todo, deadline, event)));
        List<Task> loadedTasks = storage.load();

        assertEquals(
                List.of(
                        "[T][ ][H] | read book",
                        "[D][ ][M] | submit report | 2026-09-20",
                        "[E][X][L] | meeting | 2pm | 4pm"),
                Files.readAllLines(file));
        assertEquals("[T][HIGH][ ] read book", loadedTasks.get(0).getDescription());
        assertEquals(
                "[D][MEDIUM][ ] submit report (by: Sep 20 2026)",
                loadedTasks.get(1).getDescription());
        assertEquals(
                "[E][LOW][X] meeting (from: 2pm to: 4pm)",
                loadedTasks.get(2).getDescription());
    }

    @Test
    void load_unknownPriorityTag_exceptionThrown() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "[T][ ][U] | read book\n");

        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> new Storage(file).load());

        assertEquals(
                "I couldn't load the saved tasks because line 1 is invalid.",
                exception.getMessage());
    }

    @Test
    void load_malformedTaskFields_eachLineRejectedWithItsLineNumber() throws IOException {
        String[] invalidLines = {
            "[T][?] | invalid status",
            "[D][ ] | missing date",
            "[D][ ] | invalid date | 2025-02-29",
            "[E][ ] | missing end | 2pm",
            "[T][ ] | "
        };
        Path file = directory.resolve("tasks.txt");

        for (String invalidLine : invalidLines) {
            Files.writeString(file, "\n" + invalidLine + "\n");

            ZikiaiException exception = assertThrows(
                    ZikiaiException.class, () -> new Storage(file).load(), invalidLine);

            assertEquals(
                    "I couldn't load the saved tasks because line 2 is invalid.",
                    exception.getMessage(),
                    invalidLine);
        }
    }
}
