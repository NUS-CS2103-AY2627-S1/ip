package ace.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TaskManager.addTask/markTask/unmarkTask/deleteTask all call Storage.save(), which
 * writes to the real data/Ace.txt on disk (Storage's save path isn't configurable).
 * To avoid these tests clobbering whatever the developer actually has saved there,
 * each test backs up that file beforehand and restores it afterward.
 */
public class TaskManagerTest {
    private static final Path SAVE_FILE = Paths.get("data", "Ace.txt");
    private byte[] backup;
    private boolean wasFileExisting;

    @BeforeEach
    public void backUpRealSaveFile() throws IOException {
        wasFileExisting = Files.exists(SAVE_FILE);
        if (wasFileExisting) {
            backup = Files.readAllBytes(SAVE_FILE);
        }
    }

    @AfterEach
    public void restoreRealSaveFile() throws IOException {
        if (wasFileExisting) {
            Files.write(SAVE_FILE, backup);
        } else {
            Files.deleteIfExists(SAVE_FILE);
        }
    }

    @Test
    public void addTask_singleTask_incrementsCountAndIsRetrievable() {
        TaskManager taskManager = new TaskManager();
        Todo todo = new Todo("borrow book");

        taskManager.addTask(todo);

        assertEquals(1, taskManager.getCurrentNumberOfTasks());
        assertEquals(todo, taskManager.getTask(0));
    }

    @Test
    public void deleteTask_middleElement_shiftsLaterTasksLeft() {
        TaskManager taskManager = new TaskManager();
        Todo first = new Todo("A");
        Todo second = new Todo("B");
        Todo third = new Todo("C");
        taskManager.addTask(first);
        taskManager.addTask(second);
        taskManager.addTask(third);

        taskManager.deleteTask(1); // delete "B"

        assertEquals(2, taskManager.getCurrentNumberOfTasks());
        assertEquals(first, taskManager.getTask(0));
        assertEquals(third, taskManager.getTask(1));
    }

    @Test
    public void deleteTask_firstElement_shiftsAllTasksLeft() {
        TaskManager taskManager = new TaskManager();
        Todo first = new Todo("A");
        Todo second = new Todo("B");
        taskManager.addTask(first);
        taskManager.addTask(second);

        taskManager.deleteTask(0);

        assertEquals(1, taskManager.getCurrentNumberOfTasks());
        assertEquals(second, taskManager.getTask(0));
    }

    @Test
    public void deleteTask_lastElement_noShiftNeeded() {
        TaskManager taskManager = new TaskManager();
        Todo first = new Todo("A");
        Todo second = new Todo("B");
        taskManager.addTask(first);
        taskManager.addTask(second);

        taskManager.deleteTask(1);

        assertEquals(1, taskManager.getCurrentNumberOfTasks());
        assertEquals(first, taskManager.getTask(0));
    }

    @Test
    public void deleteTask_onlyElement_listBecomesEmpty() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Todo("A"));

        taskManager.deleteTask(0);

        assertEquals(0, taskManager.getCurrentNumberOfTasks());
    }

    @Test
    public void markTask_incompleteTask_becomesCompleted() {
        TaskManager taskManager = new TaskManager();
        Todo todo = new Todo("borrow book");
        taskManager.addTask(todo);

        taskManager.markTask(0);

        assertTrue(taskManager.getTask(0).isCompleted());
    }

    @Test
    public void unmarkTask_completedTask_becomesIncomplete() {
        TaskManager taskManager = new TaskManager();
        Todo todo = new Todo("borrow book");
        taskManager.addTask(todo);
        taskManager.markTask(0);

        taskManager.unmarkTask(0);

        assertFalse(taskManager.getTask(0).isCompleted());
    }

    @Test
    public void printTasksByDate_matchingDeadlinePresent_listsIt() throws Exception {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Todo("not a deadline"));
        Deadline matching = new Deadline("return book", "2/12/2019 1800");
        taskManager.addTask(matching);

        String output = captureStdout(() -> taskManager.printTasksByDate(LocalDate.of(2019, 12, 2)));

        assertTrue(output.contains("return book"));
        assertFalse(output.contains("No cards due"));
    }

    @Test
    public void printTasksByDate_noMatchingDeadline_showsNoCardsMessage() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Todo("not a deadline"));

        String output = captureStdout(() -> taskManager.printTasksByDate(LocalDate.of(2019, 12, 2)));

        assertTrue(output.contains("No cards due on this date!"));
    }

    @Test
    public void printTasksByName_exactCaseMatch_listsMatchingTask() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Todo("read book"));
        taskManager.addTask(new Todo("write essay"));

        String output = captureStdout(() -> taskManager.printTasksByName("book"));

        assertTrue(output.contains("read book"));
        assertFalse(output.contains("write essay"));
    }

    @Test
    public void printTasksByName_differentCaseInKeywordAndTask_stillMatches() {
        // The match must be case-insensitive regardless of which side (the stored
        // description or the search keyword) is capitalized differently.
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Todo("Read BOOK"));

        String upperKeyword = captureStdout(() -> taskManager.printTasksByName("BOOK"));
        String lowerKeyword = captureStdout(() -> taskManager.printTasksByName("book"));
        String mixedKeyword = captureStdout(() -> taskManager.printTasksByName("BoOk"));

        assertTrue(upperKeyword.contains("Read BOOK"));
        assertTrue(lowerKeyword.contains("Read BOOK"));
        assertTrue(mixedKeyword.contains("Read BOOK"));
    }

    @Test
    public void printTasksByName_partialSubstringMatch_listsMatchingTask() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Todo("read book"));

        String output = captureStdout(() -> taskManager.printTasksByName("boo"));

        assertTrue(output.contains("read book"));
    }

    @Test
    public void printTasksByName_multipleMatches_listsAllInOrder() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Todo("read book"));
        taskManager.addTask(new Todo("write essay"));
        taskManager.addTask(new Todo("return book"));

        String output = captureStdout(() -> taskManager.printTasksByName("book"));

        assertTrue(output.contains(" 1.") && output.contains("read book"));
        assertTrue(output.contains(" 2.") && output.contains("return book"));
        assertFalse(output.contains("write essay"));
    }

    @Test
    public void printTasksByName_noMatch_showsNoSuchCardsMessage() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Todo("read book"));

        String output = captureStdout(() -> taskManager.printTasksByName("zzz"));

        assertTrue(output.contains("No such cards in your hand!"));
    }

    @Test
    public void printTasks_emptyList_showsEmptyHandMessage() {
        TaskManager taskManager = new TaskManager();

        String output = captureStdout(taskManager::printTasks);

        assertTrue(output.contains("Your hand is currently empty!"));
    }

    @Test
    public void printTasks_populatedList_listsAllTasksNumbered() {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Todo("first"));
        taskManager.addTask(new Todo("second"));

        String output = captureStdout(taskManager::printTasks);

        assertTrue(output.contains(" 1.") && output.contains("first"));
        assertTrue(output.contains(" 2.") && output.contains("second"));
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static String captureStdout(ThrowingRunnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
        }
        return captured.toString();
    }
}
