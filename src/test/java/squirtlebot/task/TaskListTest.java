package squirtlebot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests task state and task-list display behavior.
 */
public class TaskListTest {
    @Test
    public void task_creationAndStatusChange_updatesDisplay() {
        ToDo task = new ToDo("  read chapter 1  ");

        assertFalse(task.isDone());
        assertEquals("[T] [ ] read chapter 1", task.toString());

        task.setDone(true);

        assertTrue(task.isDone());
        assertEquals("[T] [X] read chapter 1", task.toString());
    }

    @Test
    public void toString_multipleTasks_returnsNumberedLines() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read chapter 1"));
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 30)));

        String expected = "\t1. [T] [ ] read chapter 1\n"
                + "2. [D] [ ] submit report (by: 2026-09-30)";

        assertEquals(expected, tasks.toString());
    }

    @Test
    public void toString_emptyList_returnsSingleTab() {
        assertEquals("\t", new TaskList().toString());
    }
}
