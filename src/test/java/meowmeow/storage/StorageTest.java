package meowmeow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import meowmeow.MeowMeowException;
import meowmeow.task.Deadline;
import meowmeow.task.Event;
import meowmeow.task.Task;
import meowmeow.task.TaskDateTime;
import meowmeow.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void load_fileDoesNotExist_emptyListReturned() throws MeowMeowException {
        Storage storage = new Storage(tempDir.resolve("nothing.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_allTaskTypes_tasksPreserved() throws MeowMeowException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());

        Todo todo = new Todo("read book");
        todo.markAsDone();
        ArrayList<Task> original = new ArrayList<>();
        original.add(todo);
        original.add(new Deadline("submit report", TaskDateTime.parse("2099-12-02 1800")));
        original.add(new Event("conference",
                TaskDateTime.parse("2099-12-05"),
                TaskDateTime.parse("2099-12-07")));

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(original.size(), loaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toString(), loaded.get(i).toString());
        }
    }

    @Test
    public void load_corruptedLines_validTasksStillLoaded() throws MeowMeowException, IOException {
        Path file = tempDir.resolve("mixed.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "T | 0 | read book",
                "this is not a task",
                "D | 0 | no date after this",
                "X | 0 | unknown type",
                "T | 9 | bad done flag",
                "T | 1 | return book"));

        ArrayList<Task> loaded = new Storage(file.toString()).load();

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[T][X] return book", loaded.get(1).toString());
    }

    @Test
    public void load_blankLines_ignored() throws MeowMeowException, IOException {
        Path file = tempDir.resolve("blanks.txt");
        Files.writeString(file, System.lineSeparator() + "T | 0 | read book"
                + System.lineSeparator() + "   " + System.lineSeparator());

        assertEquals(1, new Storage(file.toString()).load().size());
    }

    @Test
    public void save_parentDirectoryMissing_directoryCreated() throws MeowMeowException {
        Path file = tempDir.resolve("nested").resolve("deeper").resolve("tasks.txt");

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        new Storage(file.toString()).save(tasks);

        assertTrue(Files.exists(file));
    }
}