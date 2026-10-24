package kiki.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kiki.exception.KikiException;
import kiki.task.Deadlines;
import kiki.task.Events;
import kiki.task.Priority;
import kiki.task.TaskList;
import kiki.task.ToDos;
import kiki.ui.Ui;

public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void saveThenLoad_tasksRoundTrip() throws Exception {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("read book"));
        tasks.add(new Deadlines("pay rent", LocalDateTime.of(2035, 3, 2, 18, 0)));
        tasks.add(new Events("team meeting", LocalDateTime.of(2035, 3, 3, 10, 0),
                LocalDateTime.of(2035, 3, 3, 11, 0)));
        tasks.get(0).markAsDone();

        storage.save(tasks);
        TaskList loadedTasks = new TaskList();
        storage.load(loadedTasks, createUi(new ByteArrayOutputStream()));

        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertEquals(LocalDateTime.of(2035, 3, 2, 18, 0), ((Deadlines) loadedTasks.get(1)).getBy());
        assertEquals(LocalDateTime.of(2035, 3, 3, 11, 0), ((Events) loadedTasks.get(2)).getTo());
    }

    @Test
    public void load_corruptedRow_skipsItAndLoadsValidRows() throws Exception {
        Path saveFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "T | 0 | read book\nX | 0 | invalid\nT | 0 | walk dog\n");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskList tasks = new TaskList();

        new Storage(saveFile).load(tasks, createUi(output));

        assertEquals(2, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("walk dog", tasks.get(1).getDescription());
        assertTrue(output.toString().contains("Skipped a corrupted saved task: unknown saved task type."));
    }

    @Test
    public void load_reversedEventRow_skipsItWithWarning() throws Exception {
        Path saveFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(saveFile,
                "E | 0 | meeting | 2035-03-03T11:00 | 2035-03-03T10:00\n");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskList tasks = new TaskList();

        new Storage(saveFile).load(tasks, createUi(output));

        assertEquals(0, tasks.size());
        assertTrue(output.toString().contains("start must be before end"));
    }

    @Test
    public void load_missingFile_startsWithEmptyListWithoutWarning() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskList tasks = new TaskList();

        new Storage(tempDirectory.resolve("missing.txt")).load(tasks, createUi(output));

        assertEquals(0, tasks.size());
        assertTrue(output.toString().isEmpty());
    }

    @Test
    public void load_legacyTaskWithoutPriority_defaultsToNormal() throws IOException {
        Path saveFile = tempDirectory.resolve("legacy.txt");
        Files.writeString(saveFile, "T | 0 | read book\n");
        TaskList tasks = new TaskList();

        new Storage(saveFile).load(tasks, new Ui());

        assertEquals(Priority.NORMAL, tasks.get(0).getPriority());
    }

    @Test
    public void save_savePathIsDirectory_throwsHelpfulException() {
        KikiException exception = assertThrows(KikiException.class,
                () -> new Storage(tempDirectory).save(new TaskList()));

        assertEquals("Unable to save tasks because the save path is a folder.", exception.getMessage());
    }

    private Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
    }
}
