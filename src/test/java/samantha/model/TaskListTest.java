package samantha.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import samantha.exception.InputException;
import samantha.exception.TaskValidationException;

class TaskListTest {
    @Test
    void taskOperations_preserveOrderAndUseOneBasedTaskIds()
            throws TaskValidationException, InputException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        Todo third = new Todo("third");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.addAll(List.of(second, third));

        assertEquals(3, tasks.size());
        assertEquals(first, tasks.getTask(1));
        assertEquals(third, tasks.getTask(3));
        assertEquals(second, tasks.removeTask(2));
        assertEquals(List.of(first, third), tasks.asList());
    }

    @Test
    void getTask_invalidOneBasedId_inputExceptionThrown() throws TaskValidationException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));

        assertThrows(InputException.class, () -> tasks.getTask(0));
        assertThrows(InputException.class, () -> tasks.getTask(-1));
        assertThrows(InputException.class, () -> tasks.getTask(2));
    }

    @Test
    void asList_snapshotIsImmutableAndDoesNotChangeAfterAddition() throws TaskValidationException {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        tasks.add(first);
        List<Task> snapshot = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("second")));
        tasks.add(new Todo("second"));
        assertEquals(List.of(first), snapshot);
    }
}
