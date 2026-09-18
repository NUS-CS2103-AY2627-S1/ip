package chausistant.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import chausistant.storage.Storage;
import chausistant.task.DeadlineTask;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/** Tests the user-facing reminder output using a fixed clock. */
class ReminderCommandTest {

    @Test
    void execute_upcomingDeadlines_displaysStandardDeadlineFormatInDueTimeOrder() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-01T10:00:00Z"), ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.now(clock);
        DeadlineTask laterDeadline = new DeadlineTask("later", now.plusDays(2), true);
        DeadlineTask dateOnlyDeadline = new DeadlineTask("submit form", now.plusDays(1)
                .toLocalDate().atTime(23, 59), false);
        DeadlineTask completedDeadline = new DeadlineTask("completed", now.plusHours(1), true);
        completedDeadline.setCompleted(true);
        TaskList tasks = new TaskList(laterDeadline, completedDeadline, dateOnlyDeadline,
                new DeadlineTask("overdue", now.minusMinutes(1), true),
                new DeadlineTask("outside window", now.plusDays(7).plusMinutes(1), true));
        StringBuilder response = new StringBuilder();

        new ReminderCommand(clock).execute(tasks, new Ui(response), new Storage(Path.of("unused.txt")));

        assertEquals(String.join(System.lineSeparator(),
                "peek-a-boo! here are your upcoming deadlines:",
                "[D][ ] submit form (by: Jun 2 2026)",
                "[D][ ] later (by: Jun 3 2026 1000)") + System.lineSeparator(), response.toString());
    }

    @Test
    void execute_noUpcomingDeadlines_displaysEmptyReminderMessage() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-01T10:00:00Z"), ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.now(clock);
        TaskList tasks = new TaskList(
                new DeadlineTask("overdue", now.minusMinutes(1), true),
                new DeadlineTask("outside window", now.plusDays(7).plusMinutes(1), true));
        StringBuilder response = new StringBuilder();

        new ReminderCommand(clock).execute(tasks, new Ui(response), new Storage(Path.of("unused.txt")));

        assertEquals(String.join(System.lineSeparator(),
                "peek-a-boo! here are your upcoming deadlines:",
                "no upcoming deadlines in the next 7 days -- you're all caught up!")
                + System.lineSeparator(),
                response.toString());
    }
}
