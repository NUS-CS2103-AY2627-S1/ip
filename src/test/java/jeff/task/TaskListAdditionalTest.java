package jeff.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListAdditionalTest {

    @Test
    public void constructor_existingTasks_preservesOrder() {
        ArrayList<Task> originalTasks = new ArrayList<>();
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        originalTasks.add(firstTask);
        originalTasks.add(secondTask);

        TaskList taskList = new TaskList(originalTasks);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.getTask(0));
        assertSame(secondTask, taskList.getTask(1));
        assertSame(originalTasks, taskList.getTasks());
    }

    @Test
    public void addAndDeleteTask_validTasks_updatesListAndReturnsDeletedTask() {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");

        taskList.addTask(firstTask);
        taskList.addTask(secondTask);
        Task deletedTask = taskList.deleteTask(0);

        assertSame(firstTask, deletedTask);
        assertEquals(1, taskList.size());
        assertSame(secondTask, taskList.getTask(0));
    }

    @Test
    public void findTasks_mixedCaseKeyword_preservesListAndTaskOrder() {
        TaskList taskList = new TaskList();
        Todo firstMatch = new Todo("Read Java book");
        Todo nonMatch = new Todo("buy groceries");
        Deadline secondMatch = new Deadline(
                "return BOOK", LocalDate.of(2026, 9, 30));
        taskList.addTask(firstMatch);
        taskList.addTask(nonMatch);
        taskList.addTask(secondMatch);

        ArrayList<Task> matches = taskList.findTasks("bOoK");

        assertEquals(2, matches.size());
        assertSame(firstMatch, matches.get(0));
        assertSame(secondMatch, matches.get(1));
        assertEquals(3, taskList.size());
        assertSame(nonMatch, taskList.getTask(1));
    }

    @Test
    public void updateTask_deadlineDescription_preservesDateAndStatus() {
        TaskList taskList = new TaskList();
        Deadline original = new Deadline(
                "old description", LocalDate.of(2026, 9, 30));
        original.markAsDone();
        taskList.addTask(original);

        Deadline updated = (Deadline) taskList.updateTask(
                0, UpdateField.DESCRIPTION, "new description");

        assertEquals("new description", updated.getDescription());
        assertEquals(LocalDate.of(2026, 9, 30), updated.getDueDate());
        assertTrue(updated.isDone());
    }

    @Test
    public void updateTask_eventDescription_preservesTimesAndStatus() {
        TaskList taskList = new TaskList();
        Event original = new Event("old meeting", "1400", "1600");
        original.markAsDone();
        taskList.addTask(original);

        Event updated = (Event) taskList.updateTask(
                0, UpdateField.DESCRIPTION, "new meeting");

        assertEquals("new meeting", updated.getDescription());
        assertEquals("1400", updated.getStartTime());
        assertEquals("1600", updated.getEndTime());
        assertTrue(updated.isDone());
    }

    @Test
    public void updateTask_eventStartTime_preservesOtherDetails() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Event("meeting", "1400", "1600"));

        Event updated = (Event) taskList.updateTask(
                0, UpdateField.START_TIME, "1500");

        assertEquals("meeting", updated.getDescription());
        assertEquals("1500", updated.getStartTime());
        assertEquals("1600", updated.getEndTime());
    }

    @Test
    public void updateTask_incompatibleField_leavesOriginalTaskUnchanged() {
        TaskList taskList = new TaskList();
        Deadline original = new Deadline(
                "report", LocalDate.of(2026, 9, 30));
        original.markAsDone();
        taskList.addTask(original);

        assertThrows(
                IllegalArgumentException.class, (
                ) -> taskList.updateTask(
                        0, UpdateField.START_TIME, "1400"));

        assertSame(original, taskList.getTask(0));
        assertEquals("report", original.getDescription());
        assertEquals(LocalDate.of(2026, 9, 30), original.getDueDate());
        assertTrue(original.isDone());
    }

    @Test
    public void updateTask_invalidDate_leavesOriginalTaskUnchanged() {
        TaskList taskList = new TaskList();
        Deadline original = new Deadline(
                "report", LocalDate.of(2026, 9, 30));
        taskList.addTask(original);

        assertThrows(
                IllegalArgumentException.class, (
                ) -> taskList.updateTask(
                        0, UpdateField.DUE_DATE, "2026-02-30"));

        assertSame(original, taskList.getTask(0));
        assertEquals(LocalDate.of(2026, 9, 30), original.getDueDate());
    }
}
