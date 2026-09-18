package benjamin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import benjamin.BenjaminException;

/**
 * Tests tagging on tasks: adding, removing, matching and rendering.
 */
public class TaskTagTest {
    @Test
    public void addTag_newTag_isAddedAndReported() {
        Todo todo = new Todo("read book");

        assertTrue(todo.addTag("fun"));
        assertTrue(todo.hasTag("fun"));
    }

    @Test
    public void addTag_leadingHash_storedWithoutIt() {
        Todo todo = new Todo("read book");
        todo.addTag("#fun");

        assertEquals(List.of("fun"), List.copyOf(todo.getTags()));
    }

    @Test
    public void addTag_differentCase_treatedAsTheSameTag() {
        Todo todo = new Todo("read book");
        todo.addTag("#FUN");

        assertFalse(todo.addTag("fun"));
        assertFalse(todo.addTag("#Fun"));
        assertEquals(1, todo.getTags().size());
    }

    @Test
    public void addTag_severalTags_keptInTheOrderAdded() {
        Todo todo = new Todo("read book");
        todo.addTag("#urgent");
        todo.addTag("#fun");
        todo.addTag("#later");

        assertEquals(List.of("urgent", "fun", "later"), List.copyOf(todo.getTags()));
    }

    @Test
    public void removeTag_presentTag_isRemovedAndReported() {
        Todo todo = new Todo("read book");
        todo.addTag("fun");

        assertTrue(todo.removeTag("#FUN"));
        assertFalse(todo.hasTag("fun"));
    }

    @Test
    public void removeTag_absentTag_reportsFalseAndChangesNothing() {
        Todo todo = new Todo("read book");
        todo.addTag("fun");

        assertFalse(todo.removeTag("nope"));
        assertEquals(1, todo.getTags().size());
    }

    @Test
    public void getTags_returnedSet_cannotBeModified() {
        Todo todo = new Todo("read book");
        todo.addTag("fun");

        assertThrows(UnsupportedOperationException.class, () -> todo.getTags().add("sneaky"));
    }

    @Test
    public void toString_todoWithTags_showsThemAfterTheDescription() {
        Todo todo = new Todo("read book");
        todo.addTag("fun");
        todo.addTag("urgent");

        assertEquals("[T][ ] read book #fun #urgent", todo.toString());
    }

    @Test
    public void toString_deadlineWithTags_showsThemAfterTheDate() throws BenjaminException {
        Deadline deadline = new Deadline("essay", TaskDateTime.parse("2019-10-15"));
        deadline.addTag("school");

        assertEquals("[D][ ] essay (by: Oct 15 2019) #school", deadline.toString());
    }

    @Test
    public void toString_eventWithTags_showsThemAfterTheRange() throws BenjaminException {
        Event event = new Event("camp",
                TaskDateTime.parse("2019-08-06"),
                TaskDateTime.parse("2019-08-08"));
        event.addTag("outdoors");

        assertEquals("[E][ ] camp (from: Aug 06 2019 to: Aug 08 2019) #outdoors",
                event.toString());
    }

    @Test
    public void toString_noTags_looksExactlyAsBefore() throws BenjaminException {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
        assertEquals("[D][ ] essay (by: Oct 15 2019)",
                new Deadline("essay", TaskDateTime.parse("2019-10-15")).toString());
    }

    @Test
    public void toSaveFormat_withTags_appendsThemAsALastField() {
        Todo todo = new Todo("read book");
        todo.addTag("fun");
        todo.addTag("urgent");

        assertEquals("T | 0 | read book | fun urgent", todo.toSaveFormat());
    }

    @Test
    public void toSaveFormat_deadlineWithTags_putsTagsAfterTheDate() throws BenjaminException {
        Deadline deadline = new Deadline("essay", TaskDateTime.parse("2019-10-15"));
        deadline.addTag("school");

        assertEquals("D | 0 | essay | 2019-10-15 | school", deadline.toSaveFormat());
    }

    @Test
    public void toSaveFormat_noTags_omitsTheFieldEntirely() throws BenjaminException {
        assertEquals("T | 0 | read book", new Todo("read book").toSaveFormat());
        assertEquals("D | 0 | essay | 2019-10-15",
                new Deadline("essay", TaskDateTime.parse("2019-10-15")).toSaveFormat());
    }

    @Test
    public void hasKeyword_tagName_matchesWithOrWithoutHash() {
        Todo todo = new Todo("read book");
        todo.addTag("fun");

        assertTrue(todo.hasKeyword("#fun"));
        assertTrue(todo.hasKeyword("fun"));
        assertTrue(todo.hasKeyword("#FUN"));
    }

    @Test
    public void hasKeyword_descriptionStillMatches_whenTagsExist() {
        Todo todo = new Todo("read book");
        todo.addTag("fun");

        assertTrue(todo.hasKeyword("book"));
        assertFalse(todo.hasKeyword("holiday"));
    }
}
