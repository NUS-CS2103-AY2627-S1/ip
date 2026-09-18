package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void snooze_positiveDays_pushesDateBack() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.snooze(3);
        assertEquals(LocalDateTime.of(2019, 12, 5, 18, 0), deadline.getBy());
    }

    @Test
    public void getTypeName_deadline_returnsDeadline() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("Deadline", deadline.getTypeName());
    }

    @Test
    public void getScheduleSummary_deadline_describesDueDate() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("by Dec 2 2019, 6:00 PM", deadline.getScheduleSummary());
    }

    @Test
    public void getScheduleSummary_afterSnooze_reflectsNewDate() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.snooze(3);
        assertEquals("by Dec 5 2019, 6:00 PM", deadline.getScheduleSummary());
    }

    @Test
    public void toString_notDone_showsTypeAndDueDate() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)", deadline.toString());
    }

    @Test
    public void toFileFormat_notDone_usesIsoDateAndZeroFlag() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("D | 0 | return book | 2019-12-02T18:00", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_markedDone_recordsTheDoneFlag() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-12-02T18:00", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_afterSnooze_savesTheNewDate() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.snooze(3);
        assertEquals("D | 0 | return book | 2019-12-05T18:00", deadline.toFileFormat());
    }

    @Test
    public void snooze_calledTwice_accumulatesOffset() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.snooze(2);
        deadline.snooze(5);
        assertEquals(LocalDateTime.of(2019, 12, 9, 18, 0), deadline.getBy());
    }
}
