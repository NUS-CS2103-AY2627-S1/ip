package seedu.mattchatbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests what each task type shows, saves, and counts as the same as itself.
 */
public class TaskTest {

    private static LocalDateTime at(int year, int month, int day, int hour) {
        return LocalDateTime.of(year, month, day, hour, 0);
    }

    // ---------- what each type calls itself ----------

    @Test
    public void getTypeName_eachType_namedForTheUser() throws Exception {
        assertEquals("todo", new Todo("x").getTypeName());
        assertEquals("deadline", new Deadline("x", at(2019, 10, 15, 0)).getTypeName());
        assertEquals("event",
                new Event("x", at(2019, 10, 15, 9), at(2019, 10, 15, 10)).getTypeName());
    }

    @Test
    public void getTypeIcon_eachType_matchesTheSaveFormat() throws Exception {
        assertEquals(Todo.TYPE_ICON, new Todo("x").getTypeIcon());
        assertEquals(Deadline.TYPE_ICON, new Deadline("x", at(2019, 10, 15, 0)).getTypeIcon());
        assertEquals(Event.TYPE_ICON,
                new Event("x", at(2019, 10, 15, 9), at(2019, 10, 15, 10)).getTypeIcon());
    }

    // ---------- status ----------

    @Test
    public void getStatusIcon_beforeAndAfterMarking_changes() {
        Todo todo = new Todo("read book");
        assertEquals(" ", todo.getStatusIcon());
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
        todo.markAsNotDone();
        assertEquals(" ", todo.getStatusIcon());
    }

    // ---------- saving ----------

    @Test
    public void toFileFormat_eachType_fieldsInOrder() throws Exception {
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
        assertEquals("D | 0 | return book | 2019-10-15 0000",
                new Deadline("return book", at(2019, 10, 15, 0)).toFileFormat());
        assertEquals("E | 0 | meeting | 2019-10-15 1400 | 2019-10-15 1600",
                new Event("meeting", at(2019, 10, 15, 14), at(2019, 10, 15, 16)).toFileFormat());
    }

    @Test
    public void toFileFormat_doneTask_statusIsOne() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    // ---------- falling on a date ----------

    @Test
    public void isOn_todo_neverFallsOnADate() {
        assertFalse(new Todo("read book").isOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void isOn_deadline_onlyItsOwnDay() throws Exception {
        Deadline deadline = new Deadline("return book", at(2019, 10, 15, 18));
        assertTrue(deadline.isOn(LocalDate.of(2019, 10, 15)));
        assertFalse(deadline.isOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void isOn_multiDayEvent_everyDayItCovers() throws Exception {
        // An event runs across the days between its ends, not only the first.
        Event event = new Event("orientation", at(2019, 10, 14, 9), at(2019, 10, 16, 17));
        assertFalse(event.isOn(LocalDate.of(2019, 10, 13)));
        assertTrue(event.isOn(LocalDate.of(2019, 10, 14)));
        assertTrue(event.isOn(LocalDate.of(2019, 10, 15)));
        assertTrue(event.isOn(LocalDate.of(2019, 10, 16)));
        assertFalse(event.isOn(LocalDate.of(2019, 10, 17)));
    }

    // ---------- what counts as the same entry ----------

    @Test
    public void isSameTask_deadlinesDifferingOnlyByDueDate_notTheSame() throws Exception {
        Deadline october = new Deadline("pay rent", at(2019, 10, 1, 0));
        Deadline november = new Deadline("pay rent", at(2019, 11, 1, 0));
        assertFalse(october.isSameTask(november));
        assertTrue(october.isSameTask(new Deadline("pay rent", at(2019, 10, 1, 0))));
    }

    @Test
    public void isSameTask_eventsDifferingOnlyByEndTime_notTheSame() throws Exception {
        Event shorter = new Event("meeting", at(2019, 10, 15, 14), at(2019, 10, 15, 15));
        Event longer = new Event("meeting", at(2019, 10, 15, 14), at(2019, 10, 15, 16));
        assertFalse(shorter.isSameTask(longer));
        assertTrue(shorter.isSameTask(
                new Event("meeting", at(2019, 10, 15, 14), at(2019, 10, 15, 15))));
    }

    @Test
    public void isSameTask_null_notTheSame() {
        assertFalse(new Todo("read book").isSameTask(null));
    }

    @Test
    public void isSameTask_doneAndNotDone_stillTheSame() {
        Todo done = new Todo("read book");
        done.markAsDone();
        assertTrue(done.isSameTask(new Todo("read book")));
    }
}
