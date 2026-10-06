package com.gihun456.reminder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.gihun456.model.Deadline;
import com.gihun456.model.Event;
import com.gihun456.model.Task;
import com.gihun456.ui.UiMessages;

/**
 * Evaluates and formats reminders for incomplete deadlines and events.
 */
public class ReminderService {
    private static final long REMINDER_WINDOW_HOURS = 72;
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    private final Clock clock;

    /**
     * Creates a reminder service using the supplied clock.
     *
     * @param clock Clock used to determine the current local date-time.
     */
    public ReminderService(Clock clock) {
        this.clock = clock;
    }

    /**
     * Creates a reminder service using the system default time zone.
     */
    public ReminderService() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a report for all applicable reminders.
     *
     * @param tasks Tasks to inspect.
     * @return Formatted reminder report, or an empty string when there are no reminders.
     */
    public String formatReport(List<Task> tasks) {
        LocalDateTime currentTime = LocalDateTime.now(clock);
        LocalDateTime upcomingLimit = currentTime.plusHours(REMINDER_WINDOW_HOURS);
        List<Reminder> upcoming = new ArrayList<>();
        List<Reminder> missed = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.isDone()) {
                continue;
            }

            LocalDateTime relevantTime = getRelevantTime(task);
            if (relevantTime == null) {
                continue;
            }

            Reminder reminder = new Reminder(i + 1, task, relevantTime);
            if (!relevantTime.isBefore(currentTime) && !relevantTime.isAfter(upcomingLimit)) {
                upcoming.add(reminder);
            } else if (relevantTime.isBefore(currentTime)) {
                missed.add(reminder);
            }
        }

        Comparator<Reminder> ordering = Comparator
                .comparing(Reminder::relevantTime)
                .thenComparingInt(Reminder::taskNumber);
        upcoming.sort(ordering);
        missed.sort(ordering);

        return formatReportSections(upcoming, missed);
    }

    private LocalDateTime getRelevantTime(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getDueDate();
        }
        if (task instanceof Event event) {
            return event.getStartDate();
        }
        return null;
    }

    private String formatReportSections(List<Reminder> upcoming, List<Reminder> missed) {
        StringBuilder report = new StringBuilder();
        appendSection(report, UiMessages.UPCOMING_REMINDERS, upcoming);
        appendSection(report, UiMessages.MISSED_REMINDERS, missed);
        return report.toString().trim();
    }

    private void appendSection(StringBuilder report, String heading, List<Reminder> reminders) {
        if (reminders.isEmpty()) {
            return;
        }
        if (report.length() > 0) {
            report.append("\n\n");
        }
        report.append(heading);
        for (Reminder reminder : reminders) {
            report.append("\n").append(formatReminder(reminder));
        }
    }

    private String formatReminder(Reminder reminder) {
        Task task = reminder.task();
        if (task instanceof Deadline deadline) {
            return String.format(
                    "%d. [Deadline] %s (by: %s)",
                    reminder.taskNumber(),
                    task.getTaskName(),
                    DISPLAY_FORMATTER.format(deadline.getDueDate()));
        }
        Event event = (Event) task;
        return String.format(
                "%d. [Event] %s (from: %s to: %s)",
                reminder.taskNumber(),
                task.getTaskName(),
                DISPLAY_FORMATTER.format(event.getStartDate()),
                DISPLAY_FORMATTER.format(event.getEndDate()));
    }

    private record Reminder(int taskNumber, Task task, LocalDateTime relevantTime) {
    }
}
