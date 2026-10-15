package shai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shai.command.AddCommand;
import shai.command.DeleteCommand;
import shai.command.MarkCommand;
import shai.command.ReminderCommand;
import shai.command.UnmarkCommand;
import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.Deadline;
import shai.task.Reminder;
import shai.task.Task;
import shai.task.TaskList;
import shai.task.ToDo;
import shai.ui.Ui;

/** Tests command processing through the programmatic interface used by the GUI. */
class ShaiTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getGreeting_returnsGuiFriendlyIntroduction() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Yo, I'm Shai. What's good, King? Ready to get things done?", shai.getGreeting());
    }

    @Test
    void getResponse_addTodo_returnsResponseAndPersistsTask() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());

        String response = shai.getResponse("todo buy milk");

        String lineSeparator = System.lineSeparator();
        assertEquals(String.join(lineSeparator,
                "Added to the lineup, King.",
                "  [T][ ] buy milk",
                "Roster updated, King. You now have 1 tasks on the board."), response);
        assertEquals(String.join(lineSeparator,
                "Here's the current lineup, King.",
                "1.[T][ ] buy milk"), shai.getResponse("list"));
    }

    @Test
    void getResponse_findKeyword_returnsMatchingTasksOnly() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());
        shai.getResponse("todo read book");
        shai.getResponse("todo buy milk");

        assertEquals(String.join(System.lineSeparator(),
                "Here's what I found on the scouting report, King.",
                "1.[T][ ] read book"), shai.getResponse("find BOOK"));
        assertEquals("Nothing showed up on the scouting report, King.", shai.getResponse("find cooking"));
    }

    @Test
    void getResponse_markAndUnmark_returnsConfidentSuccessMessage() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());
        shai.getResponse("todo submit report");

        assertEquals(String.join(System.lineSeparator(),
                "That play worked perfectly, King.",
                "  [T][X] submit report"), shai.getResponse("mark 1"));
        assertEquals(String.join(System.lineSeparator(),
                "That play worked perfectly, King.",
                "  [T][ ] submit report"), shai.getResponse("unmark 1"));
    }

    @Test
    void getResponse_deleteMiddleTask_renumbersTasksAndPersistsChange() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Shai shai = new Shai(taskFile.toString());
        shai.getResponse("todo first");
        shai.getResponse("todo middle");
        shai.getResponse("todo last");

        assertEquals(String.join(System.lineSeparator(),
                "That one's been sent to the bench.",
                "  [T][ ] middle",
                "Roster updated, King. You now have 2 tasks on the board."),
                shai.getResponse("delete 2"));
        assertEquals(String.join(System.lineSeparator(),
                "Here's the current lineup, King.",
                "1.[T][ ] first",
                "2.[T][ ] last"), shai.getResponse("list"));

        Shai reloadedShai = new Shai(taskFile.toString());
        assertEquals(String.join(System.lineSeparator(),
                "Here's the current lineup, King.",
                "1.[T][ ] first",
                "2.[T][ ] last"), reloadedShai.getResponse("list"));
    }

    @Test
    void getResponse_bye_returnsKingGoodbye() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Until next time, King. Keep winning.", shai.getResponse("bye"));
    }

    @Test
    void getResponse_invalidCommand_returnsUserFriendlyError() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Turnover. Check your command, King.", shai.getResponse("unknown"));
        assertTrue(shai.wasLastResponseAnError());

        shai.getResponse("list");
        assertFalse(shai.wasLastResponseAnError());
    }

    @Test
    void getResponse_nullInput_returnsUserFriendlyError() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Please enter a command, King.", shai.getResponse(null));
        assertTrue(shai.wasLastResponseAnError());
    }

    @Test
    void getResponse_afterLoadFailure_doesNotOverwriteTaskFile() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        String malformedData = "X | 0 | invalid task\n";
        Files.writeString(taskFile, malformedData);
        Shai shai = new Shai(taskFile.toString());

        String response = shai.getResponse("todo replacement");

        assertEquals("I couldn't update your lineup because the task file could not be loaded. "
                + "Fix the task file before making changes, King.", response);
        assertEquals(malformedData, Files.readString(taskFile));
    }

    @Test
    void addCommand_saveFails_rollsBackTask() {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(new ToDo("buy milk"));

        assertThrows(ShaiException.class, () -> command.execute(tasks, new Ui(), failingStorage()));

        assertEquals(0, tasks.size());
    }

    @Test
    void deleteCommand_saveFails_restoresTask() {
        Task task = new ToDo("buy milk");
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(ShaiException.class, () ->
                new DeleteCommand(0).execute(tasks, new Ui(), failingStorage()));

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    void markAndUnmarkCommands_saveFails_restorePreviousStatus() {
        Task task = new ToDo("buy milk");
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(ShaiException.class, () ->
                new MarkCommand(0).execute(tasks, new Ui(), failingStorage()));
        assertFalse(task.isDone());

        task.markAsDone();
        assertThrows(ShaiException.class, () ->
                new UnmarkCommand(0).execute(tasks, new Ui(), failingStorage()));
        assertTrue(task.isDone());
    }

    @Test
    void reminderCommand_saveFails_restoresPreviousReminder() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2099, 9, 15, 17, 0));
        TaskList tasks = new TaskList();
        tasks.add(deadline);

        assertThrows(ShaiException.class, () ->
                new ReminderCommand(0, 180,
                        Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC")))
                                .execute(tasks, new Ui(), failingStorage()));

        assertEquals(Reminder.DEFAULT_MINUTES_BEFORE, deadline.getReminderMinutesBefore());
    }

    @Test
    void getResponse_remind_listsUpcomingRemindersChronologically() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString(), clock);
        shai.getResponse("deadline submit report /by 2026-09-15 1700");
        shai.getResponse("event team meeting /from 2026-09-18 1400 /to 2026-09-18 1600");

        assertEquals(String.join(System.lineSeparator(),
                "Here's the reminder lineup, King.",
                "1.[D][ ] submit report (by: Sep 15 2026, 5:00 PM) (reminder: Sep 14 2026, 5:00 PM)",
                "2.[E][ ] team meeting (from: Sep 18 2026, 2:00 PM to: Sep 18 2026, 4:00 PM) "
                        + "(reminder: Sep 17 2026, 2:00 PM)"), shai.getResponse("remind"));
    }

    @Test
    void getResponse_remind_listsMissedReminderForUpcomingTask() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDateTime.of(2026, 9, 12, 0, 0)));
        new Storage(taskFile.toString()).saveTasks(tasks);
        Shai shai = new Shai(taskFile.toString(), clock);

        assertEquals(String.join(System.lineSeparator(),
                "Missed reminders, King. Handle these while they're still upcoming:",
                "1.[D][ ] submit report (by: Sep 12 2026) "
                        + "(missed reminder: Sep 11 2026)"), shai.getResponse("remind"));
    }

    @Test
    void getResponse_remindConfiguration_updatesAndPersistsReminder() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Shai shai = new Shai(taskFile.toString(), clock);
        shai.getResponse("deadline submit report /by 2026-09-15 1700");

        assertEquals(String.join(System.lineSeparator(),
                "Locked in. I'll remind you at Sep 15 2026, 2:00 PM, King.",
                "  [D][ ] submit report (by: Sep 15 2026, 5:00 PM)"),
                shai.getResponse("remind 1 /before 3h"));
        assertEquals(String.join(System.lineSeparator(),
                "That reminder's been benched, King.",
                "  [D][ ] submit report (by: Sep 15 2026, 5:00 PM)"),
                shai.getResponse("remind 1 /off"));
        assertEquals("No reminders on the board. Stay ready, King.", shai.getResponse("remind"));
    }

    @Test
    void getResponse_eventReminder_canBeConfiguredAndDisabled() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString(), clock);
        shai.getResponse("event team meeting /from 2026-09-15 1400 /to 2026-09-15 1600");

        assertEquals(String.join(System.lineSeparator(),
                "Locked in. I'll remind you at Sep 15 2026, 12:00 PM, King.",
                "  [E][ ] team meeting (from: Sep 15 2026, 2:00 PM to: Sep 15 2026, 4:00 PM)"),
                shai.getResponse("remind 1 /before 2h"));
        assertEquals(String.join(System.lineSeparator(),
                "That reminder's been benched, King.",
                "  [E][ ] team meeting (from: Sep 15 2026, 2:00 PM to: Sep 15 2026, 4:00 PM)"),
                shai.getResponse("remind 1 /off"));
        assertEquals("No reminders on the board. Stay ready, King.", shai.getResponse("remind"));
    }

    @Test
    void getResponse_pastReminder_rejectsChangeAndPreservesDefaultReminder() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDateTime.of(2026, 9, 11, 13, 0)));
        new Storage(taskFile.toString()).saveTasks(tasks);
        Shai shai = new Shai(taskFile.toString(), clock);

        assertEquals("That reminder time is already in the past, King.",
                shai.getResponse("remind 1 /before 2h"));
        assertTrue(shai.wasLastResponseAnError());

        TaskList reloadedTasks = new Storage(taskFile.toString()).loadTasks();
        Deadline reloadedDeadline = (Deadline) reloadedTasks.get(0);
        assertEquals(Reminder.DEFAULT_MINUTES_BEFORE, reloadedDeadline.getReminderMinutesBefore());
    }

    @Test
    void getResponse_failedCommand_doesNotShowAutomaticReminderUntilSuccess() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDateTime.of(2026, 9, 12, 12, 0)));
        new Storage(taskFile.toString()).saveTasks(tasks);
        Shai shai = new Shai(taskFile.toString(), clock);

        assertEquals("Turnover. Check your command, King.", shai.getResponse("unknown"));
        assertEquals(String.join(System.lineSeparator(),
                "Here's the current lineup, King.",
                "1.[D][ ] submit report (by: Sep 12 2026, 12:00 PM)",
                "Reminder alert, King. Time to lock in.",
                "1.[D][ ] submit report (by: Sep 12 2026, 12:00 PM) "
                        + "(reminder: Sep 11 2026, 12:00 PM)"), shai.getResponse("list"));
    }

    @Test
    void getStartupReminderResponse_showsEachDueSoonReminderOnlyOnce() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 12, 12, 0));
        TaskList tasks = new TaskList();
        tasks.add(deadline);
        new Storage(taskFile.toString()).saveTasks(tasks);
        Shai shai = new Shai(taskFile.toString(), clock);

        assertEquals(String.join(System.lineSeparator(),
                "Reminder alert, King. Time to lock in.",
                "1.[D][ ] submit report (by: Sep 12 2026, 12:00 PM) "
                        + "(reminder: Sep 11 2026, 12:00 PM)"), shai.getStartupReminderResponse());
        assertEquals("", shai.getStartupReminderResponse());
    }

    @Test
    void getResponse_dueSoonReminder_isShownAfterFirstSuccessfulCommandOnly() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString(), clock);

        String firstResponse = shai.getResponse("deadline submit report /by 2026-09-12 1200");

        assertEquals(String.join(System.lineSeparator(),
                "Added to the lineup, King.",
                "  [D][ ] submit report (by: Sep 12 2026, 12:00 PM)",
                "Roster updated, King. You now have 1 tasks on the board.",
                "Reminder alert, King. Time to lock in.",
                "1.[D][ ] submit report (by: Sep 12 2026, 12:00 PM) "
                        + "(reminder: Sep 11 2026, 12:00 PM)"), firstResponse);
        assertEquals(String.join(System.lineSeparator(),
                "Here's the current lineup, King.",
                "1.[D][ ] submit report (by: Sep 12 2026, 12:00 PM)"), shai.getResponse("list"));
    }

    /** Returns storage that consistently fails writes for rollback tests. */
    private Storage failingStorage() {
        return new Storage(temporaryDirectory.resolve("unused.txt").toString()) {
            @Override
            public void saveTasks(TaskList tasks) throws ShaiException {
                throw new ShaiException("save failed");
            }
        };
    }
}
