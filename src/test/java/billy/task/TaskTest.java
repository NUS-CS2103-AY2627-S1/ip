package billy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import billy.BillyException;

/**
 * Tests {@link Task}, the parts every kind of task has in common.
 *
 * <p>Two things are pinned here. The first is that a task's description and
 * whether it is done stay together and can be changed only through the class,
 * since a status that drifts from its task is a bug the user sees as Billy
 * lying to them.
 *
 * <p>The second is the shape of the two ways a task writes itself out. Every
 * subclass builds on the base version, so an accidental change here would
 * quietly reword every task in the list and rewrite every line of the save file.
 */
public class TaskTest {

    @Test
    public void constructor_newTask_startsNotDone() {
        Task task = new Task("read book");
        // Nothing is done the moment it is added, whatever else may be true.
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void markAsDone_notDoneTask_statusChanges() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void markAsDone_alreadyDoneTask_staysDone() {
        // Marking twice is the user's prerogative and must not toggle back.
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_doneTask_statusChangesBack() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void markAsNotDone_taskThatWasNeverDone_staysNotDone() {
        Task task = new Task("read book");
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toSaveFormat_notDoneTask_flagIsZero() {
        assertEquals("0 | read book", new Task("read book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneTask_flagIsOne() {
        Task task = new Task("read book");
        task.markAsDone();
        // The subclasses put their type letter in front of exactly this.
        assertEquals("1 | read book", task.toSaveFormat());
    }

    @Test
    public void occursOn_plainTask_neverOnAnyDay() {
        // A plain task carries no date, so it belongs to no day. Answering here
        // rather than asking each task its type is what lets `on` search the
        // whole list without knowing which kinds of task exist.
        Task task = new Task("read book");
        assertFalse(task.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(task.occursOn(LocalDate.of(1999, 1, 1)));
    }

    // ---------------------------------------------------------------
    // Finding a task by a word in its description
    // ---------------------------------------------------------------

    @Test
    public void descriptionContains_wholeWordPresent_true() {
        assertTrue(new Task("read book").descriptionContains("book"));
    }

    @Test
    public void descriptionContains_wordAbsent_false() {
        assertFalse(new Task("read book").descriptionContains("meeting"));
    }

    @Test
    public void descriptionContains_differentCapitalization_true() {
        // Capitalization is the user's business here as it is for keywords.
        assertTrue(new Task("read book").descriptionContains("BOOK"));
        assertTrue(new Task("Read Book").descriptionContains("book"));
    }

    @Test
    public void descriptionContains_partOfAWord_true() {
        // Insisting on whole words would mean remembering how a task was worded
        // in order to find it again.
        assertTrue(new Task("read textbook").descriptionContains("book"));
        assertTrue(new Task("read book").descriptionContains("boo"));
    }

    @Test
    public void descriptionContains_phraseSpanningWords_true() {
        assertTrue(new Task("read the green book").descriptionContains("the green"));
    }

    @Test
    public void descriptionContains_wordsPresentButNotTogether_false() {
        // The search is for the text as typed, not for each word separately.
        assertFalse(new Task("read the green book").descriptionContains("read book"));
    }

    @Test
    public void descriptionContains_wholeDescription_true() {
        assertTrue(new Task("read book").descriptionContains("read book"));
    }

    @Test
    public void descriptionContains_longerThanTheDescription_false() {
        assertFalse(new Task("read").descriptionContains("read book"));
    }

    @Test
    public void toString_descriptionWithSpaces_keptAsTyped() {
        // The description is the user's words and is not tidied up.
        assertEquals("[ ] read the  green book", new Task("read the  green book").toString());
    }

    @Test
    public void constructor_blankDescription_assertionFails() {
        // Parser and Storage each refuse an empty description before building a
        // task, so reaching here with one means a caller skipped those checks.
        // A task with nothing in it would list and save as an empty line.
        assertThrows(AssertionError.class, () -> new Task("   "));
    }

    // ---------------------------------------------------------------
    // isSameTask
    // ---------------------------------------------------------------

    @Test
    public void isSameTask_sameDescription_true() {
        assertTrue(new Todo("read book").isSameTask(new Todo("read book")));
    }

    @Test
    public void isSameTask_descriptionInADifferentCase_true() {
        // The common double entry: the same errand typed again, capitalized
        // however it fell out that time. A case-sensitive check would miss it.
        assertTrue(new Todo("Read Book").isSameTask(new Todo("read book")));
    }

    @Test
    public void isSameTask_differentDescription_false() {
        assertFalse(new Todo("read book").isSameTask(new Todo("write essay")));
    }

    @Test
    public void isSameTask_oneDoneOneNot_stillTrue() {
        // Sameness is about what the user typed, not what Billy has done with
        // it. Which of the two gets warned about is the caller's business.
        Todo done = new Todo("read book");
        done.markAsDone();
        assertTrue(done.isSameTask(new Todo("read book")));
    }

    @Test
    public void isSameTask_null_false() {
        assertFalse(new Todo("read book").isSameTask(null));
    }

    @Test
    public void isSameTask_differentTypeSameWording_false() throws BillyException {
        // A todo and a deadline worded alike are different commitments: one has
        // a date to meet and the other does not.
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("read book", TaskDate.parse("2019-12-02"));
        assertFalse(todo.isSameTask(deadline));
        assertFalse(deadline.isSameTask(todo));
    }

    @Test
    public void constructor_nullDescription_assertionFails() {
        // Parser refuses an empty description and Storage refuses an empty
        // field, so null here means a task was built from somewhere that skipped
        // both checks. The assertion says so at the point of the mistake rather
        // than letting a null surface later as a bare NullPointerException.
        assertThrows(AssertionError.class, () -> new Todo(null));
    }
}
