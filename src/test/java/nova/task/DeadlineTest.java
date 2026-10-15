package nova.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    private static final LocalDate DUE_DATE = LocalDate.of(2026, 8, 24);
    private final Deadline deadline = new Deadline("submit report", DUE_DATE);

    @Test
    void isDueOn_sameDate_returnsTrue() {
        assertTrue(deadline.isDueOn(DUE_DATE));
    }

    @Test
    void isDueOn_earlierDate_returnsFalse() {
        assertFalse(deadline.isDueOn(DUE_DATE.minusDays(1)));
    }

    @Test
    void isDueOn_laterDate_returnsFalse() {
        assertFalse(deadline.isDueOn(DUE_DATE.plusDays(1)));
    }

    @Test
    void toDataString_incompleteDeadline_returnsEncodedRecord() {
        assertEquals("V2 | D | 0 | c3VibWl0IHJlcG9ydA== | MjAyNi0wOC0yNA==",
                deadline.toDataString());
    }

    @Test
    void toDataString_completedDeadline_returnsEncodedRecordWithCompletedStatus() {
        deadline.markAsDone();

        assertEquals("V2 | D | 1 | c3VibWl0IHJlcG9ydA== | MjAyNi0wOC0yNA==",
                deadline.toDataString());
    }

    @Test
    void toDataString_descriptionWithDelimiterAndUnicode_returnsSafelyEncodedRecord() {
        Deadline deadlineWithSpecialCharacters = new Deadline("read | café", DUE_DATE);

        assertEquals("V2 | D | 0 | cmVhZCB8IGNhZsOp | MjAyNi0wOC0yNA==",
                deadlineWithSpecialCharacters.toDataString());
    }

    @Test
    void toString_incompleteDeadline_returnsFormattedDisplayString() {
        assertEquals("[D][ ] submit report (by: Aug 24 2026)", deadline.toString());
    }

    @Test
    void toString_completedDeadline_returnsFormattedDisplayStringWithCompletedStatus() {
        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Aug 24 2026)", deadline.toString());
    }
}
