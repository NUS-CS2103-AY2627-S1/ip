package gnaix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import gnaix.task.Task;
import gnaix.task.Todo;

class TaskDisplayTest {

    @Test
    void constructor_values_valuesReturned() {
        Task task = new Todo("buy milk");

        TaskDisplay taskDisplay = new TaskDisplay(3, task);

        assertEquals(3, taskDisplay.getNumber());
        assertSame(task, taskDisplay.getTask());
    }
}
