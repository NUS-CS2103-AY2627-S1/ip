package gunna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gunna.task.Deadline;
import gunna.task.Event;
import gunna.task.Task;
import gunna.task.Todo;

/**
 * JUnit test class for testing the Storage class.
 * Tests cover loading, saving, parsing, and data integrity operations.
 */
public class StorageTest {
    private static final String TEST_FILE_PATH = "data/test_tasks.txt";
    private Storage storage;
    private File testFile;

    @BeforeEach
    public void setUp() {
        storage = new Storage(TEST_FILE_PATH);
        testFile = new File(TEST_FILE_PATH);
        // Clean up any existing test file
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @AfterEach
    public void tearDown() {
        // Clean up test file and directory after each test
        if (testFile.exists()) {
            testFile.delete();
        }
        File parentDir = testFile.getParentFile();
        if (parentDir != null && parentDir.exists() && parentDir.list().length == 0) {
            parentDir.delete();
        }
    }

    // Tests for loadTasks()

    @Test
    public void loadTasks_nonExistentFile_returnsEmptyList() {
        ArrayList<Task> tasks = storage.loadTasks();
        assertNotNull(tasks, "loadTasks should return a list, not null");
        assertEquals(0, tasks.size(), "Loading from non-existent file should return empty list");
    }

    @Test
    public void loadTasks_emptyFile_returnsEmptyList() throws IOException {
        createTestFile("");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(0, tasks.size(), "Loading from empty file should return empty list");
    }

    @Test
    public void loadTasks_validTodoTask_loadsCorrectly() throws IOException {
        createTestFile("T | 0 | Buy milk");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size(), "Should load one task");
        assertTrue(tasks.get(0) instanceof Todo, "Task should be a Todo");
        assertEquals("Buy milk", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone(), "Task should not be marked as done");
    }

    @Test
    public void loadTasks_completedTodoTask_loadsWithDoneStatus() throws IOException {
        createTestFile("T | 1 | Buy milk");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isDone(), "Task should be marked as done");
    }

    @Test
    public void loadTasks_validDeadlineTask_loadsCorrectly() throws IOException {
        createTestFile("D | 0 | Submit report | 2024-06-15");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Deadline, "Task should be a Deadline");
        assertEquals("Submit report", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void loadTasks_validEventTask_loadsCorrectly() throws IOException {
        createTestFile("E | 0 | Team meeting | 2pm | 4pm");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Event, "Task should be an Event");
        assertEquals("Team meeting", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void loadTasks_multipleTasks_loadsAllCorrectly() throws IOException {
        createTestFile("T | 0 | Buy milk\n"
                + "D | 1 | Submit report | 2024-06-15\n"
                + "E | 0 | Team meeting | 2pm | 4pm");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(3, tasks.size(), "Should load all three tasks");
        assertTrue(tasks.get(0) instanceof Todo);
        assertTrue(tasks.get(1) instanceof Deadline);
        assertTrue(tasks.get(2) instanceof Event);
        assertTrue(tasks.get(1).isDone(), "Second task should be marked as done");
    }

    @Test
    public void loadTasks_taskWithEscapedPipe_loadsCorrectly() throws IOException {
        createTestFile("T | 0 | Task with \\| pipe character");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size());
        assertEquals("Task with | pipe character", tasks.get(0).getDescription(),
                "Should unescape pipe character");
    }

    @Test
    public void loadTasks_taskWithEscapedBackslash_loadsCorrectly() throws IOException {
        createTestFile("T | 0 | Task with \\\\ backslash");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size());
        assertEquals("Task with \\ backslash", tasks.get(0).getDescription(),
                "Should unescape backslash");
    }

    @Test
    public void loadTasks_taskWithEscapedNewline_loadsCorrectly() throws IOException {
        createTestFile("T | 0 | Task with \\n newline");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size());
        assertEquals("Task with \n newline", tasks.get(0).getDescription(),
                "Should unescape newline character");
    }

    @Test
    public void loadTasks_corruptedLine_skipsLine() throws IOException {
        createTestFile("T | 0 | Valid task\n"
                + "INVALID LINE\n"
                + "D | 0 | Another valid task | 2024-06-15");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(2, tasks.size(), "Should skip corrupted line and load valid tasks");
        assertTrue(tasks.get(0) instanceof Todo);
        assertTrue(tasks.get(1) instanceof Deadline);
    }

    @Test
    public void loadTasks_invalidStatus_skipsTask() throws IOException {
        createTestFile("T | X | Invalid status task\n"
                + "T | 0 | Valid task");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size(), "Should skip task with invalid status");
        assertEquals("Valid task", tasks.get(0).getDescription());
    }

    @Test
    public void loadTasks_todoWithExtraFields_skipsTask() throws IOException {
        createTestFile("T | 0 | Todo task | extra field\n"
                + "T | 0 | Valid task");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size(), "Should skip todo with extra fields");
        assertEquals("Valid task", tasks.get(0).getDescription());
    }

    @Test
    public void loadTasks_deadlineMissingBy_skipsTask() throws IOException {
        createTestFile("D | 0 | Deadline without by\n"
                + "T | 0 | Valid task");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size(), "Should skip deadline missing /by field");
    }

    @Test
    public void loadTasks_eventMissingToField_skipsTask() throws IOException {
        createTestFile("E | 0 | Event without to | 2pm\n"
                + "T | 0 | Valid task");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size(), "Should skip event missing /to field");
    }

    @Test
    public void loadTasks_emptyDescription_skipsTask() throws IOException {
        createTestFile("T | 0 | \n"
                + "T | 0 | Valid task");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size(), "Should skip task with empty description");
    }

    @Test
    public void loadTasks_whitespaceLinesSkipped() throws IOException {
        createTestFile("T | 0 | Task 1\n\n   \n\t\nD | 0 | Task 2 | 2024-06-15");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(2, tasks.size(), "Should skip whitespace-only lines");
    }

    @Test
    public void loadTasks_unknownTaskType_skipsTask() throws IOException {
        createTestFile("X | 0 | Unknown type task\n"
                + "T | 0 | Valid task");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size(), "Should skip task with unknown type");
    }

    @Test
    public void loadTasks_invalidDateFormat_skipsTask() throws IOException {
        createTestFile("D | 0 | Task with invalid date | invalid-date\n"
                + "T | 0 | Valid task");
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(1, tasks.size(), "Should skip deadline with invalid date format");
    }

    // Tests for saveTasks()

    @Test
    public void saveTasks_emptyList_createsEmptyFile() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        storage.saveTasks(tasks);
        assertTrue(testFile.exists(), "File should be created");
        assertEquals(0, Files.readAllLines(testFile.toPath()).size(),
                "File should be empty for empty task list");
    }

    @Test
    public void saveTasks_singleTodoTask_savesCorrectly() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("Buy milk"));
        storage.saveTasks(tasks);

        assertTrue(testFile.exists(), "File should be created");
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(testFile.toPath());
        assertEquals(1, lines.size(), "Should have one line");
        assertEquals("T | 0 | Buy milk", lines.get(0));
    }

    @Test
    public void saveTasks_completedTodoTask_savesWithDoneStatus() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        Task todo = new Todo("Buy milk");
        todo.markAsDone();
        tasks.add(todo);
        storage.saveTasks(tasks);

        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(testFile.toPath());
        assertEquals("T | 1 | Buy milk", lines.get(0), "Should save with status 1");
    }

    @Test
    public void saveTasks_deadlineTask_savesCorrectly() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Deadline("Submit report", LocalDate.of(2024, 6, 15)));
        storage.saveTasks(tasks);

        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(testFile.toPath());
        assertEquals("D | 0 | Submit report | 2024-06-15", lines.get(0));
    }

    @Test
    public void saveTasks_eventTask_savesCorrectly() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Event("Team meeting", "2pm", "4pm"));
        storage.saveTasks(tasks);

        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(testFile.toPath());
        assertEquals("E | 0 | Team meeting | 2pm | 4pm", lines.get(0));
    }

    @Test
    public void saveTasks_multipleTasks_savesAllCorrectly() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("Buy milk"));
        Task deadline = new Deadline("Submit report", LocalDate.of(2024, 6, 15));
        deadline.markAsDone();
        tasks.add(deadline);
        tasks.add(new Event("Team meeting", "2pm", "4pm"));
        storage.saveTasks(tasks);

        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(testFile.toPath());
        assertEquals(3, lines.size());
        assertEquals("T | 0 | Buy milk", lines.get(0));
        assertEquals("D | 1 | Submit report | 2024-06-15", lines.get(1));
        assertEquals("E | 0 | Team meeting | 2pm | 4pm", lines.get(2));
    }

    @Test
    public void saveTasks_taskWithPipeCharacter_escapesCorrectly() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("Task with | pipe"));
        storage.saveTasks(tasks);

        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(testFile.toPath());
        assertEquals("T | 0 | Task with \\| pipe", lines.get(0),
                "Pipe character should be escaped");
    }

    @Test
    public void saveTasks_taskWithBackslash_escapesCorrectly() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("Task with \\ backslash"));
        storage.saveTasks(tasks);

        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(testFile.toPath());
        assertEquals("T | 0 | Task with \\\\ backslash", lines.get(0),
                "Backslash should be escaped");
    }

    @Test
    public void saveTasks_taskWithNewline_escapesCorrectly() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("Task with \n newline"));
        storage.saveTasks(tasks);

        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(testFile.toPath());
        assertEquals("T | 0 | Task with \\n newline", lines.get(0),
                "Newline should be escaped");
    }

    @Test
    public void saveTasks_createsDirectoryIfNotExists() throws IOException {
        String deepPath = "data/test/nested/tasks.txt";
        Storage deepStorage = new Storage(deepPath);
        File deepFile = new File(deepPath);

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("Test task"));
        deepStorage.saveTasks(tasks);

        assertTrue(deepFile.exists(), "File should be created with nested directories");

        // Clean up
        deepFile.delete();
        new File("data/test/nested").delete();
        new File("data/test").delete();
    }

    // Tests for round-trip persistence (save then load)

    @Test
    public void saveAndLoad_singleTask_preservesData() {
        ArrayList<Task> originalTasks = new ArrayList<>();
        Task todo = new Todo("Buy milk");
        todo.markAsDone();
        originalTasks.add(todo);

        storage.saveTasks(originalTasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("Buy milk", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(0).isDone());
    }

    @Test
    public void saveAndLoad_multipleTasks_preservesOrder() {
        ArrayList<Task> originalTasks = new ArrayList<>();
        originalTasks.add(new Todo("Task 1"));
        originalTasks.add(new Deadline("Task 2", LocalDate.of(2024, 6, 15)));
        originalTasks.add(new Event("Task 3", "2pm", "4pm"));

        storage.saveTasks(originalTasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("Task 1", loadedTasks.get(0).getDescription());
        assertEquals("Task 2", loadedTasks.get(1).getDescription());
        assertEquals("Task 3", loadedTasks.get(2).getDescription());
    }

    @Test
    public void saveAndLoad_taskWithSpecialCharacters_preservesCharacters() {
        ArrayList<Task> originalTasks = new ArrayList<>();
        originalTasks.add(new Todo("Task | with \\ special \n characters"));

        storage.saveTasks(originalTasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("Task | with \\ special \n characters",
                loadedTasks.get(0).getDescription(),
                "Special characters should be preserved through save/load cycle");
    }

    /**
     * Helper method to create a test file with the given content.
     */
    private void createTestFile(String content) throws IOException {
        File parentDir = testFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
            writer.write(content);
        }
    }
}
