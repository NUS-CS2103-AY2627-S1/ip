package nori.command;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nori.testutil.IsolatedSessionTest;

/**
 * Tests the day a schedule lays out, section by section and in order.
 */
public class ScheduleCommandTest extends IsolatedSessionTest {
    @Test
    public void execute_dayWithEveryKindOfTask_laysTheDayOut() {
        String response = runCommands(
                "todo read book",
                "deadline return book /by 2019-06-06",
                "event exam /from 2019-06-06 0900 /to 2019-06-06 1200",
                "event conference /from 2019-06-06 /to 2019-06-08",
                "event book fair /from 2019-06-06 1000 /to 1800",
                "schedule 2019-06-06");

        assertEquals("Noot noot! Schedule for 2019-06-06:\n"
                + "09:00 3.[E][ ] exam (from: 2019-06-06 0900 to: 2019-06-06 1200)\n"
                + "10:00 5.[E][ ] book fair (from: 2019-06-06 1000 to: 1800)\n"
                + "All day:\n"
                + "      4.[E][ ] conference (from: 2019-06-06 to: 2019-06-08)\n"
                + "Due:\n"
                + "      2.[D][ ] return book (by: Jun 06 2019)\n"
                + "Anytime:\n"
                + "      1.[T][ ] read book", response);
    }

    @Test
    public void execute_eventsAddedOutOfOrder_ordersThemByStartTime() {
        String response = runCommands(
                "event late talk /from 2019-06-06 1600 /to 1700",
                "event early run /from 2019-06-06 0630 /to 0730",
                "schedule 2019-06-06");

        assertEquals("Noot noot! Schedule for 2019-06-06:\n"
                + "06:30 2.[E][ ] early run (from: 2019-06-06 0630 to: 0730)\n"
                + "16:00 1.[E][ ] late talk (from: 2019-06-06 1600 to: 1700)", response);
    }

    @Test
    public void execute_twelveHourStartTimes_orderThemAcrossNoon() {
        String response = runCommands(
                "event lunch /from 2019-06-06 12pm /to 1pm",
                "event standup /from 2019-06-06 9am /to 9:30am",
                "schedule 2019-06-06");

        assertEquals("Noot noot! Schedule for 2019-06-06:\n"
                + "09:00 2.[E][ ] standup (from: 2019-06-06 9am to: 9:30am)\n"
                + "12:00 1.[E][ ] lunch (from: 2019-06-06 12pm to: 1pm)", response);
    }

    @Test
    public void execute_eventsAtTheSameTime_keepTaskListOrder() {
        String response = runCommands(
                "event first /from 2019-06-06 0900 /to 1000",
                "event second /from 2019-06-06 0900 /to 1000",
                "schedule 2019-06-06");

        assertEquals("Noot noot! Schedule for 2019-06-06:\n"
                + "09:00 1.[E][ ] first (from: 2019-06-06 0900 to: 1000)\n"
                + "09:00 2.[E][ ] second (from: 2019-06-06 0900 to: 1000)", response);
    }

    @Test
    public void execute_firstDayOfAMultiDayEvent_showsItsStartTime() {
        String response = runCommands(
                "event conference /from 2019-06-05 0900 /to 2019-06-08 1700",
                "schedule 2019-06-05");

        assertEquals("Noot noot! Schedule for 2019-06-05:\n"
                + "09:00 1.[E][ ] conference (from: 2019-06-05 0900 to: 2019-06-08 1700)", response);
    }

    @Test
    public void execute_middleDayOfAMultiDayEvent_showsItAsAllDay() {
        String response = runCommands(
                "event conference /from 2019-06-05 0900 /to 2019-06-08 1700",
                "schedule 2019-06-06");

        assertEquals("Noot noot! Schedule for 2019-06-06:\n"
                + "All day:\n"
                + "      1.[E][ ] conference (from: 2019-06-05 0900 to: 2019-06-08 1700)", response);
    }

    @Test
    public void execute_undatedEvent_staysOffEverySchedule() {
        String response = runCommands(
                "event team meeting /from Mon 2pm /to 4pm",
                "schedule 2019-06-06");

        assertEquals("Nothing on the schedule for 2019-06-06. The ice is quiet.", response);
    }

    @Test
    public void execute_dayWithNothingOnIt_reportsQuietIce() {
        String response = runCommands(
                "deadline return book /by 2019-06-06",
                "schedule 2019-07-01");

        assertEquals("Nothing on the schedule for 2019-07-01. The ice is quiet.", response);
    }

    @Test
    public void execute_noDate_laysOutToday() {
        String response = runCommands("todo read book", "schedule");

        assertEquals("Noot noot! Schedule for " + LocalDate.now() + ":\n"
                + "Anytime:\n"
                + "      1.[T][ ] read book", response);
    }

    @Test
    public void execute_unreadableDate_reportsTheDateError() {
        String response = runCommands("schedule tomorrow");

        assertEquals("NOOT?! I cannot understand \"tomorrow\" as a date."
                + " Use a date like \"2019-10-15\".", response);
    }

    @Test
    public void execute_numberReadOffASchedule_marksThatTask() {
        String response = runCommands(
                "todo read book",
                "event exam /from 2019-06-06 0900 /to 1200",
                "schedule 2019-06-06",
                "mark 2");

        assertEquals("Noot noot! This task is now ice-cold complete:\n"
                + "  [E][X] exam (from: 2019-06-06 0900 to: 1200)", response);
    }

    @Test
    public void execute_doneTasks_stayOnTheSchedule() {
        String response = runCommands(
                "event exam /from 2019-06-06 0900 /to 1200",
                "mark 1",
                "schedule 2019-06-06");

        assertEquals("Noot noot! Schedule for 2019-06-06:\n"
                + "09:00 1.[E][X] exam (from: 2019-06-06 0900 to: 1200)", response);
    }

}
