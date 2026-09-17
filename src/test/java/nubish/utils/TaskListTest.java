package nubish.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import nubish.tasks.Deadline;
import nubish.tasks.Task;
import nubish.tasks.Todo;

/**
 * Tests task list creation and task insertion behavior.
 */
public class TaskListTest {
    /**
     * Verifies that a newly created task list has no tasks.
     */
    @Test
    public void creating_newTaskList_createsAnEmptyList() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    /**
     * Verifies that adding a task increases the list size and stores the task.
     */
    @Test
    public void add_newTaskList_addsTask() {
        TaskList taskList = new TaskList();
        Task task = new Todo("");
        taskList.add(task);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(0));
    }

    /**
     * Verifies that duplicate task descriptions are rejected.
     */
    @Test
    public void add_duplicateDescription_throwsException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(NubishException.class, () -> taskList.add(new Todo("read book")));
    }

    /**
     * Verifies that duplicate task descriptions are rejected regardless of case.
     */
    @Test
    public void add_duplicateDescriptionWithDifferentCase_throwsException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read Book"));

        assertThrows(NubishException.class, () -> taskList.add(new Todo("read book")));
    }

    /**
     * Verifies that duplicate task descriptions in the same insertion batch are rejected.
     */
    @Test
    public void add_duplicateDescriptionInSameBatch_throwsException() {
        TaskList taskList = new TaskList();

        assertThrows(NubishException.class, () -> taskList.add(new Todo("read book"), new Todo("read book")));
    }

    /**
     * Verifies that removing a task returns the removed task and reduces the list size.
     */
    @Test
    public void remove_existingTask_returnsRemovedTask() {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");
        taskList.add(task);

        Task removedTask = taskList.remove(0);

        assertSame(task, removedTask);
        assertEquals(0, taskList.size());
    }

    /**
     * Verifies that finding tasks returns only tasks whose descriptions contain the keyword.
     */
    @Test
    public void find_matchingKeyword_returnsMatchingTasks() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("write code"));

        TaskList foundTasks = taskList.find("read");

        assertEquals(1, foundTasks.size());
        assertEquals("read book", foundTasks.get(0).getDescription());
    }

    /**
     * Verifies that finding tasks with no matching keyword returns an empty list.
     */
    @Test
    public void find_noMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        TaskList foundTasks = taskList.find("code");

        assertEquals(0, foundTasks.size());
    }

    /**
     * Verifies that reminders include only deadline tasks due today.
     */
    @Test
    public void remind_deadlineDueToday_returnsTodayDeadline() {
        TaskList taskList = new TaskList();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + " 1800";
        taskList.add(new Todo("read book"));
        taskList.add(new Deadline("submit assignment", today));

        TaskList reminders = taskList.remind();

        assertEquals(1, reminders.size());
        assertEquals("submit assignment", reminders.get(0).getDescription());
    }

    /**
     * Verifies that an empty task list is displayed as an empty string.
     */
    @Test
    public void toString_emptyTaskList_returnsEmptyString() {
        TaskList taskList = new TaskList();

        assertEquals("", taskList.toString());
    }

    /**
     * Verifies that a non-empty task list is numbered in display order.
     */
    @Test
    public void toString_nonEmptyTaskList_returnsNumberedList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("write code"));

        assertEquals("1. [T][ ] read book\n2. [T][ ] write code\n\n", taskList.toString());
    }
}
