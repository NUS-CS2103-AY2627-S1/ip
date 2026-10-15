package zaru.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zaru.exception.ZaruException;
import zaru.storage.Storage;

/** Tests task construction, identity, ordering, and display formatting. */
public class TaskTest {
    private static final LocalDateTime MORNING = LocalDateTime.of(2026, 9, 8, 9, 0);
    private static final LocalDateTime NOON = LocalDateTime.of(2026, 9, 8, 12, 0);
    private static final LocalDateTime EVENING = LocalDateTime.of(2026, 9, 8, 18, 0);

    @TempDir
    Path temporaryDirectory;

    /** Verifies that marking an already marked task, and vice versa, produces an exception. */
    @Test
    public void updateCompletionState_redundantChange_throwsException() throws ZaruException {
        TaskList tasks = new TaskList(new Storage(temporaryDirectory.resolve("completion-state.txt")));
        tasks.add(new ToDo("completed task", true));
        tasks.add(new ToDo("incomplete task"));

        ZaruException alreadyMarked = assertThrows(ZaruException.class, () -> tasks.markAsComplete(1));
        ZaruException alreadyUnmarked = assertThrows(ZaruException.class, () -> tasks.markAsIncomplete(2));

        assertEquals("That task is already marked as done.", alreadyMarked.getMessage());
        assertEquals("That task is already marked as incomplete.", alreadyUnmarked.getMessage());
        assertTrue(tasks.getTaskString(1).contains("[x]"));
        assertTrue(tasks.getTaskString(2).contains("[ ]"));
    }

    /** Verifies that todo identity ignores case and completion but respects type and description. */
    @Test
    public void hasSameDetails_todoVariants_returnsExpectedResult() {
        ToDo task = new ToDo("Read Book", true);

        assertTrue(task.hasSameDetails(new ToDo("read book", false)));
        assertFalse(task.hasSameDetails(new ToDo("write notes")));
        assertFalse(task.hasSameDetails(new Deadline("Read Book", MORNING)));
        assertFalse(task.hasSameDetails(null));
    }

    /** Verifies that deadline identity includes its due time. */
    @Test
    public void hasSameDetails_deadlineVariants_returnsExpectedResult() {
        Deadline deadline = new Deadline("Submit report", MORNING);

        assertTrue(deadline.hasSameDetails(new Deadline("submit report", MORNING)));
        assertFalse(deadline.hasSameDetails(new Deadline("submit report", NOON)));
        assertFalse(deadline.hasSameDetails(new ToDo("submit report")));
    }

    /** Verifies that event identity includes both boundary times. */
    @Test
    public void hasSameDetails_eventVariants_returnsExpectedResult() throws ZaruException {
        Event event = new Event("Meeting", MORNING, NOON);

        assertTrue(event.hasSameDetails(new Event("meeting", MORNING, NOON)));
        assertFalse(event.hasSameDetails(new Event("meeting", MORNING, EVENING)));
        assertFalse(event.hasSameDetails(new Event("meeting", NOON, EVENING)));
        assertFalse(event.hasSameDetails(new Event("workshop", MORNING, NOON)));
        assertFalse(event.hasSameDetails(new ToDo("Meeting")));
    }

    /** Verifies deadline ordering by due time and then description. */
    @Test
    public void compareTo_deadlines_ordersByDateThenDescription() {
        Deadline early = new Deadline("Zebra", MORNING);
        Deadline late = new Deadline("Alpha", NOON);
        Deadline sameTimeEarlierTitle = new Deadline("Alpha", MORNING);

        assertTrue(early.compareTo(late) < 0);
        assertTrue(late.compareTo(early) > 0);
        assertTrue(sameTimeEarlierTitle.compareTo(early) < 0);
    }

    /** Verifies event ordering by start, end, and description in that order. */
    @Test
    public void compareTo_events_ordersByBoundariesThenDescription() throws ZaruException {
        Event earlyStart = new Event("Zebra", MORNING, NOON);
        Event lateStart = new Event("Alpha", NOON, EVENING);
        Event laterEnd = new Event("Alpha", MORNING, EVENING);
        Event sameTimesEarlierTitle = new Event("Alpha", MORNING, NOON);

        assertTrue(earlyStart.compareTo(lateStart) < 0);
        assertTrue(earlyStart.compareTo(laterEnd) < 0);
        assertTrue(sameTimesEarlierTitle.compareTo(earlyStart) < 0);
    }

    /** Verifies ordering between different task types. */
    @Test
    public void compareTo_differentTaskTypes_ordersTodoDeadlineEvent() throws ZaruException {
        ToDo todo = new ToDo("todo");
        Deadline deadline = new Deadline("deadline", MORNING);
        Event event = new Event("event", MORNING, NOON);

        assertTrue(todo.compareTo(deadline) < 0);
        assertTrue(deadline.compareTo(event) < 0);
        assertTrue(event.compareTo(todo) > 0);
    }

    /** Verifies task display markers and parsed constructor values. */
    @Test
    public void constructors_andToString_preserveTaskDetails() throws ZaruException {
        ToDo todo = new ToDo("read book");
        Deadline deadline = new Deadline("submit report", "2026-09-08 1200");
        Event event = new Event("meeting", "2026-09-08 0900", "2026-09-08 1200");

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals(NOON, deadline.getDueDate());
        assertEquals(MORNING, event.getFrom());
        assertEquals(NOON, event.getTo());
        assertTrue(deadline.toString().contains("Sep 08 2026, 12:00PM"));
        assertTrue(event.toString().contains("from: Sep 08 2026, 9:00AM"));
    }

    /** Verifies that constructor invariants reject null values when assertions are enabled. */
    @Test
    public void constructors_nullRequiredValue_assertionThrown() {
        assertThrows(AssertionError.class, () -> new ToDo(null));
        assertThrows(AssertionError.class, () -> new Deadline("deadline", (LocalDateTime) null));
        assertThrows(AssertionError.class, () -> new Event("event", null, NOON));
        assertThrows(AssertionError.class, () -> new Event("event", MORNING, null));
    }
}
