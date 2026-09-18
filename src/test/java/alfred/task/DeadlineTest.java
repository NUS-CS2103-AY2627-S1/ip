package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Deadline}, which falls on the single day it is due.
 */
public class DeadlineTest {
    private static final LocalDate OCT_14 = LocalDate.of(2019, 10, 14);

    private static final LocalDate OCT_15 = LocalDate.of(2019, 10, 15);

    private static final LocalDate OCT_16 = LocalDate.of(2019, 10, 16);

    @Test
    public void occursOn_dayItIsDue_true() {
        assertTrue(createDeadline().occursOn(OCT_15));
    }

    @Test
    public void occursOn_dayBefore_false() {
        assertFalse(createDeadline().occursOn(OCT_14));
    }

    @Test
    public void occursOn_dayAfter_false() {
        assertFalse(createDeadline().occursOn(OCT_16));
    }

    @Test
    public void matches_dueDate_false() {
        Deadline deadline = createDeadline();
        assertFalse(deadline.matches("2019-10-15"));
        assertFalse(deadline.matches("Oct"));
    }

    @Test
    public void matches_wordInDescription_true() {
        assertTrue(createDeadline().matches("book"));
    }

    @Test
    public void isSameTask_sameDescriptionAndDate_true() {
        assertTrue(createDeadline().isSameTask(new Deadline("return book", OCT_15)));
    }

    @Test
    public void isSameTask_eventWithSameDescription_false() {
        assertFalse(createDeadline().isSameTask(new Event("return book", OCT_15, OCT_16)));
    }

    @Test
    public void isSameTask_sameDescriptionDifferentDate_false() {
        assertFalse(createDeadline().isSameTask(new Deadline("return book", OCT_16)));
    }

    @Test
    public void toString_notDone_dueDateShownInReadersForm() {
        assertEquals("[D][ ] return book (by: Oct 15 2019)", createDeadline().toString());
    }

    @Test
    public void toString_done_statusBoxMarked() {
        Deadline deadline = createDeadline();
        deadline.markDone();
        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toFileFields_notDone_dueDateSavedInReadableBackForm() {
        assertEquals(List.of("D", "0", "return book", "2019-10-15"), createDeadline().toFileFields());
    }

    @Test
    public void toFileFields_done_statusIsOne() {
        Deadline deadline = createDeadline();
        deadline.markDone();
        assertEquals("1", deadline.toFileFields().get(1));
    }

    /**
     * Returns a deadline due on October 15, 2019.
     *
     * @return a fresh deadline, so that marking one test's copy cannot affect another.
     */
    private Deadline createDeadline() {
        return new Deadline("return book", OCT_15);
    }
}
