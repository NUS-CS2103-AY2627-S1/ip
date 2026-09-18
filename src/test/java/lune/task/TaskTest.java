package lune.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

/**
 * Tests Task's own behavior directly: construction, status
 * getters/setters, the base toString()/toSaveFormat(), the default
 * occursOn() (false, since a bare Task has no date), and the shared
 * date-formatting helpers subclasses use. Subclass-specific overrides
 * (Todo/Deadline/Event's own toString()/toSaveFormat()/occursOn()) are
 * tested in their own test classes.
 */
public class TaskTest {

    // Mirrors Task's private DISPLAY_DATE_TIME_FORMAT. Built independently
    // here (rather than hardcoding "6:00 pm") so this test doesn't depend
    // on the JVM's default locale for AM/PM casing.
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    @Test
    public void constructor_newTask_notDoneByDefault() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getDescription_returnsConstructorValue() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void getStatusIcon_notDone_blankSpace() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_done_capitalX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsDone_notDoneTask_becomesDone() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsUndone_doneTask_becomesNotDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsUndone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toString_notDoneTask_bracketedBlankStatusShown() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_doneTask_bracketedXShown() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toSaveFormat_notDoneTask_pipeSeparatedLineWithZeroFlag() {
        Task task = new Task("read book");
        assertEquals("0 | read book", task.toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneTask_pipeSeparatedLineWithOneFlag() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("1 | read book", task.toSaveFormat());
    }

    @Test
    public void occursOn_bareTask_alwaysFalse() {
        // A plain Task has no date at all — occursOn is only meaningful for
        // Deadline/Event, which override it. Todo deliberately doesn't
        // override it either, so it also returns false via this same path.
        Task task = new Task("read book");
        assertFalse(task.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void formatDate_returnsMonthDayYearFormat() {
        assertEquals("Oct 15 2019", Task.formatDate(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void formatDateTime_midnightTime_dateOnlyDisplayed() {
        assertEquals("Oct 15 2019", Task.formatDateTime(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    public void formatDateTime_nonMidnightTime_dateAndTimeDisplayed() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 15, 18, 0);
        assertEquals(dateTime.format(DATE_TIME_FORMAT), Task.formatDateTime(dateTime));
    }
}
