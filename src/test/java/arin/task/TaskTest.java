package arin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the completion-status behaviour of {@link Task}.
 */
class TaskTest {
    @Test
    void getStatusIcon_newTask_returnsIncompleteIcon() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void markTask_incompleteTask_marksTask() {
        Task task = new Task("read book");
        task.markTask();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void unmarkTask_markedTask_unmarksTask() {
        Task task = new Task("read book");
        task.markTask();
        task.unmarkTask();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void storageToString_incompleteTask_returnsStorageFormat() {
        Task task = new Task("read book");

        assertEquals(" |   | read book", task.storageToString());
    }

    @Test
    void storageToString_completeTask_returnsStorageFormat() {
        Task task = new Task("read book");
        task.markTask();

        assertEquals(" | X | read book", task.storageToString());
    }

    @Test
    void toString_incompleteTask_returnsDisplayFormat() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void toString_completeTask_returnsDisplayFormat() {
        Task task = new Task("read book");
        task.markTask();

        assertEquals("[X] read book", task.toString());
    }
}
