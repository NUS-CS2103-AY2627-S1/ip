package piplupbot.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import piplupbot.PiplupBotException;

/**
 * Tests what {@link Deadline} adds to {@link Task}: a due date, shown one way on
 * screen and written another way to the save file.
 *
 * <p>The two renderings are deliberately different, and that difference is the
 * point of most of these cases. The screen gets {@code Oct 15 2019 06:00 PM},
 * written for a person; the file gets {@code 2019-10-15T18:00}, written to be
 * read back exactly. Were the file ever to hold the displayed form, changing
 * the wording on screen would silently make every saved file unreadable.</p>
 *
 * <p>The behavior todos and deadlines share is checked in {@link TodoTest}
 * rather than repeated here.</p>
 */
public class DeadlineTest {

    // ---------- What the user sees ----------

    @Test
    public void toString_newDeadline_showsDescriptionAndDueDate() throws PiplupBotException {
        assertEquals("[D][ ] return book (by: Oct 15 2019 06:00 PM)",
                new Deadline("return book", "2019-10-15 1800").toString());
    }

    @Test
    public void toString_doneDeadline_showsCrossInStatusBox() throws PiplupBotException {
        Deadline deadline = new Deadline("return book", "2019-10-15 1800");
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Oct 15 2019 06:00 PM)", deadline.toString());
    }

    /**
     * However the date was typed, it is shown in the one display format -- the
     * whole reason the date is stored as a {@code LocalDateTime} rather than as
     * the text the user wrote.
     */
    @Test
    public void toString_dateTypedInDifferentLayouts_showsTheSameText() throws PiplupBotException {
        String expected = "[D][ ] return book (by: Oct 15 2019 06:00 PM)";
        assertEquals(expected, new Deadline("return book", "2019-10-15 1800").toString());
        assertEquals(expected, new Deadline("return book", "15/10/2019 18:00").toString());
        assertEquals(expected, new Deadline("return book", "2019-10-15T18:00").toString());
    }

    /** A deadline given as a day alone falls at the start of that day. */
    @Test
    public void toString_dateWithoutTime_showsMidnight() throws PiplupBotException {
        assertEquals("[D][ ] pay fees (by: Dec 1 2019 12:00 AM)",
                new Deadline("pay fees", "1/12/2019").toString());
    }

    // ---------- What the save file holds ----------

    @Test
    public void toFileFields_newDeadline_returnsDueDateInIsoForm() throws PiplupBotException {
        assertArrayEquals(new String[] {"D", "0", "return book", "2019-10-15T18:00"},
                new Deadline("return book", "2019-10-15 1800").toFileFields());
    }

    @Test
    public void toFileFields_doneDeadline_returnsDoneFlagOfOne() throws PiplupBotException {
        Deadline deadline = new Deadline("return book", "2019-10-15 1800");
        deadline.markAsDone();
        assertArrayEquals(new String[] {"D", "1", "return book", "2019-10-15T18:00"},
                deadline.toFileFields());
    }

    // ---------- What a search matches ----------

    /**
     * A search reads the description only, so a deadline is not found by the
     * month or the year it falls in. The case is here rather than in
     * {@link TodoTest} because a todo has no date to be wrongly matched
     * against: only a deadline can show that the date shown on screen is not
     * part of what {@code find} looks at.
     */
    @Test
    public void descriptionContains_textFromTheDueDate_returnsFalse() throws PiplupBotException {
        Deadline deadline = new Deadline("return book", "2019-10-15 1800");
        assertTrue(deadline.descriptionContains("book"));
        assertFalse(deadline.descriptionContains("Oct"));
        assertFalse(deadline.descriptionContains("2019"));
    }

    // ---------- A date the bot cannot read ----------

    /**
     * The date is checked when the deadline is created, so a task that exists is
     * a task with a real date on it. Refusing here rather than later is what lets
     * every other method treat the date as certain.
     */
    @Test
    public void constructor_unreadableDate_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> new Deadline("return book", "next Friday"));
    }

    @Test
    public void constructor_impossibleDate_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> new Deadline("return book", "31/2/2019 1800"));
    }
}
