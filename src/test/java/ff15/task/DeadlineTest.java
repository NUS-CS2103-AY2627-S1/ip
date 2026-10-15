package ff15.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ff15.FF15Exception;

/**
 * Tests how a {@link Deadline} displays itself, saves itself, and answers whether
 * it falls inside a queried span of dates.
 */
public class DeadlineTest {

    private static Deadline deadline(String description, String by) throws FF15Exception {
        return new Deadline(description, TaskTime.parse(by));
    }

    @Test
    public void toString_dateOnly_showsDateWithoutATime() throws FF15Exception {
        assertEquals("[D][ ] return book (by: Dec 02 2019)",
                deadline("return book", "2019-12-02").toString());
    }

    @Test
    public void toString_dateAndTime_showsBoth() throws FF15Exception {
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)",
                deadline("return book", "2019-12-02 1800").toString());
    }

    @Test
    public void toString_doneDeadline_showsCrossInStatusBox() throws FF15Exception {
        Deadline task = deadline("return book", "2019-12-02");
        task.markAsDone();
        assertEquals("[D][X] return book (by: Dec 02 2019)", task.toString());
    }

    @Test
    public void toFileFormat_dateOnly_writesDateInInputForm() throws FF15Exception {
        assertEquals("D | 0 | return book | 2019-12-02",
                deadline("return book", "2019-12-02").toFileFormat());
    }

    @Test
    public void toFileFormat_dateAndTime_keepsTheTime() throws FF15Exception {
        Deadline task = deadline("return book", "2019-12-02 1800");
        task.markAsDone();
        assertEquals("D | 1 | return book | 2019-12-02 1800", task.toFileFormat());
    }

    @Test
    public void occursIn_rangeIsTheDueDay_returnsTrue() throws FF15Exception {
        assertTrue(deadline("return book", "2019-12-02 1800").occursIn(DateRange.parse("2019-12-02")));
    }

    @Test
    public void occursIn_rangeIsAnotherDay_returnsFalse() throws FF15Exception {
        Deadline task = deadline("return book", "2019-12-02");
        assertFalse(task.occursIn(DateRange.parse("2019-12-01")));
        assertFalse(task.occursIn(DateRange.parse("2019-12-03")));
    }

    @Test
    public void occursIn_rangeIsTheDueMonthOrYear_returnsTrue() throws FF15Exception {
        Deadline task = deadline("return book", "2019-12-02");
        assertTrue(task.occursIn(DateRange.parse("2019-12")));
        assertTrue(task.occursIn(DateRange.parse("2019")));
    }

    @Test
    public void occursIn_rangeIsAnotherMonthOrYear_returnsFalse() throws FF15Exception {
        Deadline task = deadline("return book", "2019-12-02");
        assertFalse(task.occursIn(DateRange.parse("2019-11")));
        assertFalse(task.occursIn(DateRange.parse("2020")));
    }

    @Test
    public void occursIn_timeOfDayIgnored_matchesOnTheDayAlone() throws FF15Exception {
        assertTrue(deadline("return book", "2019-12-02 0000").occursIn(DateRange.parse("2019-12-02")));
        assertTrue(deadline("return book", "2019-12-02 2359").occursIn(DateRange.parse("2019-12-02")));
    }

    @Test
    public void isSameAs_sameDescriptionAndDate_isTrue() throws FF15Exception {
        Deadline a = new Deadline("return book", TaskTime.parse("2019-12-02 1800"));
        Deadline b = new Deadline("return book", TaskTime.parse("2019-12-02 1800"));
        assertTrue(a.isSameAs(b));
    }

    @Test
    public void isSameAs_sameDescriptionDifferentDate_isFalse() throws FF15Exception {
        Deadline a = new Deadline("return book", TaskTime.parse("2019-12-02"));
        Deadline b = new Deadline("return book", TaskTime.parse("2019-12-03"));
        assertFalse(a.isSameAs(b));
    }

    @Test
    public void isSameAs_dateOnlyVersusMidnight_isFalse() throws FF15Exception {
        // They print differently, so the user would see two distinct tasks.
        Deadline a = new Deadline("return book", TaskTime.parse("2019-12-02"));
        Deadline b = new Deadline("return book", TaskTime.parse("2019-12-02 0000"));
        assertFalse(a.isSameAs(b));
    }

    @Test
    public void isSameAs_differentDescriptionSameDate_isFalse() throws FF15Exception {
        Deadline a = new Deadline("return book", TaskTime.parse("2019-12-02"));
        Deadline b = new Deadline("return the book", TaskTime.parse("2019-12-02"));
        assertFalse(a.isSameAs(b));
    }
}
