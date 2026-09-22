package genie.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToDoTest {
    @Test
    public void toString_newToDo_returnsIncompleteToDo() {
        ToDo todo = new ToDo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_completedToDo_returnsCompletedToDo() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toString_undoneToDo_returnsIncompleteToDo() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        todo.markAsUndone();

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileFormat_incompleteToDo_returnsIncompleteRecord() {
        ToDo todo = new ToDo("read book");

        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_completedToDo_returnsCompletedRecord() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void getName_createdToDo_returnsDescription() {
        ToDo todo = new ToDo("read book");

        assertEquals("read book", todo.getName());
    }

    @Test
    public void setDescription_createdToDo_updatesDescription() {
        ToDo todo = new ToDo("read book");
        todo.setDescription("write notes");

        assertEquals("[T][ ] write notes", todo.toString());
    }
}
