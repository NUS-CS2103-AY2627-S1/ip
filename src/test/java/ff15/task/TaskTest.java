package ff15.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ff15.FF15Exception;

/**
 * Tests the keyword search that {@link Task} offers to the {@code find} command.
 *
 * <p>Only the description is searched, and case is ignored, so that a user who
 * types {@code find Book} still turns up a task described as {@code read book}.
 */
public class TaskTest {

    @Test
    public void hasKeyword_wordInTheDescription_returnsTrue() {
        assertTrue(new Task("read book").hasKeyword("book"));
        assertTrue(new Task("read book").hasKeyword("read"));
    }

    @Test
    public void hasKeyword_partOfAWord_returnsTrue() {
        // The search is a plain substring match, not a whole-word match.
        assertTrue(new Task("read book").hasKeyword("boo"));
        assertTrue(new Task("read book").hasKeyword("ead bo"));
    }

    @Test
    public void hasKeyword_differentCase_returnsTrue() {
        assertTrue(new Task("read book").hasKeyword("BOOK"));
        assertTrue(new Task("Read Book").hasKeyword("book"));
        assertTrue(new Task("READ BOOK").hasKeyword("Book"));
    }

    @Test
    public void hasKeyword_wholeDescription_returnsTrue() {
        assertTrue(new Task("read book").hasKeyword("read book"));
    }

    @Test
    public void hasKeyword_wordNotPresent_returnsFalse() {
        assertFalse(new Task("read book").hasKeyword("milk"));
        assertFalse(new Task("read book").hasKeyword("books and more"));
    }

    @Test
    public void hasKeyword_emptyKeyword_returnsTrue() {
        // Every string contains the empty string. The parser rejects an empty
        // keyword before it ever reaches here, so this only pins the behaviour.
        assertTrue(new Task("read book").hasKeyword(""));
    }

    @Test
    public void hasKeyword_statusNotSearched_ignoresTheDoneMarker() {
        Task done = new Task("read book");
        done.markAsDone();
        assertFalse(done.hasKeyword("X"));
        assertTrue(done.hasKeyword("book"));
    }

    @Test
    public void hasKeyword_dateNotSearched_matchesOnTheDescriptionAlone() throws FF15Exception {
        Deadline deadline = new Deadline("return book", TaskTime.parse("2019-12-02"));
        assertTrue(deadline.hasKeyword("return"));
        assertFalse(deadline.hasKeyword("2019"));
        assertFalse(deadline.hasKeyword("Dec"));
    }

    @Test
    public void hasKeyword_typeMarkerNotSearched_ignoresTheTaskType() {
        assertFalse(new Todo("read book").hasKeyword("[T]"));
    }

    @Test
    public void isSameAs_sameKindAndDescription_isTrueRegardlessOfDone() {
        Task done = new Todo("read book");
        done.markAsDone();
        assertTrue(new Todo("read book").isSameAs(done));
    }

    @Test
    public void isSameAs_differentDescription_isFalse() {
        assertFalse(new Todo("read book").isSameAs(new Todo("read books")));
    }

    @Test
    public void isSameAs_differentKindSameDescription_isFalse() throws FF15Exception {
        assertFalse(new Todo("x").isSameAs(new Deadline("x", TaskTime.parse("2019-12-02"))));
    }
}
