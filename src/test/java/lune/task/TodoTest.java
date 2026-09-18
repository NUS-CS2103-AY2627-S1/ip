package lune.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the two methods Todo itself overrides: {@link Todo#toString()} and
 * {@link Todo#toSaveFormat()}. Inherited members (getDescription,
 * markAsDone, occursOn, etc.) belong to Task and are tested there.
 */
public class TodoTest {

    @Test
    public void toString_notDoneTodo_bracketTTagAndBlankStatusShown() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_doneTodo_statusIconXShown() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toSaveFormat_notDoneTodo_pipeSeparatedLineWithZeroFlag() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneTodo_pipeSeparatedLineWithOneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }
}
