package davidgoggins.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import davidgoggins.DavidGogginsException;

/**
 * Tests what every {@link Task} shares: the save format, dates, and duplicate detection.
 *
 * <p>Many inputs share one expectation here, e.g. "every malformed line is refused", so
 * those cases are written as parameterized tests: one method, run once per input.
 */
public class TaskTest {

    /** Reading a line and writing it back must give the same line, or saves would drift. */
    @ParameterizedTest
    @ValueSource(strings = {
        "T | 0 | read book",
        "T | 1 | read book",
        "D | 0 | return book | 2026-09-10",
        "D | 1 | return book | 2026-09-10",
        "E | 0 | camp | 2026-10-01 | 2026-10-03",
        "E | 1 | camp | 2026-10-01 | 2026-10-01",
    })
    public void fromSaveFormat_validLine_sameLineSavedBack(String line) throws DavidGogginsException {
        assertEquals(line, Task.fromSaveFormat(line).toSaveFormat());
    }

    /** A hand-edited file may lose the spaces around the bars. */
    @Test
    public void fromSaveFormat_noSpacesAroundSeparators_fieldsTrimmed() throws DavidGogginsException {
        Task task = Task.fromSaveFormat("T|1|  read book  ");
        assertEquals("[T][X] read book", task.toString());
        assertTrue(task.isDone());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "no separators at all",
        "T | 0",
        "X | 0 | unknown type marker",
        "T | 2 | done flag is not 0 or 1",
        "T | 0 |",
        "T | 0 | too many | fields",
        "D | 0 | no due date",
        "D | 0 | empty due date |",
        "D | 0 | due date in words | tomorrow",
        "E | 0 | too few fields | 2026-10-01",
        "E | 0 | ends before it starts | 2026-10-03 | 2026-10-01",
        "E | 0 | impossible start | 2026-02-30 | 2026-03-01",
    })
    public void fromSaveFormat_malformedLine_exceptionThrown(String line) {
        assertThrows(DavidGogginsException.class, () -> Task.fromSaveFormat(line));
    }

    @Test
    public void markAsNotDone_doneTask_statusIconCleared() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    /** Each of these names a day that does not exist, so it needs different advice from a typo. */
    @ParameterizedTest
    @ValueSource(strings = {"2026-02-29", "2027-02-29", "2026-04-31", "2026-13-01", "2026-00-10", "2026-01-32"})
    public void deadlineConstructor_impossibleDate_notARealDateMessage(String date) {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                new Deadline("pay rent", date));
        assertTrue(e.getMessage().contains("not a real date"), e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"tomorrow", "10-09-2026", "2026/09/10", "2026-9-10", ""})
    public void deadlineConstructor_wrongFormat_formatMessage(String date) {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                new Deadline("pay rent", date));
        assertTrue(e.getMessage().contains("yyyy-mm-dd"), e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0001-01-01", "1899-12-31", "2101-01-01", "9999-12-31"})
    public void deadlineConstructor_unrealisticYear_yearRangeMessage(String date) {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                new Deadline("pay rent", date));
        assertTrue(e.getMessage().contains("Pick a year from 1900 to 2100"), e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1900-01-01", "2100-12-31"})
    public void deadlineConstructor_boundaryYear_deadlineCreated(String date) throws DavidGogginsException {
        assertTrue(new Deadline("pay rent", date).toString().contains(date));
    }

    @Test
    public void deadlineConstructor_leapDay_deadlineCreated() throws DavidGogginsException {
        assertEquals("[D][ ] pay rent (by: 2028-02-29)", new Deadline("pay rent", "2028-02-29").toString());
    }

    @Test
    public void isDuplicateOf_sameTodoDifferentCaseAndSpacing_true() {
        assertTrue(new Todo("Read  Book").isDuplicateOf(new Todo("read book")));
    }

    /** Logging a finished task again is still a repeat of the same work. */
    @Test
    public void isDuplicateOf_oneDoneOneNot_true() {
        Todo done = new Todo("read book");
        done.markAsDone();
        assertTrue(done.isDuplicateOf(new Todo("read book")));
    }

    @Test
    public void isDuplicateOf_differentDescription_false() {
        assertFalse(new Todo("read book").isDuplicateOf(new Todo("read books")));
    }

    @Test
    public void isDuplicateOf_sameDescriptionDifferentType_false() throws DavidGogginsException {
        assertFalse(new Todo("return book").isDuplicateOf(new Deadline("return book", "2026-09-10")));
    }

    @Test
    public void isDuplicateOf_null_false() {
        assertFalse(new Todo("read book").isDuplicateOf(null));
    }

    @Test
    public void isDuplicateOf_deadlinesWithSameDate_true() throws DavidGogginsException {
        assertTrue(new Deadline("return book", "2026-09-10")
                .isDuplicateOf(new Deadline("RETURN book", "2026-09-10")));
    }

    @Test
    public void isDuplicateOf_deadlinesWithDifferentDates_false() throws DavidGogginsException {
        assertFalse(new Deadline("return book", "2026-09-10")
                .isDuplicateOf(new Deadline("return book", "2026-09-11")));
    }

    @Test
    public void matches_keywordInMiddleOfWord_true() {
        assertTrue(new Todo("run 10 miles").matches("MILE"));
    }

    /** Matching must not depend on the machine's language, where "I" can lower-case oddly. */
    @Test
    public void matches_upperCaseIOnTurkishMachine_stillMatches() {
        Locale original = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            assertTrue(new Todo("LIFT weights").matches("lift"));
            assertTrue(new Todo("LIFT weights").isDuplicateOf(new Todo("lift weights")));
        } finally {
            Locale.setDefault(original);
        }
    }
}
