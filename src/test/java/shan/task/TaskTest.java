package shan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void constructor_blankTaskName_assertionThrown() {
        assertThrows(AssertionError.class, () -> new ToDo(" "));
    }

    @Test
    void markDone_incompleteTask_marksTaskAndReturnsUpdatedDisplay() {
        Task task = new ToDo("read book");

        task.markDone();

        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    void unmarkDone_completedTask_unmarksTask() {
        Task task = new ToDo("read book");
        task.markDone();

        task.unmarkDone();

        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void occursBetween_undatedTask_returnsFalse() {
        Task task = new ToDo("read book");

        boolean result = task.occursBetween(
                LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 31));

        assertFalse(result);
    }

    @Test
    void containsKeyword_partialKeywordWithDifferentCase_returnsTrue() {
        Task task = new ToDo("Read Book");

        assertTrue(task.containsKeyword("book"));
    }

    @Test
    void containsKeyword_absentKeyword_returnsFalse() {
        Task task = new ToDo("read book");

        assertFalse(task.containsKeyword("write"));
    }

}
