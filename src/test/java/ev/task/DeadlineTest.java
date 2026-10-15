package ev.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ev.EvException;

public class DeadlineTest {

    private static final LocalDateTime DEC_2_6PM = LocalDateTime.of(2019, 12, 2, 18, 0);

    @Test
    public void toString_withTime_timeShown() throws EvException {
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)",
                new Deadline("return book", DEC_2_6PM).toString());
    }

    @Test
    public void toString_midnight_onlyDateShown() throws EvException {
        assertEquals("[D][ ] return book (by: Dec 2 2019)",
                new Deadline("return book", LocalDateTime.of(2019, 12, 2, 0, 0)).toString());
    }

    @Test
    public void toFileFormat_withTime_isoDateStored() throws EvException {
        assertEquals("D | 0 | return book | 2019-12-02T18:00",
                new Deadline("return book", DEC_2_6PM).toFileFormat());
    }

    @Test
    public void toFileFormat_doneDeadline_doneFlag() throws EvException {
        Deadline deadline = new Deadline("return book", DEC_2_6PM);
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-12-02T18:00", deadline.toFileFormat());
    }

    @Test
    public void occursOn_sameDayDifferentTime_true() throws EvException {
        assertTrue(new Deadline("return book", DEC_2_6PM).occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_dayBeforeOrAfter_false() throws EvException {
        Deadline deadline = new Deadline("return book", DEC_2_6PM);
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 1)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void applyUpdate_by_onlyTheDueTimeChanges() throws EvException {
        Deadline deadline = new Deadline("return book", DEC_2_6PM);
        deadline.markAsDone();
        deadline.applyUpdate(Deadline.OPTION_BY, "2019-12-05 1800");
        assertEquals("[D][X] return book (by: Dec 5 2019, 6:00 PM)", deadline.toString());
    }

    @Test
    public void applyUpdate_description_dueTimeKept() throws EvException {
        Deadline deadline = new Deadline("return book", DEC_2_6PM);
        deadline.applyUpdate(Task.OPTION_DESC, "return the book");
        assertEquals("[D][ ] return the book (by: Dec 2 2019, 6:00 PM)", deadline.toString());
    }

    @Test
    public void applyUpdate_unreadableDate_rejectedAndUnchanged() throws EvException {
        Deadline deadline = new Deadline("return book", DEC_2_6PM);
        assertThrows(EvException.class, () -> deadline.applyUpdate(Deadline.OPTION_BY, "tomorrow"));
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)", deadline.toString());
    }

    @Test
    public void applyUpdate_optionThisTypeLacks_messageListsWhatItHas() throws EvException {
        Deadline deadline = new Deadline("return book", DEC_2_6PM);
        EvException thrown = assertThrows(EvException.class, () ->
                deadline.applyUpdate(Event.OPTION_FROM, "2019-12-05"));
        assertTrue(thrown.getMessage().contains("a deadline"));
        assertTrue(thrown.getMessage().contains("/desc, /by"));
    }
}
