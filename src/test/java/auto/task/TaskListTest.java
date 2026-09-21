package auto.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import auto.exception.AutoException;

/** Tests one-based lookup, mutation, rollback helpers, and list encapsulation. */
class TaskListTest {
    @Test
    void constructor_sourceListChanges_doesNotChangeTaskList() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("read book"));
        TaskList tasks = new TaskList(source);

        source.clear();

        assertEquals(1, tasks.size());
    }

    @Test
    void addAndGet_multipleTasks_usesOneBasedNumbers() throws Exception {
        Task first = new ToDo("read book");
        Task second = new ToDo("write essay");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(1));
        assertSame(second, tasks.get(2));
    }

    @Test
    void get_numberBelowAndAboveRange_throwsDescriptiveException() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));

        AutoException belowRange = assertThrows(AutoException.class, () -> tasks.get(0));
        AutoException aboveRange = assertThrows(AutoException.class, () -> tasks.get(2));

        assertEquals("Paiseh, no task 0 leh. Type list to check the numbers.", belowRange.getMessage());
        assertEquals("Paiseh, no task 2 leh. Type list to check the numbers.", aboveRange.getMessage());
    }

    @Test
    void delete_middleTask_removesAndReturnsTaskThenShiftsFollowingTask() throws Exception {
        Task first = new ToDo("read book");
        Task middle = new ToDo("write essay");
        Task last = new ToDo("file report");
        TaskList tasks = new TaskList(List.of(first, middle, last));

        Task deleted = tasks.delete(2);

        assertSame(middle, deleted);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(1));
        assertSame(last, tasks.get(2));
    }

    @Test
    void delete_invalidNumber_throwsWithoutChangingList() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(AutoException.class, () -> tasks.delete(-1));

        assertEquals(1, tasks.size());
        assertSame(task, tasks.asList().getFirst());
    }

    @Test
    void markAndUnmark_validNumber_updatesAndReturnsSelectedTask() throws Exception {
        Task first = new ToDo("read book");
        Task second = new ToDo("write essay");
        TaskList tasks = new TaskList(List.of(first, second));

        assertSame(second, tasks.mark(2));
        assertFalse(first.isCompleted());
        assertTrue(second.isCompleted());

        assertSame(second, tasks.unmark(2));
        assertFalse(second.isCompleted());
    }

    @Test
    void asList_externalMutationAttempt_throwsAndPreservesTasks() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        List<Task> view = tasks.asList();

        assertThrows(UnsupportedOperationException.class,
                () -> view.add(new ToDo("write essay")));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));

        assertEquals(1, tasks.size());
        assertSame(task, tasks.asList().getFirst());
    }

    @Test
    void restoreDeleted_originalMiddlePosition_restoresOrdering() throws Exception {
        Task first = new ToDo("read book");
        Task middle = new ToDo("write essay");
        Task last = new ToDo("file report");
        TaskList tasks = new TaskList(List.of(first, middle, last));
        Task deleted = tasks.delete(2);

        tasks.restoreDeleted(2, deleted);

        assertEquals(List.of(first, middle, last), tasks.asList());
    }

    @Test
    void removeLast_multipleTasks_removesOnlyFinalTask() throws Exception {
        Task first = new ToDo("read book");
        Task second = new ToDo("write essay");
        TaskList tasks = new TaskList(List.of(first, second));

        tasks.removeLast();

        assertEquals(1, tasks.size());
        assertSame(first, tasks.get(1));
    }
}
