package serangooner.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import serangooner.SerangoonerException;

public class TaskTest {
    /** Smallest possible task type, so the base class is tested on its own terms. */
    private static class PlainTask extends Task {
        PlainTask(String description) {
            super(description);
        }

        @Override
        public String getTypeName() {
            return "plain";
        }
    }

    @Test
    public void constructor_newTask_startsNotDone() {
        assertFalse(new PlainTask("read book").isDone());
    }

    @Test
    public void constructor_descriptionCarryingTheSaveDelimiter_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> new PlainTask("read book | write essay"));
    }

    @Test
    public void constructor_barePipeWithoutSurroundingSpaces_isAllowed() {
        // Only the delimiter as written, with its spaces, would split a saved line.
        assertEquals("[ ] either|or", new PlainTask("either|or").toString());
    }

    @Test
    public void markDone_notDoneTask_marksItDone() {
        Task task = new PlainTask("read book");
        task.markDone();
        assertTrue(task.isDone());
    }

    @Test
    public void markDone_alreadyDoneTask_leavesItDone() {
        Task task = new PlainTask("read book");
        task.markDone();
        task.markDone();
        assertTrue(task.isDone());
    }

    @Test
    public void markNotDone_doneTask_marksItIncomplete() {
        Task task = new PlainTask("read book");
        task.markDone();
        task.markNotDone();
        assertFalse(task.isDone());
    }

    @Test
    public void markNotDone_taskThatWasNeverDone_leavesItIncomplete() {
        Task task = new PlainTask("read book");
        task.markNotDone();
        assertFalse(task.isDone());
    }

    @Test
    public void isWithin_anyRange_returnsFalseForATaskCarryingNoDate() {
        Task task = new PlainTask("read book");
        assertFalse(task.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
        assertFalse(task.isWithin(LocalDate.MIN, LocalDate.MAX));
    }

    @Test
    public void toSaveFormat_notDone_writesTheNotDoneFlagThenTheDescription() {
        assertEquals("0 | read book", new PlainTask("read book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_writesTheDoneFlag() {
        Task task = new PlainTask("read book");
        task.markDone();
        assertEquals("1 | read book", task.toSaveFormat());
    }

    @Test
    public void toString_notDone_showsAnEmptyBox() {
        assertEquals("[ ] read book", new PlainTask("read book").toString());
    }

    @Test
    public void toString_done_showsATickedBox() {
        Task task = new PlainTask("read book");
        task.markDone();
        assertEquals("[✓] read book", task.toString());
    }

    @Test
    public void isDuplicateOf_sameDescriptionInAnotherCaseAndSpacing_returnsTrue() {
        assertTrue(new PlainTask("  Read   Book ").isDuplicateOf(new PlainTask("read book")));
    }

    @Test
    public void isDuplicateOf_differentDescription_returnsFalse() {
        assertFalse(new PlainTask("read book").isDuplicateOf(new PlainTask("read books")));
    }

    @Test
    public void isDuplicateOf_onlyOneOfThemDone_stillReturnsTrue() {
        Task doneTask = new PlainTask("read book");
        doneTask.markDone();
        assertTrue(doneTask.isDuplicateOf(new PlainTask("read book")));
    }

    @Test
    public void isDuplicateOf_sameDescriptionButAnotherKind_returnsFalse() {
        assertFalse(new PlainTask("read book").isDuplicateOf(new Todo("read book")));
        assertFalse(new Todo("read book").isDuplicateOf(new PlainTask("read book")));
    }

    @Test
    public void isDuplicateOf_defaultLocaleTurkish_stillIgnoresCase() {
        // Turkish lower-cases "I" to a dotless "ı", which would make "FILE" differ from "file".
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            assertTrue(new PlainTask("FILE taxes").isDuplicateOf(new PlainTask("file taxes")));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
