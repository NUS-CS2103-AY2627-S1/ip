package quu.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import quu.exception.MissingArgumentException;
import quu.exception.QuuException;
import quu.exception.UnsupportedEditException;

/**
 * Tests how a {@link ToDo} renders itself and survives a trip through the save file format.
 */
public class ToDoTest {

    @Test
    public void toString_rendersTodoIconAndStatus() {
        assertEquals("[T][ ] borrow book", new ToDo("borrow book").toString());
    }

    @Test
    public void toFileString_rendersTypeStatusAndDescription() {
        assertEquals("T | 0 | borrow book", new ToDo("borrow book").toFileString());
    }

    @Test
    public void toFileString_thenFromFileString_roundTripsTask() throws MissingArgumentException {
        ToDo original = new ToDo("borrow book");
        ToDo restored = ToDo.fromFileString(original.toFileString().split("\\s*\\|\\s*", 3));
        assertEquals(original.toString(), restored.toString());
    }

    @Test
    public void fromFileString_blankDescription_throwsMissingArgument() {
        String[] fields = {"T", "0", "   "};
        assertThrows(MissingArgumentException.class, () -> ToDo.fromFileString(fields));
    }

    @Test
    public void fromFileString_fewerThanThreeFields_assertionFails() {
        String[] fields = {"T", "0"};
        assertThrows(AssertionError.class, () -> ToDo.fromFileString(fields));
    }

    @Test
    public void withEdits_newDescription_returnsEditedToDo() throws QuuException {
        ToDo editedToDo = new ToDo("borrow book").withEdits(Map.of(Task.KEY_DESCRIPTION, "borrow two books"));
        assertEquals("[T][ ] borrow two books", editedToDo.toString());
    }

    @Test
    public void withEdits_flagAToDoDoesNotHave_throwsUnsupportedEdit() {
        assertThrows(UnsupportedEditException.class, () ->
                new ToDo("borrow book").withEdits(Map.of("by", "2026-06-06")));
    }

    @Test
    public void withEdits_blankDescription_throwsMissingArgument() {
        assertThrows(MissingArgumentException.class, () ->
                new ToDo("borrow book").withEdits(Map.of(Task.KEY_DESCRIPTION, "   ")));
    }

}
