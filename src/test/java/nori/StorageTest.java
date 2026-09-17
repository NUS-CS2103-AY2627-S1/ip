package nori;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nori.task.Deadline;
import nori.task.Event;
import nori.task.Todo;

class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void load_missingFile_returnsEmptyTaskList() throws NoriException {
        Storage storage = new Storage(tempDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_tasks_restoresTaskData() throws NoriException {
        Storage storage = new Storage(tempDirectory.resolve("nori.txt").toString());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.mark();
        tasks.add(todo);
        tasks.add(new Deadline("submit iP", "2026-08-28"));

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals("[D][ ] submit iP (by: Aug 28 2026)", loadedTasks.get(1).toString());
    }

    @Test
    void saveAndLoad_event_restoresAllEventFields() throws NoriException {
        Storage storage = new Storage(tempDirectory.resolve("nori.txt").toString());
        TaskList tasks = new TaskList();
        tasks.add(new Event("team meeting", "2pm", "3pm"));

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertEquals("[E][ ] team meeting (from: 2pm to: 3pm)", loadedTasks.get(0).toString());
    }

    @Test
    void load_unknownTaskType_throwsNoriException() throws IOException {
        Path saveFile = tempDirectory.resolve("nori.txt");
        Files.writeString(saveFile, "X | 0 | mystery");

        Storage storage = new Storage(saveFile.toString());

        assertThrows(NoriException.class, storage::load);
    }

    @Test
    void load_invalidCompletionStatus_throwsNoriException() throws IOException {
        Path saveFile = tempDirectory.resolve("nori.txt");
        Files.writeString(saveFile, "T | maybe | read book");

        Storage storage = new Storage(saveFile.toString());

        assertThrows(NoriException.class, storage::load);
    }

    @Test
    void load_blankTaskDescription_throwsNoriException() throws IOException {
        Path saveFile = tempDirectory.resolve("nori.txt");
        Files.writeString(saveFile, "T | 0 | ");

        Storage storage = new Storage(saveFile.toString());

        assertThrows(NoriException.class, storage::load);
    }
}
