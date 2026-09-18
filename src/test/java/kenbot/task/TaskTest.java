package kenbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import kenbot.KenbotException;

/**
 * Tests {@link Task}, the shared part of every kind of task: its done state,
 * its tags, and the two output formats it assembles on behalf of its subclasses.
 *
 * <p>Test names read as {@code method_condition_expectedResult}.</p>
 */
public class TaskTest {

    @Test
    public void constructor_newTask_isNotDoneAndHasNoTags() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
        assertEquals(" ", task.getStatusIcon());
        assertTrue(task.getTags().isEmpty());
    }

    @Test
    public void markAsDone_thenNotDone_returnsTheIconEachWay() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void addTag_newTag_isKept() throws KenbotException {
        Task task = new Task("read book");
        assertTrue(task.addTag(Tag.of("fun")));
        assertEquals(1, task.getTags().size());
    }

    @Test
    public void addTag_tagAlreadyThere_changesNothing() throws KenbotException {
        Task task = new Task("read book");
        task.addTag(Tag.of("fun"));
        assertFalse(task.addTag(Tag.of("fun")), "a repeat must not be added again");
        assertEquals(1, task.getTags().size());
    }

    @Test
    public void removeTag_tagThatIsThere_reportsTheChange() throws KenbotException {
        Task task = new Task("read book");
        task.addTag(Tag.of("fun"));
        assertTrue(task.removeTag(Tag.of("fun")));
        assertTrue(task.getTags().isEmpty());
    }

    @Test
    public void removeTag_tagThatIsNotThere_reportsNoChange() throws KenbotException {
        assertFalse(new Task("read book").removeTag(Tag.of("fun")));
    }

    @Test
    public void getTags_tryingToChangeTheCopy_isRefused() throws KenbotException {
        Task task = new Task("read book");
        assertThrows(UnsupportedOperationException.class, () -> task.getTags().add(Tag.of("fun")));
    }

    @Test
    public void getTags_severalTags_keepsTheOrderTheyWereAdded() throws KenbotException {
        Task task = new Task("read book");
        task.addTag(Tag.of("b"));
        task.addTag(Tag.of("a"));
        task.addTag(Tag.of("c"));
        assertEquals("[#b, #a, #c]", task.getTags().toString());
    }

    @Test
    public void toString_plainTask_usesTheFallbackTypeCode() {
        // Task is never shown on its own in the app, but the fallback keeps the
        // format readable if a new subclass forgets to supply a code.
        assertEquals("[?][ ] read book", new Task("read book").toString());
    }

    @Test
    public void toString_withTags_putsThemLast() throws KenbotException {
        Task task = new Task("read book");
        task.addTag(Tag.of("fun"));
        assertEquals("[?][ ] read book #fun", task.toString());
    }

    @Test
    public void toStorable_noTags_leavesTheTagFieldOut() {
        assertEquals("? | 0 | read book", new Task("read book").toStorable());
    }

    @Test
    public void toStorable_withTags_addsOneTrailingField() throws KenbotException {
        Task task = new Task("read book");
        task.addTag(Tag.of("fun"));
        task.addTag(Tag.of("work"));
        assertEquals("? | 0 | read book | fun work", task.toStorable());
    }

    @Test
    public void toStorable_doneTask_writesOneNotAnX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertTrue(task.toStorable().startsWith("? | 1 | "));
    }

    @Test
    public void isSameTask_sameDescriptionAndType_isTrue() {
        assertTrue(new Task("read book").isSameTask(new Task("read book")));
    }

    @Test
    public void isSameTask_differentCase_isFalse() {
        assertFalse(new Task("read book").isSameTask(new Task("Read book")));
    }

    @Test
    public void isSameTask_oneIsDone_isStillTrue() {
        // Ticking something off does not make it a different task.
        Task done = new Task("read book");
        done.markAsDone();
        assertTrue(done.isSameTask(new Task("read book")));
    }

    @Test
    public void isSameTask_oneIsTagged_isStillTrue() throws KenbotException {
        // Otherwise labelling one copy would let an identical second copy in.
        Task tagged = new Task("read book");
        tagged.addTag(Tag.of("fun"));
        assertTrue(tagged.isSameTask(new Task("read book")));
    }

    @Test
    public void isSameTask_differentType_isFalse() throws KenbotException {
        assertFalse(new Todo("read book").isSameTask(
                Deadline.of("read book /by 2019-10-15")));
    }
}
