package shannon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import shannon.exception.DuplicateTaskException;
import shannon.exception.TaskNotFoundException;
import shannon.task.Task;
import shannon.task.Todo;

/** Tests task numbering, duplicate detection, and safe list updates. */
class TaskListTest {

    @Test
    void add_duplicateWithDifferentCaseAndStatus_rejected() throws Exception {
        TaskList tasks = new TaskList();
        Todo original = new Todo("Read book");
        original.markDone();
        tasks.add(original);

        assertThrows(DuplicateTaskException.class, () -> tasks.add(new Todo("read BOOK")));
        assertEquals(List.of(original), tasks.asList());
    }

    @Test
    void getTasks_repeatedNumbers_preservesRequestedOrder() throws Exception {
        Todo first = new Todo("Read book");
        Todo second = new Todo("Buy milk");
        TaskList tasks = new TaskList(List.of(first, second));

        assertEquals(List.of(second, first), tasks.getTasks(2, 1, 2));
        assertThrows(TaskNotFoundException.class, () -> tasks.getTasks(0));
        assertThrows(TaskNotFoundException.class, () -> tasks.getTasks(3));
    }

    @Test
    void deleteTasks_repeatedNumbers_removesEachTaskOnce() throws Exception {
        Todo first = new Todo("Read book");
        Todo second = new Todo("Buy milk");
        Todo third = new Todo("Exercise");
        TaskList tasks = new TaskList(List.of(first, second, third));

        assertEquals(List.of(third, first), tasks.deleteTasks(3, 1, 3));
        assertEquals(List.of(second), tasks.asList());
        assertEquals(1, tasks.size());
    }

    @Test
    void deleteTasks_invalidNumber_leavesAllTasksUntouched() {
        Todo first = new Todo("Read book");
        Todo second = new Todo("Buy milk");
        TaskList tasks = new TaskList(List.of(first, second));

        assertThrows(TaskNotFoundException.class, () -> tasks.deleteTasks(1, 3));
        assertEquals(List.of(first, second), tasks.asList());
    }

    @Test
    void find_mixedCase_matchesSubstringInListOrder() {
        Todo first = new Todo("Read book");
        Todo second = new Todo("Visit bookshop");
        TaskList tasks = new TaskList(List.of(first, new Todo("Buy milk"), second));

        assertEquals(List.of(first, second), tasks.find("BOOK"));
        assertEquals(List.of(), tasks.find("missing"));
    }

    @Test
    void construct_externalListChanges_doNotChangeTasks() throws Exception {
        Todo task = new Todo("Read book");
        List<Task> originals = new ArrayList<>(List.of(task));
        TaskList tasks = new TaskList(originals);
        originals.clear();

        assertEquals(List.of(task), tasks.asList());
        assertThrows(UnsupportedOperationException.class, () -> tasks.asList().clear());
        assertThrows(UnsupportedOperationException.class, () -> tasks.getTasks(1).clear());
    }
}
