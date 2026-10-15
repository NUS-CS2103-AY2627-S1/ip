package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToDoTest {

    @Test
    public void storageString_newToDo_expectedTodoStorageFormat() {
        assertEquals("T | 0 | ABC", new ToDo("ABC").storageString());
    }

    @Test
    public void toString_newToDo_expectedIncompleteDisplayFormat() {
        assertEquals("[T] [ ] DEF", new ToDo("DEF").toString());
    }

    @Test
    public void markAsDone_todoTask_expectedDoneMarkerInBothFormats() {
        ToDo todo = new ToDo("ABC");
        todo.markAsDone();

        assertEquals("T | 1 | ABC", todo.storageString());
        assertEquals("[T] [X] ABC", todo.toString());

        todo.markAsUndone();
        assertEquals("T | 0 | ABC", todo.storageString());
    }
}
