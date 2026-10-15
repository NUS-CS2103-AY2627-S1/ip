package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests duplicate-task comparison, mutation and formatting behaviour in TaskList.
 */
public class TaskListTest {
    @Test
    public void containsDuplicate_ignoresCompletionState() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk", true));

        assertTrue(tasks.containsDuplicate(new Task("buy milk", false)));
    }

    @Test
    public void containsDuplicate_distinguishesTaskTypes() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("submit report"));

        assertFalse(tasks.containsDuplicate(new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 9, 9, 10, 0))));
    }

    @Test
    public void containsDuplicate_comparesDeadlineTime() {
        TaskList tasks = new TaskList();
        tasks.add(new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 9, 9, 10, 0)));

        assertFalse(tasks.containsDuplicate(new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 9, 9, 11, 0))));
    }

    @Test
    public void containsDuplicate_comparesEventTimes() {
        TaskList tasks = new TaskList();
        tasks.add(new EventTask(
                "team meeting",
                LocalDateTime.of(2026, 9, 9, 10, 0),
                LocalDateTime.of(2026, 9, 9, 11, 0)));

        assertTrue(tasks.containsDuplicate(new EventTask(
                "team meeting",
                false,
                LocalDateTime.of(2026, 9, 9, 10, 0),
                LocalDateTime.of(2026, 9, 9, 11, 0))));
    }

    @Test
    public void setTask_marksTaskAsDone() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));

        String result = tasks.setTask(0, true);

        assertEquals("ok, marked Task1 as done", result);
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void setTask_unmarksTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk", true));

        String result = tasks.setTask(0, false);

        assertEquals("ok, unmarked Task1", result);
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void setTask_invalidIndexReturnsError() {
        TaskList tasks = new TaskList();
        assertEquals("RIEEEEAAAU: INVALID INDEX!", tasks.setTask(0, true));
        assertEquals("RIEEEEAAAU: INVALID INDEX!", tasks.setTask(-1, true));
    }

    @Test
    public void addTask_appendsTaskAndReturnsConfirmation() {
        TaskList tasks = new TaskList();
        String result = tasks.addTask(new Task("buy milk"));

        assertEquals("Added: [T][ ] buy milk", result);
        assertEquals(1, tasks.size());
    }

    @Test
    public void removeTask_validIndexReturnsConfirmation() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));

        String result = tasks.removeTask(0);

        assertEquals("Removed: [T][ ] buy milk", result);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void removeTask_invalidIndexReturnsError() {
        TaskList tasks = new TaskList();
        assertEquals("RIEEEEAAAU: INVALID INDEX!", tasks.removeTask(0));
    }

    @Test
    public void findTasks_returnsMatchingTasksWithOriginalPositions() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));
        tasks.add(new Task("buy bread"));
        tasks.add(new Task("read book"));

        String result = tasks.findTasks("buy");

        assertEquals(
                "Here are the matching tasks in your list:\n"
                        + "1. [T][ ] buy milk\n"
                        + "2. [T][ ] buy bread",
                result);
    }

    @Test
    public void findTasks_isCaseInsensitive() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("Buy Milk"));

        assertEquals(
                "Here are the matching tasks in your list:\n1. [T][ ] Buy Milk",
                tasks.findTasks("milk"));
    }

    @Test
    public void findTasks_noMatchesReturnsError() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));

        assertEquals("RIEEEEAAAU: NO MATCHING TASKS!", tasks.findTasks("bicycle"));
    }

    @Test
    public void toSignature_emptyListReturnsEmptyString() {
        assertEquals("", new TaskList().toSignature());
    }

    @Test
    public void toSignature_joinsTaskSignaturesWithNewlines() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));
        tasks.add(new Task("buy bread", true));

        assertEquals("T|0|buy milk\nT|1|buy bread", tasks.toSignature());
    }

    @Test
    public void toString_emptyListReturnsErrorMessage() {
        assertEquals("RIEEEEAAAU: HISTORY IS EMPTY", new TaskList().toString());
    }

    @Test
    public void toString_numbersTasksFromOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));
        tasks.add(new Task("buy bread", true));

        assertEquals("1. [T][ ] buy milk\n2. [T][X] buy bread", tasks.toString());
    }
}

