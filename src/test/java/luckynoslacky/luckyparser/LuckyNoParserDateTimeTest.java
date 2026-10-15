package luckynoslacky.luckyparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import luckynoslacky.luckycommand.LuckyNoTaskCommand;
import luckynoslacky.luckyresponse.LuckyNoQuips;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckytask.EventTask;

/** Tests date and time interpretation used by command parsing. */
class LuckyNoParserDateTimeTest extends LuckyNoParserTestSupport {
    /** Verifies relative date phrases use the injected fixed date. */
    @Test
    void parseCommand_relativeDatePhrases_usesFixedCurrentDate() throws Exception {
        assertEquals(LocalDateTime.of(2026, 9, 15, 0, 0),
                parser.parseStartDateTime("the 15th").dateTime());
        assertEquals(LocalDateTime.of(2026, 8, 30, 23, 59),
                parser.parseEndDateTime("30th").dateTime());
        assertEquals(LocalDateTime.of(2027, 6, 6, 0, 0),
                parser.parseStartDateTime("June 6th").dateTime());
        assertEquals(LocalDateTime.of(2026, 8, 31, 0, 0),
                parser.parseStartDateTime("Monday").dateTime());
        assertEquals(LocalDateTime.of(2026, 8, 24, 0, 0),
                parser.parseStartDateTime("this Monday").dateTime());
        assertEquals(LocalDateTime.of(2026, 8, 30, 0, 0),
                parser.parseStartDateTime("this Sunday").dateTime());
        assertEquals(LocalDateTime.of(2026, 9, 2, 0, 0),
                parser.parseStartDateTime("next Wednesday").dateTime());
        assertEquals(LocalDateTime.of(2026, 9, 7, 0, 0),
                parser.parseStartDateTime("next next Monday").dateTime());
        assertEquals(LocalDateTime.of(2026, 9, 9, 0, 0),
                parser.parseStartDateTime("the following Wednesday").dateTime());
        assertEquals(LocalDateTime.of(2026, 8, 26, 0, 0),
                parser.parseStartDateTime("this coming Wednesday").dateTime());
        assertEquals(LocalDateTime.of(2026, 9, 1, 0, 0),
                parser.parseStartDateTime("the coming Tuesday").dateTime());
        assertEquals(LocalDateTime.of(2026, 9, 15, 0, 0),
                parser.parseStartDateTime("next 15th").dateTime());
        assertEquals(LocalDateTime.of(2026, 10, 15, 0, 0),
                parser.parseStartDateTime("next next 15th").dateTime());
        assertEquals(LocalDateTime.of(2026, 10, 15, 0, 0),
                parser.parseStartDateTime("the following 15th").dateTime());
        assertEquals(LocalDateTime.of(2026, 9, 1, 0, 0),
                parser.parseStartDateTime("next month").dateTime());
        assertEquals(LocalDateTime.of(2026, 10, 1, 0, 0),
                parser.parseStartDateTime("next next month").dateTime());
        assertEquals(LocalDateTime.of(2026, 10, 1, 0, 0),
                parser.parseStartDateTime("the following month").dateTime());
        assertEquals(LocalDateTime.of(2027, 1, 1, 0, 0),
                parser.parseStartDateTime("next year").dateTime());
        assertEquals(LocalDateTime.of(2028, 1, 1, 0, 0),
                parser.parseStartDateTime("the following year").dateTime());
        assertEquals(LocalDateTime.of(2028, 1, 1, 0, 0),
                parser.parseStartDateTime("next next year").dateTime());
    }

    /** Verifies time-only task arguments resolve to today or tomorrow. */
    @Test
    void parseCommand_timeOnlyValues_usesTodayOrTomorrow() throws Exception {
        assertEquals(LocalDateTime.of(2026, 8, 25, 14, 0),
                parser.parseStartDateTime("2pm").dateTime());
        assertEquals(LocalDateTime.of(2026, 8, 26, 9, 0),
                parser.parseStartDateTime("9am").dateTime());
    }

    /** Verifies all documented date/time formats are accepted. */
    @Test
    void parseCommand_documentedDateTimeFormats_returnsExpectedValues() throws Exception {
        LocalDate expectedDate = LocalDate.of(2030, 10, 15);
        for (String dateTimeText : new String[] {
            "2030-10-15", "2030/10/15", "15/10/2030", "15-10-2030",
            "15 Oct 2030", "15 October 2030", "Oct 15 2030",
            "October 15 2030", "Tue Oct 15 2030",
            "Tuesday, October 15 2030"}) {
            assertEquals(expectedDate.atStartOfDay(),
                parser.parseStartDateTime(dateTimeText).dateTime(), dateTimeText);
        }

        LocalDateTime expectedTime = LocalDateTime.of(2026, 8, 25, 14, 15, 30);
        for (String dateTimeText : new String[] {
            "14:15", "14:15:30", "2pm", "2 pm", "2:15pm",
            "2:15 pm", "2.15pm", "2.15 pm"}) {
            LocalDateTime expected = dateTimeText.contains("30")
                    ? expectedTime
                    : dateTimeText.contains(":15") || dateTimeText.contains(".15")
                    ? expectedTime.withSecond(0)
                    : expectedTime.withMinute(0).withSecond(0);
            assertEquals(expected,
                    parser.parseStartDateTime(dateTimeText).dateTime(), dateTimeText);
        }
    }

    /** Verifies date-only task arguments use start and end defaults. */
    @Test
    void parseCommand_dateOnlyValues_useStartAndEndDefaults() throws Exception {
        assertEquals(LocalDateTime.of(2026, 8, 25, 0, 0),
                parser.parseStartDateTime("2026-08-25").dateTime());
        assertEquals(LocalDateTime.of(2026, 8, 25, 23, 59),
                parser.parseEndDateTime("2026-08-25").dateTime());
    }

    /** Verifies invalid and past date/time arguments are rejected. */
    @Test
    void parseCommand_invalidOrPastDateTimes_throwsInputException() {
        assertInputError(LuckyNoQuips.invalidDateTimeMessage(),
                "deadline report /by definitely-not-a-date", 0);
        assertInputError(LuckyNoQuips.timeTravelMessage(),
                "deadline report /by 2026-08-25 09:00", 0);
        assertInputError(LuckyNoQuips.timeTravelMessage(),
                "deadline report /by 25 Aug 2025", 0);
        assertInputError(LuckyNoQuips.timeTravelMessage(),
                "event meeting /from 2026-08-06 16:00 /to 2026-08-06 14:00", 0);
    }

    /** Verifies past event starts are allowed when the end is later. */
    @Test
    void parseCommand_pastEventStartWithFutureEnd_succeeds() throws Exception {
        LuckyNoTaskCommand command = assertInstanceOf(
                LuckyNoTaskCommand.class,
                scanner.parseCommand("event past meeting /from 25 Aug 2025"
                        + " /to 26 Aug 2025", 0));

        assertEquals(
                LuckyNoTaskResponses.added(
                        new EventTask(
                                "past meeting",
                                LocalDateTime.of(2025, 8, 25, 0, 0),
                                LocalDateTime.of(2025, 8, 26, 23, 59)),
                        1),
                command.execute().content());
    }

    /** Verifies event end times use the start date or roll to the next date. */
    @Test
    void parseCommand_timeOnlyEventEnd_usesStartDateOrNextDate() throws Exception {
        LuckyNoTaskCommand sameDay = assertInstanceOf(
                LuckyNoTaskCommand.class,
                scanner.parseCommand("event afternoon meeting /from 25 Aug 2026 2pm"
                        + " /to 4pm", 0));
        LuckyNoTaskCommand overnight = assertInstanceOf(
                LuckyNoTaskCommand.class,
                scanner.parseCommand("event overnight meeting /from 25 Aug 2026 11pm"
                        + " /to 1am", 0));

        assertEquals(
                LuckyNoTaskResponses.added(
                        new EventTask(
                                "afternoon meeting",
                                LocalDateTime.of(2026, 8, 25, 14, 0),
                                LocalDateTime.of(2026, 8, 25, 16, 0)),
                        1),
                sameDay.execute().content());
        assertEquals(
                LuckyNoTaskResponses.added(
                        new EventTask(
                                "overnight meeting",
                                LocalDateTime.of(2026, 8, 25, 23, 0),
                                LocalDateTime.of(2026, 8, 26, 1, 0)),
                        2),
                overnight.execute().content());
    }
}
