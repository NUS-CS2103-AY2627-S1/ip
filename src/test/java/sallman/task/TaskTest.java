package sallman.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests the task types: how each shows itself to the user, how each is written
 * to the data file, and which dates each falls on.
 */
public class TaskTest {

    @Test
    public void markAsDone_thenNotDone_statusIconFollows() {
        Task task = new Todo("read book");
        assertEquals(" ", task.getStatusIcon());

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toString_eachType_ownIconAndFields() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)",
                new Deadline("return book", LocalDate.of(2019, 10, 15)).toString());
        assertEquals("[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)",
                new Event("conference", LocalDate.of(2019, 10, 14),
                        LocalDate.of(2019, 10, 17)).toString());
    }

    @Test
    public void toFileFormat_eachType_savedInInputFormat() {
        // Dates are stored the way they are typed, not the way they are shown,
        // so the file can be read back whatever the display format becomes.
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
        assertEquals("D | 0 | return book | 2019-10-15",
                new Deadline("return book", LocalDate.of(2019, 10, 15)).toFileFormat());
        assertEquals("E | 0 | conference | 2019-10-14 | 2019-10-17",
                new Event("conference", LocalDate.of(2019, 10, 14),
                        LocalDate.of(2019, 10, 17)).toFileFormat());
    }

    @Test
    public void toFileFormat_doneTask_flagIsOne() {
        Task task = new Todo("read book");
        task.markAsDone();
        assertEquals("T | 1 | read book", task.toFileFormat());
    }

    @Test
    public void hasKeyword_partOfDescription_matched() {
        // A substring counts, so searching "book" finds "read book".
        Task task = new Todo("read book");

        assertTrue(task.hasKeyword("book"));
        assertTrue(task.hasKeyword("read book"));
        assertTrue(task.hasKeyword("ead bo"));
        assertFalse(task.hasKeyword("bread"));
    }

    @Test
    public void hasKeyword_differentCase_matched() {
        assertTrue(new Todo("Read Book").hasKeyword("book"));
        assertTrue(new Todo("read book").hasKeyword("BOOK"));
    }

    @Test
    public void hasKeyword_searchesDescriptionOnly_notTheDates() {
        // The date is shown as part of a deadline, but is not part of its
        // description, so searching for it must not match.
        Task deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertTrue(deadline.hasKeyword("return"));
        assertFalse(deadline.hasKeyword("2019"));
    }

    @Test
    public void addTag_sameTagTwice_keptOnce() {
        Task task = new Todo("read book");

        assertTrue(task.addTag("fun"));
        assertFalse(task.addTag("fun"));
        assertEquals(1, task.getTags().size());
    }

    @Test
    public void addTag_differingCase_treatedAsOneTag() {
        // #Fun and #fun would otherwise show as two tags on the same task.
        Task task = new Todo("read book");

        assertTrue(task.addTag("Fun"));
        assertFalse(task.addTag("fUN"));
        assertEquals("[T][ ] read book #fun", task.toString());
    }

    @Test
    public void removeTag_tagNotThere_reportsNoChange() {
        Task task = new Todo("read book");
        task.addTag("fun");

        assertTrue(task.removeTag("FUN"));
        assertFalse(task.removeTag("fun"));
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void toString_taggedTaskWithDates_tagsComeLast() {
        // The tags have to follow the dates, not sit between the description
        // and them, whichever kind of task is being shown.
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.addTag("fun");
        deadline.addTag("books");

        assertEquals("[D][ ] return book (by: Oct 15 2019) #fun #books",
                deadline.toString());
    }

    @Test
    public void toFileFormat_taggedTask_tagFieldLastAndPrefixed() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.addTag("fun");
        deadline.addTag("books");

        assertEquals("D | 0 | return book | 2019-10-15 | #fun,books",
                deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_untaggedDescriptionEndingLikeTags_emptyTagFieldAdded() {
        // An empty tag field makes the real description's last part unambiguous.
        assertEquals("T | 0 | buy milk | #urgent | #", new Todo("buy milk | #urgent").toFileFormat());
    }

    @Test
    public void toFileFormat_deadlineDescriptionLikeTags_noExtraFieldNeeded() {
        // The due date follows the description, so it can never be the last field.
        assertEquals("D | 0 | pay | #rent | 2026-09-30",
                new Deadline("pay | #rent", LocalDate.of(2026, 9, 30)).toFileFormat());
    }

    @Test
    public void toFileFormat_untaggedTask_noTagField() {
        // An untagged task must not gain an empty field, so that a file stays
        // readable by a version of the app that knows nothing about tags.
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
    }

    @Test
    public void hasKeyword_matchesTagsAsWellAsDescription() {
        Task task = new Todo("read book");
        task.addTag("urgent");

        assertTrue(task.hasKeyword("urgent"));
        assertTrue(task.hasKeyword("URG"));
        assertFalse(task.hasKeyword("later"));
    }

    @Test
    public void getTags_returnedSet_cannotBeChanged() {
        Task task = new Todo("read book");
        task.addTag("fun");

        assertThrows(UnsupportedOperationException.class, () -> task.getTags().add("sneaky"));
    }

    @Test
    public void getSortDate_eachType_theDateItComesUpOn() {
        // An event is ordered by when it starts, not when it ends.
        assertEquals(Optional.empty(), new Todo("read book").getSortDate());
        assertEquals(Optional.of(LocalDate.of(2019, 10, 15)),
                new Deadline("return book", LocalDate.of(2019, 10, 15)).getSortDate());
        assertEquals(Optional.of(LocalDate.of(2019, 10, 14)),
                new Event("conference", LocalDate.of(2019, 10, 14),
                        LocalDate.of(2019, 10, 17)).getSortDate());
    }

    @Test
    public void copy_changingTheCopy_leavesTheOriginalAlone() {
        Deadline original = new Deadline("return book", LocalDate.of(2019, 10, 15));
        original.addTag("fun");

        Task copy = original.copy();
        copy.markAsDone();
        copy.addTag("later");
        copy.removeTag("fun");

        assertEquals("[D][ ] return book (by: Oct 15 2019) #fun", original.toString());
        assertEquals("[D][X] return book (by: Oct 15 2019) #later", copy.toString());
    }

    @Test
    public void copy_eachType_keepsTypeAndFields() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Event event = new Event("conference", LocalDate.of(2019, 10, 14),
                LocalDate.of(2019, 10, 17));

        assertEquals("[T][X] read book", todo.copy().toString());
        assertEquals("[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)",
                event.copy().toString());
    }

    @Test
    public void isSameTask_sameDescriptionInAnotherCase_same() {
        assertTrue(new Todo("read book").isSameTask(new Todo("Read Book")));
    }

    @Test
    public void isSameTask_doneStatusAndTagsDiffer_stillSame() {
        // Marking or tagging a task changes what has happened to it, not what it is.
        Todo tagged = new Todo("read book");
        tagged.markAsDone();
        tagged.addTag("fun");

        assertTrue(tagged.isSameTask(new Todo("read book")));
    }

    @Test
    public void isSameTask_differentDates_notSame() {
        Deadline first = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertFalse(first.isSameTask(new Deadline("return book", LocalDate.of(2019, 10, 16))));
    }

    @Test
    public void isSameTask_differentKindsOfTask_notSame() {
        // A todo and a deadline can share a description without duplicating each other.
        assertFalse(new Todo("return book")
                .isSameTask(new Deadline("return book", LocalDate.of(2019, 10, 15))));
    }

    @Test
    public void copy_plainTask_independentCopy() {
        Task original = new Task("read book");

        Task copy = original.copy();
        copy.markAsDone();

        assertEquals("[ ] read book", original.toString());
        assertEquals("[X] read book", copy.toString());
    }

    @Test
    public void isSameTask_differentDescriptions_notSame() {
        assertFalse(new Todo("read book").isSameTask(new Todo("buy milk")));
    }

    @Test
    public void addTagAndHasKeyword_computerSetToTurkish_capitalIStillMatches() {
        // In Turkish, a capital I lower-cases to a dotless i, so "LIST" would
        // become "list" spelled with that dotless i, and stop matching "list" if case followed the computer.
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Task task = new Todo("shopping list");
            task.addTag("WISHLIST");

            assertTrue(task.hasKeyword("LIST"));
            assertTrue(task.removeTag("wishlist"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void isValidTag_lettersDigitsHyphensAndUnderscoresOnly() {
        assertTrue(Task.isValidTag("read_later-2"));
        assertFalse(Task.isValidTag("bad!tag"));
        assertFalse(Task.isValidTag("two words"));
        assertFalse(Task.isValidTag("a,b"));
        assertFalse(Task.isValidTag(""));
    }

    @Test
    public void isOn_todo_neverOnAnyDate() {
        // A todo carries no date, so it must not appear under any day.
        assertFalse(new Todo("read book").isOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void isOn_deadline_onlyItsDueDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertTrue(deadline.isOn(LocalDate.of(2019, 10, 15)));
        assertFalse(deadline.isOn(LocalDate.of(2019, 10, 14)));
        assertFalse(deadline.isOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void isOn_event_everyDayItSpansIncludingTheEnds() {
        Event event = new Event("conference", LocalDate.of(2019, 10, 14),
                LocalDate.of(2019, 10, 17));

        assertFalse(event.isOn(LocalDate.of(2019, 10, 13)));
        assertTrue(event.isOn(LocalDate.of(2019, 10, 14)));
        assertTrue(event.isOn(LocalDate.of(2019, 10, 16)));
        assertTrue(event.isOn(LocalDate.of(2019, 10, 17)));
        assertFalse(event.isOn(LocalDate.of(2019, 10, 18)));
    }

    @Test
    public void isOn_singleDayEvent_onThatDayOnly() {
        Event event = new Event("standup", LocalDate.of(2019, 10, 15),
                LocalDate.of(2019, 10, 15));

        assertTrue(event.isOn(LocalDate.of(2019, 10, 15)));
        assertFalse(event.isOn(LocalDate.of(2019, 10, 16)));
    }
}
