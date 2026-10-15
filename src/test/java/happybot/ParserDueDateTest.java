package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class ParserDueDateTest {
    private static final String DUE_USAGE = "Use: due <date>, such as due 2019-12-02.";

    private static final String DATE_FORMAT_HINT =
            "Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that "
                    + "defaults to 0000, such as 2019-12-02 1800.";

    private static final String NO_SUCH_DATE_HINT =
            "There is no such date on the calendar. Please enter a valid date, checking the "
                    + "number of days the month has.";

    @Test
    public void parseDueDate_isoDate_dateReturned() throws HappyBotException {
        assertEquals(LocalDate.of(2019, 12, 2), Parser.parseDueDate("2019-12-02"));
    }

    @Test
    public void parseDueDate_slashDate_dateReturned() throws HappyBotException {
        assertEquals(LocalDate.of(2019, 12, 2), Parser.parseDueDate("2/12/2019"));
    }

    @Test
    public void parseDueDate_dateWithTime_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class,
                () -> Parser.parseDueDate("2019-12-02 1800"));

        assertEquals(DUE_USAGE, e.getMessage());
    }

    @Test
    public void parseDueDate_emptyBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDueDate(""));

        assertEquals(DUE_USAGE, e.getMessage());
    }

    @Test
    public void parseDueDate_spacesOnly_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDueDate("   "));

        assertEquals(DUE_USAGE, e.getMessage());
    }

    @Test
    public void parseDueDate_nullBody_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDueDate(null));

        assertEquals(DUE_USAGE, e.getMessage());
    }

    @Test
    public void parseDueDate_dayPastEndOfMonth_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDueDate("2019-11-31"));

        assertEquals(NO_SUCH_DATE_HINT, e.getMessage());
    }

    @Test
    public void parseDueDate_unreadableDate_exceptionThrown() {
        HappyBotException e = assertThrows(HappyBotException.class, () -> Parser.parseDueDate("tomorrow"));

        assertEquals(DATE_FORMAT_HINT, e.getMessage());
    }
}
