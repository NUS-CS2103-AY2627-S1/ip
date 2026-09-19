package nori.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nori.NoriException;

/**
 * Tests the task types and the date range, at the edges their callers rely on.
 */
public class TaskModelTest {

    @Test
    public void markAsDone_newTask_changesTheStatusIcon() {
        Todo todo = new Todo("read book");
        assertEquals(" ", todo.getStatusIcon());
        assertFalse(todo.isDone());

        todo.markAsDone();

        assertEquals("X", todo.getStatusIcon());
        assertTrue(todo.isDone());
    }

    @Test
    public void markAsNotDone_completedTask_reopensIt() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.markAsNotDone();

        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void markAsDone_taskAlreadyDone_leavesItDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.markAsDone();

        assertTrue(todo.isDone());
    }

    @Test
    public void getDescription_anyTask_returnsItUnchanged() {
        assertEquals("read book", new Todo("read book").getDescription());
    }

    @Test
    public void toString_completedTodo_showsTheTypeAndStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void parseInput_validDate_returnsIt() throws NoriException {
        assertEquals(LocalDate.parse("2019-10-15"), Deadline.parseInput("2019-10-15"));
    }

    @Test
    public void parseInput_impossibleDate_throwsNoriException() {
        NoriException exception = assertThrows(
                NoriException.class, () -> Deadline.parseInput("2019-02-31"));
        assertEquals("NOOT?! I cannot understand \"2019-02-31\" as a deadline."
                + " Use a date like \"2019-10-15\".", exception.getMessage());
    }

    @Test
    public void fromStorage_validDate_rebuildsTheDeadline() throws NoriException {
        Deadline deadline = Deadline.fromStorage("return book", "2019-06-06");

        assertEquals("[D][ ] return book (by: Jun 06 2019)", deadline.toString());
    }

    @Test
    public void fromStorage_unreadableDate_throwsNoriException() {
        NoriException exception = assertThrows(
                NoriException.class, () -> Deadline.fromStorage("return book", "not a date"));
        assertEquals("SAD NOOT! I couldn't read your saved tasks off the ice.",
                exception.getMessage());
    }

    @Test
    public void getStorageDate_anyDeadline_returnsTheIsoDate() {
        assertEquals("2019-06-06",
                new Deadline("return book", LocalDate.parse("2019-06-06")).getStorageDate());
    }

    @Test
    public void occursOn_deadlineDueThatDay_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-06-06"));

        assertTrue(deadline.occursOn(LocalDate.parse("2019-06-06")));
        assertFalse(deadline.occursOn(LocalDate.parse("2019-06-05")));
        assertFalse(deadline.occursOn(LocalDate.parse("2019-06-07")));
    }

    @Test
    public void occursInDateRange_deadlineAtEitherBoundary_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-06-06"));

        assertTrue(deadline.occursInDateRange(LocalDate.parse("2019-06-06"),
                LocalDate.parse("2019-06-06")));
        assertTrue(deadline.occursInDateRange(LocalDate.parse("2019-06-06"),
                LocalDate.parse("2019-12-31")));
        assertTrue(deadline.occursInDateRange(LocalDate.parse("2019-01-01"),
                LocalDate.parse("2019-06-06")));
        assertFalse(deadline.occursInDateRange(LocalDate.parse("2019-06-07"),
                LocalDate.parse("2019-12-31")));
        assertFalse(deadline.occursInDateRange(LocalDate.parse("2019-01-01"),
                LocalDate.parse("2019-06-05")));
    }

    @Test
    public void occursOn_eventWithNoDates_returnsFalse() throws NoriException {
        assertFalse(new Event("standup", "2pm", "3pm").occursOn(LocalDate.parse("2019-06-06")));
    }

    @Test
    public void occursOn_eventDatedOnOneSideOnly_matchesThatDate() throws NoriException {
        Event event = new Event("fair", "2019-06-06 1000", "1800");

        assertTrue(event.occursOn(LocalDate.parse("2019-06-06")));
        assertFalse(event.occursOn(LocalDate.parse("2019-06-07")));
    }

    @Test
    public void occursInDateRange_eventWithNoDates_returnsFalse() throws NoriException {
        assertFalse(new Event("standup", "2pm", "3pm")
                .occursInDateRange(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31")));
    }

    @Test
    public void occursInDateRange_eventDatedOnItsEndOnly_matchesThatDate() throws NoriException {
        Event event = new Event("fair", "morning", "2019-06-06 1800");

        assertTrue(event.occursInDateRange(LocalDate.parse("2019-06-01"),
                LocalDate.parse("2019-06-30")));
        assertFalse(event.occursInDateRange(LocalDate.parse("2019-07-01"),
                LocalDate.parse("2019-07-31")));
    }

    @Test
    public void occursInDateRange_eventTouchingOnlyTheRangeEdge_returnsTrue() throws NoriException {
        Event event = new Event("conference", "2019-06-01", "2019-06-08");

        assertTrue(event.occursInDateRange(LocalDate.parse("2019-06-08"),
                LocalDate.parse("2019-06-30")));
        assertTrue(event.occursInDateRange(LocalDate.parse("2019-05-01"),
                LocalDate.parse("2019-06-01")));
        assertFalse(event.occursInDateRange(LocalDate.parse("2019-06-09"),
                LocalDate.parse("2019-06-30")));
    }

    @Test
    public void getFromAndGetTo_anyEvent_returnTheDetailsAsTyped() throws NoriException {
        Event event = new Event("fair", "Mon 2pm", "4pm");

        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    public void constructor_missingEventDetail_throwsNoriException() {
        NoriException exception = assertThrows(
                NoriException.class, () -> new Event("fair", null, "4pm"));
        assertEquals("NOOT?! An event cannot have a missing date or time.", exception.getMessage());
    }

    @Test
    public void dateRange_anyRange_returnsTheDatesItWasBuiltFrom() {
        DateRange range = new DateRange(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-12-31"));

        assertEquals(LocalDate.parse("2019-01-01"), range.getFrom());
        assertEquals(LocalDate.parse("2019-12-31"), range.getTo());
    }

    @Test
    public void dateRange_oneDay_isAllowed() {
        DateRange range = new DateRange(LocalDate.parse("2019-06-06"), LocalDate.parse("2019-06-06"));

        assertEquals(range.getFrom(), range.getTo());
    }

    @Test
    public void taskList_builtFromTasks_holdsThemInOrder() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(first, second);

        assertEquals(2, tasks.size());
        assertFalse(tasks.isEmpty());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    public void taskList_newList_isEmpty() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
        assertArrayEquals(new String[] {"The iceberg is empty. Try \"todo borrow book\". Noot noot!"},
                tasks.getDisplayLines());
    }

    @Test
    public void add_atAnIndex_insertsWithoutLosingTheOthers() {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("third"));

        tasks.add(1, new Todo("second"));

        assertArrayEquals(new String[] {"Noot noot! Tasks currently chilling on the iceberg:",
            "1.[T][ ] first", "2.[T][ ] second", "3.[T][ ] third"}, tasks.getDisplayLines());
    }

    @Test
    public void remove_anyIndex_returnsTheTaskItTookOut() {
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(new Todo("first"), second);

        assertSame(second, tasks.remove(1));
        assertEquals(1, tasks.size());
    }

    @Test
    public void asUnmodifiableList_anyList_cannotBeChangedThrough() {
        TaskList tasks = new TaskList(new Todo("first"));

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.asUnmodifiableList().add(new Todo("sneaked in")));
    }

    @Test
    public void getScheduleDisplayLines_dayWithNothingOnIt_saysSo() {
        TaskList tasks = new TaskList();

        assertArrayEquals(
                new String[] {"Nothing on the schedule for 2019-06-06. The ice is quiet."},
                tasks.getScheduleDisplayLines(LocalDate.parse("2019-06-06")));
    }

    @Test
    public void getTasksOnDateDisplayLines_quietDate_saysSo() {
        TaskList tasks = new TaskList(new Todo("read book"));

        assertArrayEquals(new String[] {"Nothing is hatching on 2019-06-06. The ice is quiet."},
                tasks.getTasksOnDateDisplayLines(LocalDate.parse("2019-06-06")));
    }
}
