package spoon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ToDoTest {

    private ToDo todo;

    @BeforeEach
    void setUp() {
        todo = new ToDo("borrow book");
    }

    @Nested
    class FormattingTests {

        @Test
        void format_uncompleted_returnsStorageFormatWithZero() {
            assertEquals("T | 0 | borrow book", todo.format());
        }

        @Test
        void format_completed_returnsStorageFormatWithOne() {
            todo.complete();
            assertEquals("T | 1 | borrow book", todo.format());
        }

        @Test
        void toString_uncompleted_returnsTagWithUncompletedSymbol() {
            assertEquals("[T][ ] borrow book", todo.toString());
        }

        @Test
        void toString_completed_returnsTagWithCompletedSymbol() {
            todo.complete();
            assertEquals("[T][X] borrow book", todo.toString());
        }
    }

    @Nested
    class InheritedBehaviorTests {

        @Test
        void equals_sameDescription_returnsTrueAndSameHashCode() {
            ToDo otherTodo = new ToDo("borrow book");
            assertEquals(todo, otherTodo);
            assertEquals(todo.hashCode(), otherTodo.hashCode());
        }

        @Test
        void equals_caseInsensitiveDescription_returnsTrue() {
            ToDo otherTodo = new ToDo("BORROW BOOK");
            assertEquals(todo, otherTodo);
            assertEquals(todo.hashCode(), otherTodo.hashCode());
        }

        @Test
        void equals_differentDescription_returnsFalse() {
            ToDo otherTodo = new ToDo("return book");
            assertNotEquals(todo, otherTodo);
        }
    }
}
