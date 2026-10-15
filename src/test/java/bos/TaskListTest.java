package bos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the collection operations provided by {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void constructor_noInitialTasks_emptyListCreated() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.getSize());
        assertEquals(List.of(), taskList.getTasks());
    }

    @Test
    public void constructor_initialTaskSourceChanged_taskListUnaffected() {
        Task task = new TodoTask("read book");
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(task);
        TaskList taskList = new TaskList(initialTasks);

        initialTasks.clear();

        assertEquals(1, taskList.getSize());
        assertSame(task, taskList.getTasks().get(0));
    }

    @Test
    public void add_task_taskAppendedAndSizeIncreased() {
        Task firstTask = new TodoTask("read book");
        Task secondTask = new TodoTask("return book");
        TaskList taskList = new TaskList(List.of(firstTask));

        boolean isAdded = taskList.add(secondTask);

        assertTrue(isAdded);
        assertEquals(2, taskList.getSize());
        assertIterableEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    @Test
    public void add_sameDescriptionWithDifferentType_duplicateRejected() {
        Task existingTask = new Deadline("return book", "Friday");
        Task duplicateTask = new Event("return book", "1000", "1100");
        duplicateTask.markAsDone();
        TaskList taskList = new TaskList(List.of(existingTask));

        boolean isAdded = taskList.add(duplicateTask);

        assertFalse(isAdded);
        assertEquals(1, taskList.getSize());
        assertSame(existingTask, taskList.getTasks().get(0));
    }

    @Test
    public void add_sameDescriptionWithDifferentDuration_duplicateRejected() {
        Task existingTask = new Event("meeting", "1000", "1100");
        TaskList taskList = new TaskList(List.of(existingTask));

        boolean isAdded = taskList.add(new Event("meeting", "1400", "1600"));

        assertFalse(isAdded);
        assertEquals(1, taskList.getSize());
        assertSame(existingTask, taskList.getTasks().get(0));
    }

    @Test
    public void add_sameDescriptionWithDifferentCaseAndSpacing_duplicateRejected() {
        Task existingTask = new TodoTask("Read   Book");
        TaskList taskList = new TaskList(List.of(existingTask));

        boolean isAdded = taskList.add(new Deadline("read book", "Friday"));

        assertFalse(isAdded);
        assertEquals(1, taskList.getSize());
        assertSame(existingTask, taskList.getTasks().get(0));
    }

    @Test
    public void add_differentDescription_taskAdded() {
        TaskList taskList = new TaskList(List.of(new TodoTask("read book")));

        boolean isAdded = taskList.add(new Deadline("return book", "Friday"));

        assertTrue(isAdded);
        assertEquals(2, taskList.getSize());
    }

    @Test
    public void constructor_duplicateInitialTasks_onlyFirstTaskRetained() {
        Task firstTask = new Event("meeting", "1000", "1100");
        Task duplicateTask = new Deadline("meeting", "Friday");

        TaskList taskList = new TaskList(List.of(firstTask, duplicateTask));

        assertEquals(1, taskList.getSize());
        assertSame(firstTask, taskList.getTasks().get(0));
    }

    @Test
    public void delete_validIndex_taskRemovedAndReturned() {
        Task firstTask = new TodoTask("read book");
        Task taskToDelete = new TodoTask("return book");
        Task lastTask = new TodoTask("buy book");
        TaskList taskList = new TaskList(List.of(firstTask, taskToDelete, lastTask));

        Task deletedTask = taskList.delete(1);

        assertSame(taskToDelete, deletedTask);
        assertEquals(2, taskList.getSize());
        assertIterableEquals(List.of(firstTask, lastTask), taskList.getTasks());
    }

    @Test
    public void delete_indexOutsideList_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new TodoTask("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(1));
    }

    @Test
    public void mark_validIndex_selectedTaskMarkedAndReturned() {
        Task taskToMark = new TodoTask("read book");
        Task otherTask = new TodoTask("return book");
        TaskList taskList = new TaskList(List.of(taskToMark, otherTask));

        Task markedTask = taskList.mark(0);

        assertSame(taskToMark, markedTask);
        assertEquals("X", taskToMark.getStatusIcon());
        assertEquals(" ", otherTask.getStatusIcon());
    }

    @Test
    public void mark_indexOutsideList_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new TodoTask("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(1));
    }

    @Test
    public void unmark_validIndex_selectedTaskUnmarkedAndReturned() {
        Task taskToUnmark = new TodoTask("read book");
        Task otherTask = new TodoTask("return book");
        taskToUnmark.markAsDone();
        otherTask.markAsDone();
        TaskList taskList = new TaskList(List.of(taskToUnmark, otherTask));

        Task unmarkedTask = taskList.unmark(0);

        assertSame(taskToUnmark, unmarkedTask);
        assertEquals(" ", taskToUnmark.getStatusIcon());
        assertEquals("X", otherTask.getStatusIcon());
    }

    @Test
    public void unmark_indexOutsideList_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new TodoTask("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(1));
    }

    @Test
    public void find_keywordInDescriptions_matchingTasksReturnedInOriginalOrder() {
        Task firstMatch = new TodoTask("read book");
        Task nonMatch = new TodoTask("buy groceries");
        Task secondMatch = new Deadline("return BOOK", "June 6th");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matches = taskList.find("book");

        assertIterableEquals(List.of(firstMatch, secondMatch), matches);
    }

    @Test
    public void find_keywordAbsent_emptyListReturned() {
        TaskList taskList = new TaskList(List.of(new TodoTask("read book")));

        assertEquals(List.of(), taskList.find("exercise"));
    }

    @Test
    public void getTasks_taskAddedAfterViewCreated_viewUpdated() {
        TaskList taskList = new TaskList();
        List<Task> taskView = taskList.getTasks();
        Task task = new TodoTask("read book");

        taskList.add(task);

        assertIterableEquals(List.of(task), taskView);
    }

    @Test
    public void getTasks_modificationAttempt_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new TodoTask("read book")));
        List<Task> taskView = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class,
                () -> taskView.add(new TodoTask("return book")));
        assertThrows(UnsupportedOperationException.class, () -> taskView.remove(0));
    }
}
