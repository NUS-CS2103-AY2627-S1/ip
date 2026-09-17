package yuki.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuki.storage.Storage;
import yuki.task.Deadline;
import yuki.task.TaskList;
import yuki.task.ToDo;
import yuki.time.TaskDateTime;
import yuki.ui.Ui;

/** Tests reminder queries using a fixed clock. */
class RemindersCommandTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-09T01:00:00Z"), ZoneOffset.ofHours(8));

    @Test
    void execute_defaultClockWithEmptyList_emptyResultMessageDisplayed() {
        Ui ui = Ui.createSilentUi();

        new RemindersCommand().execute(new TaskList(), ui, new Storage());

        assertEquals("You have no tasks due in the next 24 hours.", ui.getLastResponse());
    }

    @Test
    void execute_upcomingTask_originalTaskNumberDisplayed() {
        TaskList tasks = new TaskList(List.of(
                new ToDo("not dated"),
                new Deadline("submit report",
                        TaskDateTime.of(LocalDateTime.of(2026, 9, 10, 9, 0)))));
        Ui ui = Ui.createSilentUi();

        new RemindersCommand(FIXED_CLOCK).execute(tasks, ui, new Storage());

        String expected = "Here... These tasks are due in the next 24 hours:"
                + System.lineSeparator()
                + "2.[D][ ] submit report (by: Sep 10 2026 09:00)";
        assertEquals(expected, ui.getLastResponse());
    }

    @Test
    void execute_noUpcomingTask_emptyResultMessageDisplayed() {
        TaskList tasks = new TaskList(List.of(new Deadline("later report",
                TaskDateTime.of(LocalDateTime.of(2026, 9, 10, 9, 1)))));
        Ui ui = Ui.createSilentUi();

        new RemindersCommand(FIXED_CLOCK).execute(tasks, ui, new Storage());

        assertEquals("You have no tasks due in the next 24 hours.",
                ui.getLastResponse());
    }

    @Test
    void executeIfAny_noUpcomingTask_noResponseDisplayed() {
        TaskList tasks = new TaskList();
        Ui ui = Ui.createSilentUi();

        boolean isDisplayed = new RemindersCommand(FIXED_CLOCK).executeIfAny(tasks, ui);

        assertFalse(isDisplayed);
        assertEquals("", ui.getLastResponse());
    }
}
