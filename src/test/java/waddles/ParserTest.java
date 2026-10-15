package waddles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void parseTodo_validDescription_returnsToDoWithDescription() throws WaddlesException {
        Task task = Parser.parseTodo("todo read book");

        assertTrue(task instanceof ToDo);
        assertEquals("read book", task.getDescription());
        assertEquals(false, task.isDone());
    }

    @Test
    public void parseTodo_emptyDescription_exceptionThrown() {
        assertThrows(WaddlesException.class, () -> Parser.parseTodo("todo"));
        assertThrows(WaddlesException.class, () -> Parser.parseTodo("todo   "));
    }

    @Test
    public void parseDeadline_validInput_returnsDeadlineWithCorrectFields() throws WaddlesException {
        Task task = Parser.parseDeadline("deadline return book /by 2019-12-01");

        assertTrue(task instanceof Deadline);
        assertEquals("return book", task.getDescription());

        Deadline deadline = (Deadline) task;
        assertEquals("2019-12-01", deadline.getBy());
    }

    @Test
    public void parseDeadline_missingByKeyword_exceptionThrown() {
        assertThrows(WaddlesException.class, () -> Parser.parseDeadline("deadline return book"));
    }

    @Test
    public void parseDeadline_emptyDescriptionOrDate_exceptionThrown() {
        assertThrows(WaddlesException.class, () -> Parser.parseDeadline("deadline /by 2019-12-01"));
        assertThrows(WaddlesException.class, () -> Parser.parseDeadline("deadline return book /by "));
    }

    @Test
    public void parseDeadline_dateNotOnCalendar_exceptionThrown() {
        // Month 56 and day 45 don't exist on any calendar, so this must be
        // rejected rather than silently stored as raw text.
        assertThrows(WaddlesException.class,
                () -> Parser.parseDeadline("deadline submit ths /by 2029-56/45"));
    }

    @Test
    public void parseDeadline_slashSeparatedDate_exceptionThrown() {
        // Only yyyy-mm-dd (dash-separated) is accepted; yyyy/mm/dd must be
        // rejected instead of falling back to raw, unparsed text.
        assertThrows(WaddlesException.class,
                () -> Parser.parseDeadline("deadline testing afain /by 2029/09/09"));
    }

    @Test
    public void parseDeadline_validYyyyMmDdDate_stillAccepted() throws WaddlesException {
        Task task = Parser.parseDeadline("deadline do this /by 2027-09-09");

        assertTrue(task instanceof Deadline);
        assertEquals("2027-09-09", ((Deadline) task).getBy());
    }

    @Test
    public void parseSchedule_validDate_returnsThatDate() throws WaddlesException {
        LocalDate date = Parser.parseSchedule("schedule 2024-12-01");
        assertEquals(LocalDate.of(2024, 12, 1), date);
    }

    @Test
    public void parseSchedule_today_returnsCurrentDate() throws WaddlesException {
        assertEquals(LocalDate.now(), Parser.parseSchedule("schedule today"));
    }

    @Test
    public void parseSchedule_missingDate_exceptionThrown() {
        assertThrows(WaddlesException.class, () -> Parser.parseSchedule("schedule"));
        assertThrows(WaddlesException.class, () -> Parser.parseSchedule("schedule   "));
    }

    @Test
    public void parseSchedule_invalidDateFormat_exceptionThrown() {
        assertThrows(WaddlesException.class, () -> Parser.parseSchedule("schedule next monday"));
    }

    @Test
    public void parseDeadline_withEveryWeek_returnsRecurringDeadline() throws WaddlesException {
        Task task = Parser.parseDeadline("deadline pay rent /by 2024-01-01 /every week");

        assertTrue(task instanceof Deadline);
        assertTrue(task.isRecurring());
        assertEquals(Recurrence.WEEKLY, ((Deadline) task).getRecurrence());
    }

    @Test
    public void parseDeadline_recurringWithoutStructuredDate_exceptionThrown() {
        assertThrows(WaddlesException.class,
                () -> Parser.parseDeadline("deadline pay rent /by Sunday /every week"));
    }

    @Test
    public void parseDeadline_unknownRecurrenceKeyword_exceptionThrown() {
        assertThrows(WaddlesException.class,
                () -> Parser.parseDeadline("deadline pay rent /by 2024-01-01 /every fortnight"));
    }

    @Test
    public void parseEvent_withEveryDay_returnsRecurringEvent() throws WaddlesException {
        Task task = Parser.parseEvent("event standup /from 2024-01-01 /to 2024-01-01 /every day");

        assertTrue(task instanceof Event);
        assertTrue(task.isRecurring());
        assertEquals(Recurrence.DAILY, ((Event) task).getRecurrence());
    }

    @Test
    public void parseEvent_slashSeparatedDate_exceptionThrown() {
        assertThrows(WaddlesException.class,
                () -> Parser.parseEvent("event standup /from 2024/01/01 /to 2024-01-02"));
    }

    @Test
    public void parseEvent_validDateWithTime_accepted() throws WaddlesException {
        // Event's /from and /to must accept an optional HHmm time, unlike
        // Deadline's date-only /by.
        Task task = Parser.parseEvent("event smth /from 2026-09-09 1800 /to 2026-09-09 1900");

        assertTrue(task instanceof Event);
        Event event = (Event) task;
        assertEquals("2026-09-09 1800", event.getFrom());
        assertEquals("2026-09-09 1900", event.getTo());
    }

    @Test
    public void parseEvent_validDateWithoutTime_stillAccepted() throws WaddlesException {
        // The HHmm time component is optional.
        Task task = Parser.parseEvent("event smth /from 2026-09-09 /to 2026-09-10");

        assertTrue(task instanceof Event);
    }

    @Test
    public void parseEvent_slashSeparatedDateWithTime_exceptionThrown() {
        assertThrows(WaddlesException.class,
                () -> Parser.parseEvent("event smth /from 2026/09/09 1800 /to 2026/09/09 1900"));
    }

    @Test
    public void parseEvent_dateWithTimeNotOnCalendar_exceptionThrown() {
        assertThrows(WaddlesException.class,
                () -> Parser.parseEvent("event smth /from 2026-99-99 1800 /to 2026-09-09 1900"));
    }

    @Test
    public void parseEvent_toBeforeFrom_exceptionThrown() {
        assertThrows(WaddlesException.class,
                () -> Parser.parseEvent("event X /from 2026-09-09 1800 /to 2026-09-09 1700"));
    }

    @Test
    public void parseEvent_toEqualsFrom_accepted() throws WaddlesException {
        // Ending exactly when it begins is allowed; only ending strictly
        // before the start is rejected.
        Task task = Parser.parseEvent("event X /from 2026-09-09 1800 /to 2026-09-09 1800");

        assertTrue(task instanceof Event);
    }

    @Test
    public void parseEvent_toAfterFrom_stillAccepted() throws WaddlesException {
        Task task = Parser.parseEvent("event X /from 2026-09-09 1800 /to 2026-09-09 1900");

        assertTrue(task instanceof Event);
    }

    @Test
    public void parseEvent_toEarlierYearThanFrom_exceptionThrownDespiteLaterTimeOfDay() {
        // 2026-09-09 1900 is chronologically before 2027-09-09 1800, even
        // though 1900 > 1800 when only the time-of-day is compared. The
        // comparison must use the full date-time, not just the time.
        assertThrows(WaddlesException.class,
                () -> Parser.parseEvent("event X /from 2027-09-09 1800 /to 2026-09-09 1900"));
    }

    @Test
    public void parseEvent_toLaterYearThanFrom_acceptedDespiteEarlierTimeOfDay() throws WaddlesException {
        // 2027-09-09 1700 is chronologically after 2026-09-09 1800, even
        // though 1700 < 1800 when only the time-of-day is compared.
        Task task = Parser.parseEvent("event Y /from 2026-09-09 1800 /to 2027-09-09 1700");

        assertTrue(task instanceof Event);
    }

    @Test
    public void parseEvent_recurringWithoutStructuredDates_exceptionThrown() {
        assertThrows(WaddlesException.class,
                () -> Parser.parseEvent("event standup /from Mon /to Tue /every day"));
    }

    @Test
    public void parseEvent_recurringWithTime_accepted() throws WaddlesException {
        // Confirmed bug: a recurring event with a time was wrongly rejected
        // even though the exact same /from and /to are valid for a
        // non-recurring event. Recurring events must accept time too.
        Task task = Parser.parseEvent(
                "event Weekly sync /from 2026-09-19 1000 /to 2026-09-19 1100 /every week");

        assertTrue(task instanceof Event);
        assertTrue(task.isRecurring());
        Event event = (Event) task;
        assertEquals("2026-09-19 1000", event.getFrom());
        assertEquals("2026-09-19 1100", event.getTo());
        assertTrue(event.hasStructuredDate());
    }

    @Test
    public void parseEvent_recurringWithBareDate_stillAccepted() throws WaddlesException {
        // Bare-date recurring events (no time) must keep working exactly
        // as before this fix.
        Task task = Parser.parseEvent("event Weekly sync /from 2026-09-19 /to 2026-09-19 /every week");

        assertTrue(task instanceof Event);
        assertTrue(task.isRecurring());
    }

    @Test
    public void parseEvent_recurringWithWrongSeparatorAndTime_exceptionThrown() {
        // Consistency, not looser validation: a wrong date separator must
        // still be rejected for recurring events with a time.
        assertThrows(WaddlesException.class,
                () -> Parser.parseEvent(
                        "event Weekly sync /from 2026/09/19 1000 /to 2026-09-19 1100 /every week"));
    }

    @Test
    public void parseEvent_recurringWithInvalidCalendarDateAndTime_exceptionThrown() {
        assertThrows(WaddlesException.class,
                () -> Parser.parseEvent(
                        "event Weekly sync /from 2026-99-19 1000 /to 2026-09-19 1100 /every week"));
    }

    @Test
    public void parseEvent_nonRecurringWithTime_unaffectedByRecurrenceFix() throws WaddlesException {
        // A non-recurring event with a time must display the raw text
        // exactly as before — only recurring events get a backfilled,
        // structured (date-only) fromDate/toDate.
        Task task = Parser.parseEvent("event smth /from 2026-09-09 1800 /to 2026-09-09 1900");

        assertTrue(task instanceof Event);
        Event event = (Event) task;
        assertEquals(false, event.hasStructuredDate());
        assertEquals("[E][ ] smth (from: 2026-09-09 1800 to: 2026-09-09 1900)", event.toString());
    }

    @Test
    public void parseDeadline_recurringWithTime_exceptionThrown() {
        // Unlike events, deadlines are intentionally date-only (no /every
        // needed to trigger this — a time is rejected either way).
        assertThrows(WaddlesException.class,
                () -> Parser.parseDeadline("deadline pay rent /by 2026-09-19 1000 /every week"));
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroIndexedValue() throws WaddlesException {
        int index = Parser.parseTaskIndex("mark 2", "mark", 5);
        assertEquals(1, index);
    }

    @Test
    public void parseTaskIndex_outOfRange_exceptionThrown() {
        assertThrows(WaddlesException.class, () -> Parser.parseTaskIndex("mark 99", "mark", 5));
    }

    @Test
    public void parseTaskIndex_notANumber_exceptionThrown() {
        assertThrows(WaddlesException.class, () -> Parser.parseTaskIndex("mark abc", "mark", 5));
    }
}