package shai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/** Tests the creation and string representation of {@link ToDo}. */
class ToDoTest {
    @Test
    void constructor_descriptionProvided_storesDescriptionAndStartsIncomplete() {
        ToDo toDo = new ToDo("buy milk");

        assertEquals("buy milk", toDo.getDescription());
        assertFalse(toDo.isDone());
    }

    @Test
    void toString_newToDo_returnsIncompleteToDoFormat() {
        ToDo toDo = new ToDo("buy milk");

        assertEquals("[T][ ] buy milk", toDo.toString());
    }

    @Test
    void toString_doneToDo_returnsCompletedToDoFormat() {
        ToDo toDo = new ToDo("buy milk");
        toDo.markAsDone();

        assertEquals("[T][X] buy milk", toDo.toString());
    }

    @Test
    void toString_emptyDescription_returnsTypeAndStatusOnly() {
        ToDo toDo = new ToDo("");

        assertEquals("[T][ ] ", toDo.toString());
    }
}
