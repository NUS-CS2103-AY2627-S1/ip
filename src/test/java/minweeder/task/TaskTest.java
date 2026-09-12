package minweeder.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void getStatusIcon_newTask_returnsBlank() {
        Task task = new Todo("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void mark_incompleteTask_setsStatusIconToX() {
        Task task = new Todo("read book");

        task.mark();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void unmark_completedTask_setsStatusIconToBlank() {
        Task task = new Todo("read book");
        task.mark();

        task.unmark();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void isOccurringOn_defaultImplementation_returnsFalse() {
        Task task = new Todo("read book");

        assertFalse(task.isOccurringOn(LocalDate.of(2024, 12, 2)));
    }
}
