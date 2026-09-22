package miaow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void getDescription_returnsOriginalDescription() {
        Task task = new Task("Finish homework");

        assertEquals("Finish homework", task.getDescription());
    }

    @Test
    void newTask_isNotMarkedAsDone() {
        Task task = new Task("Finish homework");

        assertFalse(task.isDone());
    }


    @Test
    void markAsDone_changesTaskStatusToDone() {
        Task task = new Task("Finish homework");

        assertFalse(task.isDone());
        task.mark();
        assertTrue(task.isDone());
    }

    @Test
    void unmark_changesTaskStatusToNotDone() {
        Task task = new Task("Finish homework");

        task.mark();
        task.unmark();

        assertFalse(task.isDone());
    }

    @Test
    void toFileFormat_returnsExpectedFormat() {
        Task task = new Task("Finish homework");

        assertEquals("T | 0 | Finish homework", task.toFileFormat());

        task.mark();

        assertEquals("T | 1 | Finish homework", task.toFileFormat());
    }

    @Test
    void toString_returnsExpectedDescription() {
        Task task = new Task("Finish homework");

        assertEquals("[T][ ] Finish homework", task.toString());

        task.mark();

        assertEquals("[T][X] Finish homework", task.toString());
    }
}