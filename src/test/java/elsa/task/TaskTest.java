package elsa.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Task}, the parts every kind of task shares: its description, its
 * done marker, and the two ways it writes itself out.
 *
 * <p>A plain Task is never built by the chatbot itself, which always makes a
 * todo, a deadline or an event. It is tested on its own anyway, because those
 * three inherit this behaviour and each one adds only a marker around it. A
 * failure here would otherwise show up three times over, in three test classes,
 * without saying which class was really at fault.
 */
public class TaskTest {

    @Test
    public void getStatusIcon_newTask_returnsSpace() {
        assertEquals(" ", new Task("read book").getStatusIcon());
    }

    @Test
    public void getStatusIcon_afterMarkAsDone_returnsCross() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_afterMarkAsNotDone_returnsSpaceAgain() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    /** Marking a task that is already done should leave it done, not toggle it. */
    @Test
    public void markAsDone_taskAlreadyDone_staysDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_taskAlreadyNotDone_staysNotDone() {
        Task task = new Task("read book");
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    // ------------------------------------------------------------------
    // Telling one task from another
    // ------------------------------------------------------------------

    @Test
    public void isSameTask_sameDescription_returnsTrue() {
        assertTrue(new Task("read book").isSameTask(new Task("read book")));
    }

    @Test
    public void isSameTask_differentDescription_returnsFalse() {
        assertFalse(new Task("read book").isSameTask(new Task("return book")));
    }

    /**
     * Whether a task is done is left out of the comparison on purpose. Adding
     * again something already ticked off is as much a slip as adding an
     * unfinished task twice, and the user is worth telling either way.
     */
    @Test
    public void isSameTask_onlyOneOfThemDone_stillReturnsTrue() {
        Task done = new Task("read book");
        done.markAsDone();

        assertTrue(done.isSameTask(new Task("read book")));
        assertTrue(new Task("read book").isSameTask(done));
    }

    /**
     * A todo and a deadline that read the same are still different tasks, since
     * one is owed by a date and the other is not. The comparison has to weigh the
     * kind of task as well as its wording, both ways round.
     */
    @Test
    public void isSameTask_differentKindsOfTask_returnsFalse() {
        Task todo = new Todo("read book");
        Task deadline = new Deadline("read book", LocalDate.of(2019, 10, 15));

        assertFalse(todo.isSameTask(deadline));
        assertFalse(deadline.isSameTask(todo));
    }

    /**
     * A plain task carries no date, so it falls on no date. Deadlines and events
     * override this; that they do is checked in their own test classes.
     */
    @Test
    public void occursOn_anyDate_returnsFalse() {
        Task task = new Task("read book");
        assertFalse(task.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(task.occursOn(LocalDate.now()));
    }

    // ------------------------------------------------------------------
    // Matching a keyword
    // ------------------------------------------------------------------

    @Test
    public void matches_keywordInTheMiddle_returnsTrue() {
        assertTrue(new Task("read a good book").matches("good"));
    }

    @Test
    public void matches_keywordAtTheStart_returnsTrue() {
        assertTrue(new Task("read book").matches("read"));
    }

    @Test
    public void matches_keywordAtTheEnd_returnsTrue() {
        assertTrue(new Task("read book").matches("book"));
    }

    @Test
    public void matches_wholeDescription_returnsTrue() {
        assertTrue(new Task("read book").matches("read book"));
    }

    /** A keyword need not be a whole word, so a prefix of one still matches. */
    @Test
    public void matches_partOfAWord_returnsTrue() {
        assertTrue(new Task("read book").matches("boo"));
    }

    @Test
    public void matches_keywordNotPresent_returnsFalse() {
        assertFalse(new Task("read book").matches("zebra"));
    }

    /**
     * Someone looking for a task they wrote themselves should not have to
     * remember how they capitalised it, so the search ignores case both ways.
     */
    @Test
    public void matches_differentCase_returnsTrue() {
        assertTrue(new Task("Read Book").matches("book"));
        assertTrue(new Task("read book").matches("BOOK"));
        assertTrue(new Task("ReAd BoOk").matches("aD bO"));
    }

    /**
     * Only the description is searched. The type marker and the done marker are
     * part of how a task is shown, not of what the user wrote, so searching for
     * "T" or "X" must not return every todo or every finished task.
     */
    @Test
    public void matches_textFromTheDisplayedFormOnly_returnsFalse() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
        assertFalse(task.matches("[X]"));
        assertFalse(task.matches("X"));
    }

    /**
     * Any of the keywords is enough, so a task matching only the second is still
     * found. Requiring all of them would leave it out.
     */
    @Test
    public void matches_severalKeywordsOneOfWhichIsPresent_returnsTrue() {
        assertTrue(new Task("read book").matches("zebra", "book"));
        assertTrue(new Task("read book").matches("read", "zebra"));
    }

    @Test
    public void matches_severalKeywordsNoneOfWhichArePresent_returnsFalse() {
        assertFalse(new Task("read book").matches("zebra", "unicorn"));
    }

    /**
     * The words of a phrase are separate keywords, so a task matching one of
     * them is found even though the phrase itself does not appear.
     */
    @Test
    public void matches_wordsOfAPhraseSeparately_returnsTrue() {
        assertTrue(new Task("buy milk").matches("return", "milk"));
    }

    /**
     * No keyword can be found in a description that was never asked about. The
     * parser never calls it this way, but a method taking any number of
     * arguments can be handed none, so it has to answer.
     */
    @Test
    public void matches_noKeywordsAtAll_returnsFalse() {
        assertFalse(new Task("read book").matches());
    }

    /** Every description contains the empty string, so every task matches it. */
    @Test
    public void matches_emptyKeyword_returnsTrue() {
        assertTrue(new Task("read book").matches(""));
    }

    @Test
    public void toString_notDone_showsEmptyBrackets() {
        assertEquals("[ ] read book", new Task("read book").toString());
    }

    @Test
    public void toString_done_showsCrossInBrackets() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toSaveFormat_notDone_writesZeroThenDescription() {
        assertEquals("0 | read book", new Task("read book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_writesOneThenDescription() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("1 | read book", task.toSaveFormat());
    }

    /**
     * The markers written into the file are the ones TaskFormat names, not the
     * characters shown to the user. Writing "X" to the file, or "1" to the
     * screen, would be a plausible mistake, so the two are checked apart.
     */
    @Test
    public void toSaveFormat_done_usesTheStoredMarkerNotTheDisplayedOne() {
        Task task = new Task("read book");
        task.markAsDone();
        assertTrue(task.toSaveFormat().startsWith(TaskFormat.DONE));
        assertFalse(task.toSaveFormat().startsWith("X"));
    }
}
