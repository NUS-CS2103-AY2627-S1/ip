package es;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void todo_startsIncompleteAndHasTodoFormat() {
        Todo task = new Todo("read book");

        assertEquals(" ", task.getStatusIcon());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void markAndUnmark_updatesStatus() {
        Todo task = new Todo("read book");

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("[T][X] read book", task.toString());

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void repeatedStatusChanges_remainStable() {
        Todo task = new Todo("read book");

        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        task.markAsNotDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }
}
