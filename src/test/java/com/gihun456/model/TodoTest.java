package com.gihun456.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class TodoTest {
    @Test
    public void todoConstructor_validName_taskNameStored() {
        Todo todo = new Todo("Read book");

        assertEquals("Read book", todo.getTaskName());
    }

    @Test
    public void todoConstructor_validName_defaultStatusIsNotDone() {
        Todo todo = new Todo("Read book");

        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void markAsDone_todoInitiallyNotDone_statusIconBecomesDone() {
        Todo todo = new Todo("Read book");

        todo.markAsDone();

        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void markAsNotDone_doneTodo_statusIconBecomesNotDone() {
        Todo todo = new Todo("Read book");
        todo.markAsDone();

        todo.markAsNotDone();

        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void toString_newTodo_returnsTodoWithPendingStatus() {
        Todo todo = new Todo("Read book");

        assertEquals("[T][ ] Read book", todo.toString());
    }

    @Test
    public void toString_doneTodo_returnsTodoWithCompletedStatus() {
        Todo todo = new Todo("Read book");
        todo.markAsDone();

        assertEquals("[T][X] Read book", todo.toString());
    }

    @Test
    public void getTaskName_emptyName_returnsEmptyString() {
        Todo todo = new Todo("");

        assertEquals("", todo.getTaskName());
    }
}
