package vector.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import vector.task.Deadline;
import vector.task.Event;
import vector.task.Task;
import vector.task.Todo;

public class StorageTest {

    private static final String TEMP_FILE_PATH = "temp_test_data.txt";
    private Storage storage;

    @BeforeEach
    public void setUp() {
        File file = new File(TEMP_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        storage = new Storage(TEMP_FILE_PATH);
    }

    @AfterEach
    public void tearDown() {
        File file = new File(TEMP_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void load_validFile_loadsCorrectly() {
        try {
            FileWriter fw = new FileWriter(TEMP_FILE_PATH);
            fw.write("T | 1 | read book\n");
            fw.write("D | 0 | return book | 2023-06-06 2359\n");
            fw.write("E | 0 | project meeting | 2023-08-01 | 2023-08-02\n");
            fw.close();
        } catch (IOException e) {
            fail("Failed to setup test file.");
        }

        ArrayList<Task> tasks = new ArrayList<>();
        try {
            tasks = storage.load();
        } catch (vector.VectorException e) {
            fail("Exception thrown on valid file.");
        }
        assertEquals(3, tasks.size());

        // Validate Todo
        assertTrue(tasks.get(0) instanceof Todo);
        assertTrue(tasks.get(0).getStatusIcon().equals("X"));

        // Validate Deadline
        assertTrue(tasks.get(1) instanceof Deadline);
        assertEquals("return book", tasks.get(1).getDescription());

        // Validate Event
        assertTrue(tasks.get(2) instanceof Event);
        assertEquals("project meeting", tasks.get(2).getDescription());
    }

    @Test
    public void load_fileNotFound_returnsEmptyList() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            tasks = storage.load();
        } catch (vector.VectorException e) {
            fail("Exception thrown on file not found.");
        }
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_corruptedLines_throwsException() {
        try {
            FileWriter fw = new FileWriter(TEMP_FILE_PATH);
            fw.write("T | 1 | read book\n");
            fw.write("INVALID | LINE\n"); // Corrupted line
            fw.write("T | 0 | buy groceries\n");
            fw.close();
        } catch (IOException e) {
            fail("Failed to setup test file.");
        }

        try {
            storage.load();
            fail("Should have thrown VectorException for corrupted data.");
        } catch (vector.VectorException e) {
            assertTrue(e.getMessage().contains("Corrupted data found"));
        }
    }

    @Test
    public void save_validTasks_savesCorrectly() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        try {
            Deadline d = new Deadline("return book", "2023-06-06 2359");
            d.markAsDone();
            tasks.add(d);
        } catch (Exception e) {
            fail("Failed to create deadline task");
        }

        try {
            storage.save(tasks);
        } catch (vector.VectorException e) {
            fail("Exception thrown on save.");
        }

        try {
            String content = Files.readString(Path.of(TEMP_FILE_PATH));
            assertTrue(content.contains("T | 0 | read book"));
            assertTrue(content.contains("D | 1 | return book | 2023-06-06 2359"));
        } catch (IOException e) {
            fail("Failed to read saved file.");
        }
    }
}
