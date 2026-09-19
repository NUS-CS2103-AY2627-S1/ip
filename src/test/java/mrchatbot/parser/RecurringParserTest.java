package mrchatbot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import mrchatbot.command.AddCommand;
import mrchatbot.exception.MrChatbotException;
import mrchatbot.task.Recurrence;
import mrchatbot.task.RecurringTask;

public class RecurringParserTest {
    private final Parser parser = new Parser(Clock.fixed(
            Instant.parse("2026-09-20T16:00:00Z"), ZoneId.of("Asia/Singapore")));

    @Test
    public void createTask_daily_resetsTomorrowInLocalZone() throws Exception {
        RecurringTask task = (RecurringTask) parser.createTask("recurring exercise /every day");

        assertEquals(Recurrence.DAY, task.getRecurrence());
        assertEquals(LocalDate.of(2026, 9, 22), task.getNextResetDate());
        assertInstanceOf(AddCommand.class, parser.parseCommand("recurring exercise /every day"));
    }

    @Test
    public void createTask_weeklyWithoutWeekday_defaultsToCreationWeekday() throws Exception {
        RecurringTask task = (RecurringTask) parser.createTask("recurring meeting /every week");

        assertEquals(LocalDate.of(2026, 9, 28), task.getNextResetDate());
        assertEquals("[R][ ] meeting (every week on Monday; next reset: 2026-09-28)", task.toString());
    }

    @Test
    public void createTask_allWeekdaysAndMixedCase_usesNextOccurrence() throws Exception {
        for (DayOfWeek day : DayOfWeek.values()) {
            RecurringTask task = (RecurringTask) parser.createTask(
                    "RECURRING Project Meeting /EVERY WeEk /ON " + day.name().toLowerCase(Locale.ROOT));

            int daysUntilReset = day == DayOfWeek.MONDAY ? 7 : day.getValue() - 1;
            assertEquals(LocalDate.of(2026, 9, 21).plusDays(daysUntilReset), task.getNextResetDate());
            assertEquals("Project Meeting", task.getDescription());
        }
    }

    @Test
    public void createTask_invalidRecurrence_rejectsWithUserError() {
        String[] invalidInputs = {
            "recurring", "recurring   /every day", "recurring meeting", "recurring meeting /every",
            "recurring meeting /every month", "recurring meeting /every day /on monday",
            "recurring meeting /every week /on", "recurring meeting /every week /on someday",
            "recurring meeting /every week /on mon", "recurring meeting /every week /on monday extra",
            "recurring meeting /every week /on monday /on tuesday"
        };
        for (String input : invalidInputs) {
            assertThrows(MrChatbotException.class, () -> parser.createTask(input), input);
        }
    }
}
