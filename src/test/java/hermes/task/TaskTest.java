package hermes.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the behaviour shared by every kind of task, and the parts each of
 * {@link ToDo}, {@link Deadline} and {@link Event} adds on top.
 *
 * <p>Dates are kept in August so the month reads "Aug" whatever the locale of the
 * machine running the tests.
 */
public class TaskTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 27, 15, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 8, 27, 16, 30);

    @Test
    public void mark_incompleteTask_marksAndReportsChange() {
        ToDo todo = new ToDo("read");

        assertTrue(todo.mark());
        assertTrue(todo.isCompleted());
    }

    @Test
    public void mark_completedTask_staysCompletedAndReportsNoChange() {
        ToDo todo = new ToDo(true, "read");

        assertFalse(todo.mark());
        assertTrue(todo.isCompleted());
    }

    @Test
    public void unmark_completedTask_unmarksAndReportsChange() {
        ToDo todo = new ToDo(true, "read");

        assertTrue(todo.unmark());
        assertFalse(todo.isCompleted());
    }

    @Test
    public void unmark_incompleteTask_staysIncompleteAndReportsNoChange() {
        ToDo todo = new ToDo("read");

        assertFalse(todo.unmark());
        assertFalse(todo.isCompleted());
    }

    @Test
    public void getTaskDescription_newTask_returnsDescription() {
        assertEquals("read", new ToDo("read").getTaskDescription());
    }

    @Test
    public void toString_eachTaskType_showsTypeStatusAndDates() {
        assertEquals("[T][ ] read", new ToDo("read").toString());
        assertEquals("[T][X] read", new ToDo(true, "read").toString());
        assertEquals("[D][ ] essay (by: 27/08/2026 1500)", new Deadline("essay", START).toString());
        assertEquals("[E][X] meeting (from: 27/08/2026 1500 to: 27/08/2026 1630)",
                new Event(true, "meeting", START.toString(), END.toString()).toString());
    }

    @Test
    public void getFileContent_eachTaskType_matchesStoredLineFormat() {
        assertEquals("T | 0 | read", new ToDo("read").getFileContent());
        assertEquals("T | 1 | read", new ToDo(true, "read").getFileContent());
        assertEquals("D | 1 | essay | 2026-08-27T15:00",
                new Deadline(true, "essay", "2026-08-27T15:00").getFileContent());
        assertEquals("E | 0 | meeting | 2026-08-27T15:00 | 2026-08-27T16:30",
                new Event("meeting", START, END).getFileContent());
    }

    @Test
    public void getDueDateTime_eachTaskType_returnsTheMomentMeasuredAgainst() {
        assertNull(new ToDo("read").getDueDateTime());
        assertEquals(START, new Deadline("essay", START).getDueDateTime());
        // An event is measured by when it starts, not when it ends.
        assertEquals(START, new Event("meeting", START, END).getDueDateTime());
    }

    @Test
    public void isDueBy_todo_neverDue() {
        assertFalse(new ToDo("read").isDueBy(LocalDateTime.MAX));
    }

    @Test
    public void isDueBy_deadline_dueUpToAndIncludingItsDate() {
        Deadline deadline = new Deadline("essay", START);

        assertTrue(deadline.isDueBy(START.plusMinutes(1)));
        assertTrue(deadline.isDueBy(START));
        assertFalse(deadline.isDueBy(START.minusMinutes(1)));
    }

    @Test
    public void copy_todo_independentOfOriginal() {
        ToDo original = new ToDo("read");
        ToDo copy = original.copy();

        original.mark();

        assertNotSame(original, copy);
        assertEquals("[T][ ] read", copy.toString());
    }

    @Test
    public void copy_deadline_keepsDateToTheSecondAndIsIndependent() {
        LocalDateTime withSeconds = LocalDateTime.of(2026, 8, 27, 15, 0, 30);
        Deadline original = new Deadline("essay", withSeconds);
        Deadline copy = original.copy();

        original.mark();

        assertEquals(withSeconds, copy.getDueDateTime());
        assertFalse(copy.isCompleted());
    }

    @Test
    public void copy_event_keepsBothDatesAndIsIndependent() {
        Event original = new Event(true, "meeting", START.toString(), END.toString());
        Event copy = original.copy();

        original.unmark();

        assertEquals("E | 1 | meeting | 2026-08-27T15:00 | 2026-08-27T16:30", copy.getFileContent());
    }

    @Test
    public void hasSameDetails_sameErrand_trueWhateverTheirStatus() {
        assertTrue(new ToDo("read").hasSameDetails(new ToDo("read")));
        // Completing one copy does not make it a different errand.
        assertTrue(new ToDo(true, "read").hasSameDetails(new ToDo("read")));
        assertTrue(new Deadline("essay", START).hasSameDetails(new Deadline(false, "essay", START.toString())));
        assertTrue(new Event("meeting", START, END)
                .hasSameDetails(new Event(true, "meeting", START.toString(), END.toString())));
    }

    @Test
    public void hasSameDetails_differentKindDescriptionOrDate_false() {
        assertFalse(new ToDo("read").hasSameDetails(new ToDo("write")));
        assertFalse(new ToDo("essay").hasSameDetails(new Deadline("essay", START)));
        assertFalse(new Deadline("essay", START).hasSameDetails(new Deadline("essay", END)));
        assertFalse(new Event("meeting", START, END)
                .hasSameDetails(new Event("meeting", START, END.plusHours(1))));
    }

    @Test
    public void compareTo_differentRanks_datedThenUndatedThenCompleted() {
        Task dated = new Deadline("essay", START);
        Task undated = new ToDo("read");
        Task completed = new Deadline(true, "done", START.minusDays(1).toString());

        assertTrue(dated.compareTo(undated) < 0);
        assertTrue(undated.compareTo(completed) < 0);
        assertTrue(dated.compareTo(completed) < 0);
        assertTrue(completed.compareTo(dated) > 0);
    }

    @Test
    public void compareTo_twoDatedTasks_soonerFirstAcrossTaskTypes() {
        Task laterDeadline = new Deadline("essay", END);
        Task earlierEvent = new Event("meeting", START, END.plusDays(1));

        assertTrue(earlierEvent.compareTo(laterDeadline) < 0);
        assertTrue(laterDeadline.compareTo(earlierEvent) > 0);
    }

    @Test
    public void compareTo_sameUndatedOrCompletedRank_treatedAsEqual() {
        assertEquals(0, new ToDo("a").compareTo(new ToDo("b")));
        // Completed tasks are not ordered by date, even when they have one.
        assertEquals(0, new Deadline(true, "a", START.toString())
                .compareTo(new Deadline(true, "b", END.toString())));
    }
}
