package dog.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toSaveFormat_validDescription_returnsSaveFormat() {
        // Test done todo
        Todo doneTodo = new Todo("cry to sleep", true);
        assertEquals("T | X | cry to sleep", doneTodo.toSaveFormat());

        // Test pending todo
        Todo pendingTodo = new Todo("survive 2103t", false);
        assertEquals("T |   | survive 2103t", pendingTodo.toSaveFormat());

        // Test description with spaces
        Todo spacesTodo = new Todo("  a  b   c ", false);
        assertEquals("T |   |   a  b   c ", spacesTodo.toSaveFormat());

        // Test description with pipes
        Todo pipesTodo = new Todo(" || d | e |   | f ", true);
        assertEquals("T | X |  || d | e |   | f ", pipesTodo.toSaveFormat());
    }

    @Test
    public void fromSaveFormat_validFormat_returnsTodo() {
        // Test done todo
        Todo doneTodo = Todo.fromSaveFormat("T | X | cry to sleep");
        assertEquals("T | X | cry to sleep", doneTodo.toSaveFormat());

        // Test pending todo
        Todo pendingTodo = Todo.fromSaveFormat("T |   | survive 2103t");
        assertEquals("T |   | survive 2103t", pendingTodo.toSaveFormat());
    }

    @Test
    public void fromSaveFormat_specialCharacters_preservesDescription() {
        // Test description with special characters
        Todo specialCharTodo = Todo.fromSaveFormat("T | X | cry @home #urgent!#$^%&^(*^_)_){}/.,./';}{|");
        assertEquals("T | X | cry @home #urgent!#$^%&^(*^_)_){}/.,./';}{|", specialCharTodo.toSaveFormat());

        // Test description with pipe characters
        Todo pipeCharPendingTodo = Todo.fromSaveFormat("T |   | X | cry");
        assertEquals("T |   | X | cry", pipeCharPendingTodo.toSaveFormat());

        // Test description with pipe characters
        Todo pipeCharDoneTodo = Todo.fromSaveFormat("T | X |   | fard");
        assertEquals("T | X |   | fard", pipeCharDoneTodo.toSaveFormat());
    }

    @Test
    public void fromSaveFormat_invalidFormat_returnsNull() {
        // Test missing parts
        assertNull(Todo.fromSaveFormat("T | broken"));

        // Test missing spaces
        assertNull(Todo.fromSaveFormat("T| X | i need space"));
        assertNull(Todo.fromSaveFormat("T| | give me space"));

        // Test invalid/missing status icon
        assertNull(Todo.fromSaveFormat("T |  | give me more space"));
        assertNull(Todo.fromSaveFormat("T | F | what the F"));

        // Test invalid/missing task type icon
        assertNull(Todo.fromSaveFormat("D | X | im dead :skull:"));
        assertNull(Todo.fromSaveFormat("E |   | e"));
        assertNull(Todo.fromSaveFormat(" |   | where is the t"));

        // Test empty string
        assertNull(Todo.fromSaveFormat(""));

        // Test null
        assertNull(Todo.fromSaveFormat(null));
    }

    @Test
    public void fromSaveFormat_roundTrip_producesSameString() {
        // Test round-trip consistency
        String original = "T | X | Test task";
        Todo todo = Todo.fromSaveFormat(original);
        assertEquals(original, todo.toSaveFormat());
    }
}
