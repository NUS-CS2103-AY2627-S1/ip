package elora.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void constructor_newTask_isNotDone() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void constructor_newTask_storesDescription() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
    }

    @Test
    void markAsDone_notDoneTask_statusIconBecomesX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_doneTask_statusIconBecomesBlank() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void toString_notDoneTask_showsBlankBracket() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void toString_doneTask_showsXBracket() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    void isOccurringOn_anyDate_returnsFalse() {
        Task task = new Task("read book");
        assertFalse(task.isOccurringOn(LocalDate.parse("2019-10-15")));
    }

    @Test
    void getSortDate_plainTask_returnsNull() {
        Task task = new Task("read book");
        assertNull(task.getSortDate());
    }

    @Test
    void equals_sameDescription_returnsTrue() {
        assertEquals(new Task("read book"), new Task("read book"));
    }

    @Test
    void equals_differentDescription_returnsFalse() {
        assertNotEquals(new Task("read book"), new Task("return book"));
    }

    @Test
    void equals_null_returnsFalse() {
        assertNotEquals(new Task("read book"), null);
    }

    @Test
    void hashCode_equalTasks_haveEqualHashCodes() {
        assertEquals(new Task("read book").hashCode(), new Task("read book").hashCode());
    }
}
