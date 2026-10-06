package com.gihun456.reminder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gihun456.model.Deadline;
import com.gihun456.model.Event;
import com.gihun456.model.Todo;
import com.gihun456.ui.UiMessages;

public class ReminderServiceTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 13, 19, 37);
    private static final ZoneId ZONE = ZoneId.of("Asia/Singapore");

    private final ReminderService reminderService = new ReminderService(
            Clock.fixed(NOW.atZone(ZONE).toInstant(), ZONE));

    @Test
    public void formatReport_dueWithin72Hours_includesDeadline() {
        Deadline deadline = new Deadline("Submit report", NOW.plusHours(72));

        String report = reminderService.formatReport(List.of(deadline));

        assertEquals(
                UiMessages.UPCOMING_REMINDERS + "\n"
                        + "1. [Deadline] Submit report (by: Sep 16 2026, 7:37 PM)",
                report);
    }

    @Test
    public void formatReport_pastDeadline_includesMissedDeadline() {
        Deadline deadline = new Deadline("Submit report", NOW.minusDays(4));

        String report = reminderService.formatReport(List.of(deadline));

        assertEquals(
                UiMessages.MISSED_REMINDERS + "\n"
                        + "1. [Deadline] Submit report (by: Sep 09 2026, 7:37 PM)",
                report);
    }

    @Test
    public void formatReport_completedAndDistantTasks_excludesTasks() {
        Deadline completed = new Deadline("Completed", NOW.minusHours(1));
        completed.markAsDone();
        Deadline distant = new Deadline("Distant", NOW.plusHours(73));

        assertEquals("", reminderService.formatReport(List.of(completed, distant)));
    }

    @Test
    public void formatReport_eventUsesStartTimeAndPreservesTaskNumber() {
        Todo todo = new Todo("Unrelated");
        Event event = new Event("Team meeting", NOW.plusHours(1), NOW.plusHours(2));

        String report = reminderService.formatReport(List.of(todo, event));

        assertEquals(
                UiMessages.UPCOMING_REMINDERS + "\n"
                        + "2. [Event] Team meeting (from: Sep 13 2026, 8:37 PM to: Sep 13 2026, 9:37 PM)",
                report);
    }

    @Test
    public void formatReport_sameTimestamp_sortsByTaskNumber() {
        Deadline first = new Deadline("First", NOW.plusHours(1));
        Deadline second = new Deadline("Second", NOW.plusHours(1));

        String report = reminderService.formatReport(List.of(first, second));

        assertEquals(
                UiMessages.UPCOMING_REMINDERS + "\n"
                        + "1. [Deadline] First (by: Sep 13 2026, 8:37 PM)\n"
                        + "2. [Deadline] Second (by: Sep 13 2026, 8:37 PM)",
                report);
    }

    @Test
    public void formatReport_upcomingAndMissedTasks_includesBothGameSections() {
        Deadline upcoming = new Deadline("Upcoming", NOW.plusHours(1));
        Event missed = new Event("Missed", NOW.minusHours(1), NOW.plusHours(1));

        String report = reminderService.formatReport(List.of(upcoming, missed));

        assertEquals(
                UiMessages.UPCOMING_REMINDERS + "\n"
                        + "1. [Deadline] Upcoming (by: Sep 13 2026, 8:37 PM)\n\n"
                        + UiMessages.MISSED_REMINDERS + "\n"
                        + "2. [Event] Missed (from: Sep 13 2026, 6:37 PM to: Sep 13 2026, 8:37 PM)",
                report);
    }
}
