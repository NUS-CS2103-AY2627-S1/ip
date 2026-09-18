package bot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bot.exception.BotException;
import bot.parser.TaskDateTime;

class DeadlineTest {

    @Test
    void toString_dateOnly_showsByWithoutTime() throws BotException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    void toString_dateAndTime_showsByWithTime() throws BotException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15 1800"));
        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00PM)", deadline.toString());
    }

    @Test
    void toSaveFormat_roundTripsDateOnly() throws BotException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    void toSaveFormat_done_usesOneFlag() throws BotException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    void occursOn_matchingDate_returnsTrue() throws BotException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        assertTrue(deadline.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    void occursOn_differentDate_returnsFalse() throws BotException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 16)));
    }
}
