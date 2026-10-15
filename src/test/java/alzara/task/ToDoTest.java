package alzara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ToDo}. ToDo only overrides toString()/toSaveFormat() to add
 * a "[T]"/"T |" prefix - the underlying done/not-done state (mark/unmark) and
 * the find-command matching logic are inherited from Task unchanged, so this
 * class is also where that inherited behaviour gets exercised, since Task
 * itself is no longer instantiated directly anywhere in the app.
 */
class ToDoTest {

    // A freshly constructed ToDo starts out not done.
    @Test
    void toString_taskNotDone_showsEmptyCheckboxWithTypePrefix() {
        ToDo toDo = new ToDo("read book");

        assertEquals("[T][ ] read book", toDo.toString());
    }

    // mark() (inherited from Task) should flip the task to done.
    @Test
    void toString_taskMarkedDone_showsXCheckboxWithTypePrefix() {
        ToDo toDo = new ToDo("read book");

        toDo.mark();

        assertEquals("[T][X] read book", toDo.toString());
    }

    // unmark() (inherited from Task) should flip a done task back to not done.
    @Test
    void toString_markedThenUnmarked_showsEmptyCheckboxAgain() {
        ToDo toDo = new ToDo("read book");
        toDo.mark();

        toDo.unmark();

        assertEquals("[T][ ] read book", toDo.toString());
    }

    // toSaveFormat() should start with "T | N |" (type + not done) for a fresh ToDo.
    @Test
    void toSaveFormat_taskNotDone_hasTypeAndNFlag() {
        ToDo toDo = new ToDo("read book");

        assertEquals("T | N | read book", toDo.toSaveFormat());
    }

    // toSaveFormat() should switch to "T | Y |" once the task is marked done.
    @Test
    void toSaveFormat_taskMarkedDone_hasTypeAndYFlag() {
        ToDo toDo = new ToDo("read book");

        toDo.mark();

        assertEquals("T | Y | read book", toDo.toSaveFormat());
    }

    // matches() (inherited from Task) should find the keyword regardless of case.
    @Test
    void matches_keywordPresentDifferentCase_returnsTrue() {
        ToDo toDo = new ToDo("read book");

        assertTrue(toDo.matches("BOOK"));
    }

    // matches() should return false when the keyword isn't part of the description.
    @Test
    void matches_keywordAbsent_returnsFalse() {
        ToDo toDo = new ToDo("read book");

        assertFalse(toDo.matches("jog"));
    }

    // matches() with several keywords should return true only if every one of them is found.
    @Test
    void matches_multipleKeywordsAllMatch_returnsTrue() {
        ToDo toDo = new ToDo("read book");

        assertTrue(toDo.matches("read", "book"));
    }

    // matches() with several keywords should return false if even one of them is missing.
    @Test
    void matches_multipleKeywordsPartialMatch_returnsFalse() {
        ToDo toDo = new ToDo("read book");

        assertFalse(toDo.matches("book", "jog"));
    }

    // matches() with several keywords should return false when none of them are found.
    @Test
    void matches_multipleKeywordsNoneMatch_returnsFalse() {
        ToDo toDo = new ToDo("read book");

        assertFalse(toDo.matches("jog", "run"));
    }

    // isScheduledOn() (inherited from Task) should always return false - a ToDo has no date.
    @Test
    void isScheduledOn_anyDate_returnsFalse() {
        ToDo toDo = new ToDo("read book");

        assertFalse(toDo.isScheduledOn(LocalDate.of(2019, 10, 15)));
    }
}
