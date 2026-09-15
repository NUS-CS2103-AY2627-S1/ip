package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskTest {

    private Deadline deadlineAt(int hour, int minute) {
        return new Deadline("return book", false, LocalDateTime.of(2019, 10, 15, hour, minute));
    }

    // ---------------------------------------------------------------- //
    // Display                                                           //
    // ---------------------------------------------------------------- //

    @Test
    public void toString_todo_showsTypeAndStatus() {
        assertEquals("[T][ ] borrow book", new ToDo("borrow book", false).toString());
        assertEquals("[T][X] borrow book", new ToDo("borrow book", true).toString());
    }

    @Test
    public void toString_deadlineWithTime_showsFormattedDate() {
        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00PM)", deadlineAt(18, 0).toString());
    }

    @Test
    public void toString_deadlineAtMidnight_omitsTime() {
        // A LocalDateTime always carries a time, so midnight is how "the user
        // gave a date only" is represented.
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadlineAt(0, 0).toString());
    }

    @Test
    public void toString_deadlineAtNoon_showsPm() {
        assertEquals("[D][ ] return book (by: Oct 15 2019, 12:00PM)", deadlineAt(12, 0).toString());
    }

    @Test
    public void toString_eventWithTimes_showsBothEnds() {
        Event event = new Event("meeting", false,
                LocalDateTime.of(2019, 10, 15, 14, 0),
                LocalDateTime.of(2019, 10, 15, 16, 0));

        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)",
                event.toString());
    }

    @Test
    public void toString_eventAtMidnight_omitsBothTimes() {
        Event event = new Event("trip", true,
                LocalDateTime.of(2019, 10, 15, 0, 0),
                LocalDateTime.of(2019, 10, 16, 0, 0));

        assertEquals("[E][X] trip (from: Oct 15 2019 to: Oct 16 2019)", event.toString());
    }

    // ---------------------------------------------------------------- //
    // Saved form                                                        //
    // ---------------------------------------------------------------- //

    @Test
    public void toDataString_todo_writesTypeFlagAndDescription() {
        assertEquals("T | 0 | borrow book", new ToDo("borrow book", false).toDataString());
        assertEquals("T | 1 | borrow book", new ToDo("borrow book", true).toDataString());
    }

    @Test
    public void toDataString_deadline_writesTheDateInIsoForm() {
        assertEquals("D | 0 | return book | 2019-10-15T18:00", deadlineAt(18, 0).toDataString());
        assertEquals("D | 0 | return book | 2019-10-15T00:00", deadlineAt(0, 0).toDataString());
    }

    @Test
    public void toDataString_event_writesBothDatesInIsoForm() {
        Event event = new Event("meeting", true,
                LocalDateTime.of(2019, 10, 15, 14, 0),
                LocalDateTime.of(2019, 10, 15, 16, 0));

        assertEquals("E | 1 | meeting | 2019-10-15T14:00 | 2019-10-15T16:00", event.toDataString());
    }

    @Test
    public void toDataString_everyType_roundTripsThroughTheParser() {
        // The saved letter has to match the type, or a reloaded event would
        // come back as a deadline and lose its end date.
        Task[] originals = {
            new ToDo("borrow book", true),
            new Deadline("return book", true, LocalDateTime.of(2019, 10, 15, 18, 0)),
            new Event("meeting", false,
                    LocalDateTime.of(2019, 10, 15, 14, 0),
                    LocalDateTime.of(2019, 10, 15, 16, 0)),
        };

        for (Task original : originals) {
            Task reloaded = Parser.parseDataLine(original.toDataString());

            assertEquals(original.toString(), reloaded.toString());
            assertEquals(original.toDataString(), reloaded.toDataString());
        }
    }

    // ---------------------------------------------------------------- //
    // Status                                                            //
    // ---------------------------------------------------------------- //

    @Test
    public void isDone_reflectsTheStatusGivenToTheConstructor() {
        assertFalse(new ToDo("read book", false).isDone());
        assertTrue(new ToDo("read book", true).isDone());
    }

    @Test
    public void mark_aTaskNotYetDone_setsItDone() {
        Task task = new ToDo("read book", false);
        task.mark();

        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void unmark_aTaskAlreadyDone_clearsIt() {
        Task task = new ToDo("read book", true);
        task.unmark();

        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void markThenUnmark_appliedTwice_isIdempotent() {
        Task task = new ToDo("read book", false);
        task.mark();
        task.mark();
        assertTrue(task.isDone());

        task.unmark();
        task.unmark();
        assertFalse(task.isDone());
    }

    @Test
    public void getTaskName_returnsTheDescriptionUnchanged() {
        assertEquals("read book", new ToDo("read book", false).getTaskName());
    }
}
