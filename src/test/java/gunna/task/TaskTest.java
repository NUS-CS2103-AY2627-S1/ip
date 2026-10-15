package gunna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for testing the Task class.
 * Tests cover task creation, marking/unmarking, and string representations.
 */
public class TaskTest {

    private Task task;

    /**
     * Sets up a fresh task before each test.
     * This ensures tests are independent and don't affect each other.
     */
    @BeforeEach
    public void setUp() {
        task = new Task("Sample task description");
    }

    @Test
    public void constructor_newTask_isNotDone() {
        Task newTask = new Task("Test task");
        assertFalse(newTask.isDone(), "New task should not be marked as done");
    }

    @Test
    public void constructor_newTask_hasCorrectDescription() {
        Task newTask = new Task("Read book");
        assertEquals("Read book", newTask.getDescription(),
                "Task description should match the constructor argument");
    }

    @Test
    public void getStatusIcon_notDoneTask_returnsSpace() {
        assertEquals(" ", task.getStatusIcon(),
                "Status icon for not done task should be a space");
    }

    @Test
    public void getStatusIcon_doneTask_returnsX() {
        task.markAsDone();
        assertEquals("X", task.getStatusIcon(),
                "Status icon for done task should be 'X'");
    }

    @Test
    public void markAsDone_notDoneTask_becomesTrue() {
        assertFalse(task.isDone(), "Task should initially not be done");
        task.markAsDone();
        assertTrue(task.isDone(), "Task should be marked as done after calling markAsDone()");
    }

    @Test
    public void markAsDone_alreadyDoneTask_remainsTrue() {
        task.markAsDone();
        assertTrue(task.isDone(), "Task should be done");
        task.markAsDone();
        assertTrue(task.isDone(), "Task should remain done after calling markAsDone() again");
    }

    @Test
    public void markAsNotDone_doneTask_becomesFalse() {
        task.markAsDone();
        assertTrue(task.isDone(), "Task should be done");
        task.markAsNotDone();
        assertFalse(task.isDone(), "Task should not be done after calling markAsNotDone()");
    }

    @Test
    public void markAsNotDone_notDoneTask_remainsFalse() {
        assertFalse(task.isDone(), "Task should initially not be done");
        task.markAsNotDone();
        assertFalse(task.isDone(), "Task should remain not done after calling markAsNotDone()");
    }

    @Test
    public void getDescription_newTask_returnsCorrectDescription() {
        assertEquals("Sample task description", task.getDescription(),
                "getDescription() should return the task description");
    }

    @Test
    public void isDone_newTask_returnsFalse() {
        assertFalse(task.isDone(), "New task should return false for isDone()");
    }

    @Test
    public void isDone_markedTask_returnsTrue() {
        task.markAsDone();
        assertTrue(task.isDone(), "Marked task should return true for isDone()");
    }

    @Test
    public void toFileFormat_notDoneTask_correctFormat() {
        Task testTask = new Task("Buy groceries");
        assertEquals("T | 0 | Buy groceries", testTask.toFileFormat(),
                "File format for not done task should have 0 as status indicator");
    }

    @Test
    public void toFileFormat_doneTask_correctFormat() {
        Task testTask = new Task("Buy groceries");
        testTask.markAsDone();
        assertEquals("T | 1 | Buy groceries", testTask.toFileFormat(),
                "File format for done task should have 1 as status indicator");
    }

    @Test
    public void toString_notDoneTask_correctFormat() {
        Task testTask = new Task("Return books");
        assertEquals("[ ] Return books", testTask.toString(),
                "String representation for not done task should show empty checkbox");
    }

    @Test
    public void toString_doneTask_correctFormat() {
        Task testTask = new Task("Return books");
        testTask.markAsDone();
        assertEquals("[X] Return books", testTask.toString(),
                "String representation for done task should show checked checkbox");
    }

    @Test
    public void markAsDone_afterMarkAsNotDone_becomesTrue() {
        task.markAsDone();
        task.markAsNotDone();
        task.markAsDone();
        assertTrue(task.isDone(),
                "Task should be done after mark-unmark-mark sequence");
    }

    @Test
    public void constructor_emptyDescription_handlesCorrectly() {
        Task emptyTask = new Task("");
        assertEquals("", emptyTask.getDescription(),
                "Task should handle empty description");
        assertEquals("[ ] ", emptyTask.toString(),
                "toString() should work with empty description");
    }

    @Test
    public void constructor_descriptionWithSpecialCharacters_handlesCorrectly() {
        Task specialTask = new Task("Task with /special |characters| and symbols!");
        assertEquals("Task with /special |characters| and symbols!",
                specialTask.getDescription(),
                "Task should preserve special characters in description");
    }
}
