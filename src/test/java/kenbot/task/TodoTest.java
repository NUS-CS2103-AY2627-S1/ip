package kenbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import kenbot.KenbotException;

/**
 * Tests {@link Todo}, the simplest task type: a description and nothing else.
 *
 * <p>Test names read as {@code method_input_expectedResult}.</p>
 */
public class TodoTest {

    @Test
    public void of_description_keepsIt() throws KenbotException {
        assertEquals("[T][ ] read book", Todo.of("read book").toString());
    }

    @Test
    public void of_surroundingSpaces_trimsThem() throws KenbotException {
        assertEquals("read book", Todo.of("   read book   ").getDescription());
    }

    @Test
    public void of_severalSpacesInside_collapsesThem() throws KenbotException {
        assertEquals("read the book", Todo.of("read   the   book").getDescription());
    }

    @Test
    public void of_blank_throws() {
        assertThrows(KenbotException.class, () -> Todo.of("   "));
    }

    @Test
    public void of_empty_throws() {
        assertThrows(KenbotException.class, () -> Todo.of(""));
    }

    @Test
    public void of_onlyATag_throwsBecauseNothingIsLeft() {
        assertThrows(KenbotException.class, () -> Todo.of("#fun"));
    }

    @Test
    public void of_trailingTags_areTakenOffTheDescription() throws KenbotException {
        Todo todo = Todo.of("read book #fun #cs2103");
        assertEquals("read book", todo.getDescription());
        assertEquals(2, todo.getTags().size());
    }

    @Test
    public void of_hashInTheMiddle_staysInTheDescription() throws KenbotException {
        Todo todo = Todo.of("read #1 book");
        assertEquals("read #1 book", todo.getDescription());
        assertTrue(todo.getTags().isEmpty());
    }

    @Test
    public void toStorable_plainTodo_usesTheTLetter() throws KenbotException {
        assertEquals("T | 0 | read book", Todo.of("read book").toStorable());
    }

    @Test
    public void toStorable_doneTodo_writesTheFlag() throws KenbotException {
        Todo todo = Todo.of("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toStorable());
    }

    @Test
    public void toStorable_withTags_addsThemLast() throws KenbotException {
        assertEquals("T | 0 | read book | fun", Todo.of("read book #fun").toStorable());
    }
}
