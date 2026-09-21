package ted.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests how tasks present themselves, both to the user ({@code toString}) and
 * to the save file ({@link Task#toSaveFormat()}).
 * <p>
 * The save format matters most: anything it gets wrong is written to disk and
 * read back as a corrupted line on the next run.
 */
public class TaskTest {
    /** A fixed date-time, so the tests do not depend on when they are run. */
    private static final LocalDateTime SECOND_OF_DECEMBER_6PM = LocalDateTime.of(2019, 12, 2, 18, 0);

    /** Two hours before {@link #SECOND_OF_DECEMBER_6PM}. */
    private static final LocalDateTime SECOND_OF_DECEMBER_4PM = LocalDateTime.of(2019, 12, 2, 16, 0);

    /**
     * Verifies that a new todo displays its type, incomplete status, and description.
     */
    @Test
    public void toString_newTodo_showsTypeAndEmptyStatus() {
        assertEquals("[T][ ] borrow book", new Todo("borrow book").toString());
    }

    /**
     * Verifies that a completed todo displays a cross in its status indicator.
     */
    @Test
    public void toString_doneTodo_showsCross() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("[T][X] borrow book", todo.toString());
    }

    /**
     * Verifies that unmarking a completed task restores its incomplete status.
     */
    @Test
    public void markAsNotDone_doneTask_reversesTheMark() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    /**
     * Verifies that a deadline displays its type, description, and formatted due date and time.
     */
    @Test
    public void toString_deadline_showsFriendlyDate() {
        String shown = new Deadline("return book", SECOND_OF_DECEMBER_6PM).toString();
        assertEquals("[D][ ] return book (by: 2 Dec 2019, 6:00 PM)", shown);
    }

    /**
     * Verifies that an event displays its type, description, and both start and end times.
     */
    @Test
    public void toString_event_showsBothEnds() {
        String shown = new Event("meeting", SECOND_OF_DECEMBER_4PM, SECOND_OF_DECEMBER_6PM).toString();
        assertEquals("[E][ ] meeting (from: 2 Dec 2019, 4:00 PM to: 2 Dec 2019, 6:00 PM)", shown);
    }

    /**
     * Verifies that a new todo saves its type, incomplete flag, and description.
     */
    @Test
    public void toSaveFormat_todo_writesIconDoneFlagAndDescription() {
        assertEquals("T | 0 | borrow book", new Todo("borrow book").toSaveFormat());
    }

    /**
     * Verifies that a completed todo saves its done flag as one.
     */
    @Test
    public void toSaveFormat_doneTodo_writesOneAsTheFlag() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("T | 1 | borrow book", todo.toSaveFormat());
    }

    /**
     * Verifies that a deadline saves its due date and time in ISO-8601 format.
     */
    @Test
    public void toSaveFormat_deadline_writesIsoDateTime() {
        // ISO-8601 rather than the display format, so the file does not change
        // meaning when the way Ted prints dates changes.
        assertEquals("D | 0 | 2019-12-02T18:00 | return book",
                new Deadline("return book", SECOND_OF_DECEMBER_6PM).toSaveFormat());
    }

    /**
     * Verifies that an event saves both its start and end date-times in ISO-8601 format.
     */
    @Test
    public void toSaveFormat_event_writesBothIsoDateTimes() {
        assertEquals("E | 0 | 2019-12-02T16:00 | 2019-12-02T18:00 | meeting",
                new Event("meeting", SECOND_OF_DECEMBER_4PM, SECOND_OF_DECEMBER_6PM).toSaveFormat());
    }

    /**
     * Verifies that a pipe in a task description is escaped when saved.
     */
    @Test
    public void toSaveFormat_descriptionContainingSeparator_escapesIt() {
        // An unescaped pipe would split the description into two fields when
        // the line is read back.
        assertEquals("T | 0 | rock \\| roll", new Todo("rock | roll").toSaveFormat());
    }

    /**
     * Verifies that a backslash in a task description is escaped when saved.
     */
    @Test
    public void toSaveFormat_descriptionContainingBackslash_escapesIt() {
        assertEquals("T | 0 | back\\\\slash", new Todo("back\\slash").toSaveFormat());
    }

    /**
     * Verifies that a todo displays its tags after its description in insertion order.
     */
    @Test
    public void toString_taggedTodo_showsTagsAfterDescription() {
        Todo todo = new Todo("read book");
        todo.addTags(List.of(new Tag("fun"), new Tag("school")));
        assertEquals("[T][ ] read book #fun #school", todo.toString());
    }

    /**
     * Verifies that a deadline displays its tags between its description and due date.
     */
    @Test
    public void toString_taggedDeadline_showsTagsBeforeDate() {
        Deadline deadline = new Deadline("return book", SECOND_OF_DECEMBER_6PM);
        deadline.addTags(List.of(new Tag("library")));
        assertTrue(deadline.toString().startsWith("[D][ ] return book #library (by: "));
    }

    /**
     * Verifies that adding an existing tag with different capitalization creates no duplicate.
     */
    @Test
    public void addTags_tagAlreadyPresent_notRepeated() {
        Todo todo = new Todo("read book");
        todo.addTags(List.of(new Tag("fun")));
        todo.addTags(List.of(new Tag("FUN"), new Tag("school")));
        // The repeated tag keeps its original place rather than moving to the end.
        assertEquals("[T][ ] read book #fun #school", todo.toString());
    }

    /**
     * Verifies that removing selected tags preserves the task's other tags.
     */
    @Test
    public void removeTags_someOfTheTags_removesOnlyThose() {
        Todo todo = new Todo("read book");
        todo.addTags(List.of(new Tag("fun"), new Tag("school")));
        todo.removeTags(List.of(new Tag("school")));
        assertTrue(todo.hasTag(new Tag("fun")));
        assertFalse(todo.hasTag(new Tag("school")));
    }

    /**
     * Verifies that a todo saves its tags between its done flag and description.
     */
    @Test
    public void toSaveFormat_taggedTodo_writesTagsAfterDoneFlag() {
        Todo todo = new Todo("read book");
        todo.addTags(List.of(new Tag("fun"), new Tag("school")));
        assertEquals("T | 0 | #fun #school | read book", todo.toSaveFormat());
    }

    /**
     * Verifies that an event saves its tags before its start and end date-times.
     */
    @Test
    public void toSaveFormat_taggedEvent_writesTagsBeforeDates() {
        Event event = new Event("meeting", SECOND_OF_DECEMBER_4PM, SECOND_OF_DECEMBER_6PM);
        event.addTags(List.of(new Tag("cs2103")));
        assertEquals("E | 0 | #cs2103 | 2019-12-02T16:00 | 2019-12-02T18:00 | meeting",
                event.toSaveFormat());
    }

    /**
     * Verifies that removing the final tag restores the untagged save format.
     */
    @Test
    public void toSaveFormat_lastTagRemoved_writesUntaggedLine() {
        // Without tags the line must be exactly what Ted wrote before tags existed.
        Todo todo = new Todo("read book");
        todo.addTags(List.of(new Tag("fun")));
        todo.removeTags(List.of(new Tag("fun")));
        assertEquals("T | 0 | read book", todo.toSaveFormat());
    }

    /**
     * Verifies that two tasks of the same kind are duplicates when their
     * descriptions differ only in case, whatever their status and tags.
     */
    @Test
    public void isDuplicateOf_sameDescriptionInOtherCase_duplicate() {
        Todo doneAndTagged = new Todo("Read Book");
        doneAndTagged.markAsDone();
        doneAndTagged.addTags(List.of(new Tag("fun")));
        assertTrue(new Todo("read book").isDuplicateOf(doneAndTagged));
    }

    /**
     * Verifies that tasks of different kinds are never duplicates, even with
     * the same description.
     */
    @Test
    public void isDuplicateOf_differentKinds_notDuplicate() {
        Todo todo = new Todo("return book");
        Deadline deadline = new Deadline("return book", SECOND_OF_DECEMBER_6PM);
        assertFalse(todo.isDuplicateOf(deadline));
        assertFalse(deadline.isDuplicateOf(todo));
    }

    /**
     * Verifies that deadlines are duplicates only when they are due at the same time.
     */
    @Test
    public void isDuplicateOf_deadlines_comparesDueTime() {
        Deadline deadline = new Deadline("return book", SECOND_OF_DECEMBER_6PM);
        assertTrue(deadline.isDuplicateOf(new Deadline("return book", SECOND_OF_DECEMBER_6PM)));
        assertFalse(deadline.isDuplicateOf(new Deadline("return book", SECOND_OF_DECEMBER_4PM)));
    }

    /**
     * Verifies that events are duplicates only when both their start and end match.
     */
    @Test
    public void isDuplicateOf_events_comparesStartAndEnd() {
        Event event = new Event("meeting", SECOND_OF_DECEMBER_4PM, SECOND_OF_DECEMBER_6PM);
        assertTrue(event.isDuplicateOf(new Event("meeting", SECOND_OF_DECEMBER_4PM, SECOND_OF_DECEMBER_6PM)));
        assertFalse(event.isDuplicateOf(
                new Event("meeting", SECOND_OF_DECEMBER_4PM, SECOND_OF_DECEMBER_6PM.plusHours(1))));
        assertFalse(event.isDuplicateOf(
                new Event("meeting", SECOND_OF_DECEMBER_4PM.minusHours(1), SECOND_OF_DECEMBER_6PM)));
    }

    /**
     * Verifies that dates are shown in English whatever the computer's
     * language setting, to match the rest of Ted's replies.
     */
    @Test
    public void toString_chineseLocale_showsEnglishDate() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
            String shown = new Deadline("return book", SECOND_OF_DECEMBER_6PM).toString();
            assertEquals("[D][ ] return book (by: 2 Dec 2019, 6:00 PM)", shown);
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    /**
     * Verifies that a task is at a time of day only when one of its own
     * date-times is, and that a todo, having none, never is.
     */
    @Test
    public void isAtTimeOfDay_eachKindOfTask_checksItsOwnTimes() {
        LocalTime sixPm = LocalTime.of(18, 0);
        LocalTime fourPm = LocalTime.of(16, 0);

        assertFalse(new Todo("read book").isAtTimeOfDay(sixPm));
        assertTrue(new Deadline("return book", SECOND_OF_DECEMBER_6PM).isAtTimeOfDay(sixPm));
        assertFalse(new Deadline("return book", SECOND_OF_DECEMBER_6PM).isAtTimeOfDay(fourPm));

        Event meeting = new Event("meeting", SECOND_OF_DECEMBER_4PM, SECOND_OF_DECEMBER_6PM);
        assertTrue(meeting.isAtTimeOfDay(fourPm));
        assertTrue(meeting.isAtTimeOfDay(sixPm));
        assertFalse(meeting.isAtTimeOfDay(LocalTime.of(17, 0)));
    }
}
