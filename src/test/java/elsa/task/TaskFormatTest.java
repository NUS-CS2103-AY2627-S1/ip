package elsa.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import elsa.ElsaException;

/**
 * Tests {@link TaskFormat#decode}, which turns one line of the data file back
 * into a task.
 *
 * <p>This is the method with the most ways to go wrong in the whole program. It
 * is also the one place where bad input comes from a file rather than from a
 * person, so nobody is watching when it does: a line that decodes into the wrong
 * task produces a task list that is quietly incorrect, and a line that throws
 * where it should not costs the user a task they had saved. Every branch is
 * therefore given a case here.
 *
 * <p>Writing is checked in the task classes' own tests; what is checked here as
 * well is that the two directions meet, since a task that can be written but not
 * read back would be lost on the next start.
 */
public class TaskFormatTest {

    // ------------------------------------------------------------------
    // Lines that are correct
    // ------------------------------------------------------------------

    @Test
    public void decode_todoNotDone_returnsTodo() throws ElsaException {
        Task task = TaskFormat.decode("T | 0 | read book");
        assertTrue(task instanceof Todo);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void decode_todoDone_returnsTodoMarkedDone() throws ElsaException {
        assertEquals("[T][X] read book", TaskFormat.decode("T | 1 | read book").toString());
    }

    @Test
    public void decode_deadline_returnsDeadlineWithItsDate() throws ElsaException {
        Task task = TaskFormat.decode("D | 0 | return book | 2999-01-01");
        assertTrue(task instanceof Deadline);
        assertEquals("[D][ ] return book (by: Jan 01 2999)", task.toString());
    }

    @Test
    public void decode_event_returnsEventWithBothDates() throws ElsaException {
        Task task = TaskFormat.decode("E | 1 | project meeting | 2019-10-14 | 2019-10-16");
        assertTrue(task instanceof Event);
        assertEquals("[E][X] project meeting (from: Oct 14 2019 to: Oct 16 2019)",
                task.toString());
    }

    /**
     * A description may contain anything except the separator, spaces and
     * punctuation included, so it must not be trimmed, split or otherwise tidied.
     */
    @Test
    public void decode_descriptionWithPunctuation_keepsItUnchanged() throws ElsaException {
        Task task = TaskFormat.decode("T | 0 | read \"book\" (2nd ed.), ch. 1-3");
        assertEquals("[T][ ] read \"book\" (2nd ed.), ch. 1-3", task.toString());
    }

    /**
     * A deadline stored with more fields than it needs still has all the fields it
     * needs, so it is read rather than refused. Only the ones it uses are read.
     */
    @Test
    public void decode_deadlineWithAnExtraField_readsTheFieldsItNeeds() throws ElsaException {
        Task task = TaskFormat.decode("D | 0 | return book | 2019-10-15 | ignored");
        assertEquals("[D][ ] return book (by: Oct 15 2019) -- overdue", task.toString());
    }

    // ------------------------------------------------------------------
    // Lines that are too short
    // ------------------------------------------------------------------

    @Test
    public void decode_lineWithTwoFields_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("T | 0"));
    }

    @Test
    public void decode_lineWithOneField_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("T"));
    }

    @Test
    public void decode_emptyLine_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode(""));
    }

    @Test
    public void decode_deadlineWithoutItsDate_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("D | 0 | return book"));
    }

    @Test
    public void decode_eventWithOnlyOneDate_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("E | 0 | project meeting | 2019-10-14"));
    }

    // ------------------------------------------------------------------
    // Fields that are there but wrong
    // ------------------------------------------------------------------

    @Test
    public void decode_blankDescription_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("D | 0 |  | 2019-10-15"));
    }

    @Test
    public void decode_unknownTypeLetter_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("X | 0 | read book"));
    }

    /** The type letter is upper case, so a lower case one is not that type. */
    @Test
    public void decode_lowerCaseTypeLetter_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("t | 0 | read book"));
    }

    /**
     * Anything other than the two markers means the line cannot be trusted, so it
     * is refused rather than quietly assumed to mean not done.
     */
    @Test
    public void decode_doneMarkerThatIsNeitherZeroNorOne_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("T | 2 | read book"));
        assertThrows(ElsaException.class, () -> TaskFormat.decode("T | yes | read book"));
        assertThrows(ElsaException.class, () -> TaskFormat.decode("T |  | read book"));
    }

    @Test
    public void decode_deadlineWithTextInsteadOfADate_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("D | 0 | return book | someday"));
    }

    /**
     * A date the calendar does not have is refused here just as it is when typed,
     * because both go through the same date reader.
     */
    @Test
    public void decode_deadlineWithAnImpossibleDate_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("D | 0 | return book | 2019-02-30"));
    }

    @Test
    public void decode_eventWithABadSecondDate_throwsException() {
        assertThrows(ElsaException.class, () -> TaskFormat.decode("E | 0 | meeting | 2019-10-14 | not-a-date"));
    }

    /**
     * The message goes into the list of problems the user is shown after a load,
     * so it has to say what was wrong with the line. Only the offending value is
     * checked, so that rewording the sentence around it does not fail this test.
     */
    @Test
    public void decode_unknownTypeLetter_messageNamesTheLetter() {
        ElsaException thrown = assertThrows(ElsaException.class, () -> TaskFormat.decode("X | 0 | read book"));
        assertTrue(thrown.getMessage().contains("X"));
    }

    // ------------------------------------------------------------------
    // Writing and reading meet
    // ------------------------------------------------------------------

    /**
     * Every kind of task must survive being written to the file and read back, or
     * a task would be saved and then refused the next time the chatbot starts.
     * Reading is a switch here while writing is done by each task itself, so the
     * two are separate pieces of code that have to agree, and this is what checks
     * that they still do.
     */
    @Test
    public void toSaveFormatThenDecode_everyKindOfTask_givesAnEqualTask() throws ElsaException {
        Task[] tasks = {
            new Todo("read book"),
            new Deadline("return book", LocalDate.of(2999, 1, 1)),
            new Event("project meeting", LocalDate.of(2019, 10, 14),
                    LocalDate.of(2019, 10, 16)),
        };
        for (Task task : tasks) {
            assertEquals(task.toString(), TaskFormat.decode(task.toSaveFormat()).toString(),
                    "round trip failed for " + task);
            task.markAsDone();
            assertEquals(task.toString(), TaskFormat.decode(task.toSaveFormat()).toString(),
                    "round trip failed once done for " + task);
        }
    }
}
