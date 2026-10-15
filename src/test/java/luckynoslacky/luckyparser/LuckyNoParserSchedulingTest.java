package luckynoslacky.luckyparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import luckynoslacky.luckycommand.LuckyNoReschedCommand;
import luckynoslacky.luckycommand.LuckyNoSnoozeCommand;
import luckynoslacky.luckyresponse.LuckyNoFormatMessages;
import luckynoslacky.luckyresponse.LuckyNoQuips;
import luckynoslacky.luckytask.DeadlineTask;
import luckynoslacky.luckytask.EventTask;
import luckynoslacky.luckytask.TodoTask;

/** Tests parsing commands that change task schedules. */
class LuckyNoParserSchedulingTest extends LuckyNoParserTestSupport {
    /** Verifies that the default snooze extends a deadline by one hour. */
    @Test
    void parseCommand_defaultSnooze_extendsDeadlineByOneHour() throws Exception {
        DeadlineTask task = new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 12, 0));
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoSnoozeCommand command = assertInstanceOf(LuckyNoSnoozeCommand.class,
                scanner.parseCommand("snooze 1", 1));

        command.execute();

        assertEquals(LocalDateTime.of(2026, 8, 26, 13, 0), task.getByTime());
    }

    /** Verifies that a duration snooze extends only an event's end time. */
    @Test
    void parseCommand_durationSnooze_updatesOnlyEventEnd() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 8, 26, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 26, 13, 0);
        EventTask task = new EventTask("project meeting", start, end);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoSnoozeCommand command = assertInstanceOf(LuckyNoSnoozeCommand.class,
                scanner.parseCommand("snooze 1 /by 2 hours please", 1));

        command.execute();

        assertEquals(start, task.getStartTime());
        assertEquals(end.plusHours(2), task.getEndTime());
    }

    /** Verifies that an explicit snooze time replaces a deadline. */
    @Test
    void parseCommand_explicitSnoozeTime_replacesDeadline() throws Exception {
        DeadlineTask task = new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 12, 0));
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoSnoozeCommand command = assertInstanceOf(LuckyNoSnoozeCommand.class,
                scanner.parseCommand("snooze 1 /to 27 Aug 2026 5pm", 1));

        command.execute();

        assertEquals(LocalDateTime.of(2026, 8, 27, 17, 0), task.getByTime());
    }

    /** Verifies that an event reschedule uses the new start as the end reference. */
    @Test
    void parseCommand_eventRescheduleOvernight_usesNextEndDate() throws Exception {
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 12, 0),
                LocalDateTime.of(2026, 8, 26, 13, 0));
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoReschedCommand command = assertInstanceOf(LuckyNoReschedCommand.class,
                scanner.parseCommand(
                        "resched 1 /from 26 Aug 2026 11pm /to 1am", 1));

        command.execute();

        assertEquals(LocalDateTime.of(2026, 8, 26, 23, 0), task.getStartTime());
        assertEquals(LocalDateTime.of(2026, 8, 27, 1, 0), task.getEndTime());
    }

    /** Verifies that partial event start rescheduling preserves the end. */
    @Test
    void parseCommand_eventFromOnly_preservesExistingEnd() throws Exception {
        LocalDateTime existingEnd = LocalDateTime.of(2026, 8, 26, 16, 0);
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0),
                existingEnd);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoReschedCommand command = assertInstanceOf(LuckyNoReschedCommand.class,
                scanner.parseCommand("resched 1 /from 26 Aug 2026 3pm", 1));

        command.execute();

        assertEquals(LocalDateTime.of(2026, 8, 26, 15, 0), task.getStartTime());
        assertEquals(existingEnd, task.getEndTime());
    }

    /** Verifies that partial event end rescheduling preserves the start. */
    @Test
    void parseCommand_eventToOnly_preservesExistingStart() throws Exception {
        LocalDateTime existingStart = LocalDateTime.of(2026, 8, 26, 14, 0);
        EventTask task = new EventTask(
                "project meeting",
                existingStart,
                LocalDateTime.of(2026, 8, 26, 16, 0));
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoReschedCommand command = assertInstanceOf(LuckyNoReschedCommand.class,
                scanner.parseCommand("resched 1 /to 5pm", 1));

        command.execute();

        assertEquals(existingStart, task.getStartTime());
        assertEquals(LocalDateTime.of(2026, 8, 26, 17, 0), task.getEndTime());
    }

    /** Verifies reversed markers use the new start as the end reference. */
    @Test
    void parseCommand_reversedEventMarkers_useNewStartReference() throws Exception {
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0),
                LocalDateTime.of(2026, 8, 26, 16, 0));
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoReschedCommand command = assertInstanceOf(LuckyNoReschedCommand.class,
                scanner.parseCommand(
                        "resched 1 /to 1am /from 26 Aug 2026 11pm", 1));

        command.execute();

        assertEquals(LocalDateTime.of(2026, 8, 26, 23, 0), task.getStartTime());
        assertEquals(LocalDateTime.of(2026, 8, 27, 1, 0), task.getEndTime());
    }

    /** Verifies duplicate and unknown event markers use the event format. */
    @Test
    void parseCommand_invalidEventMarkers_throwsEventFormat() {
        taskMaster.loadTasksFromCsvStorageRecord(List.of(
                new EventTask(
                        "project meeting",
                        LocalDateTime.of(2026, 8, 26, 14, 0),
                        LocalDateTime.of(2026, 8, 26, 16, 0))));
        String expectedMessage = LuckyNoFormatMessages.invalidFormatMessage(
                LuckyNoParser.CommandName.RESCHED,
                LuckyNoFormatMessages.reschedEventFormat());

        assertInputError(
                expectedMessage,
                "resched 1 /from 3pm /from 4pm", 1);
        assertInputError(
                expectedMessage,
                "resched 1 /when 5pm", 1);
        assertInputError(
                expectedMessage,
                "resched 1 /to 5pm /to 6pm", 1);
        assertInputError(
                expectedMessage,
                "resched 1 /from 3pm /to 5pm /extra", 1);
    }

    /** Verifies that ToDos receive dedicated snooze and reschedule errors. */
    @Test
    void parseCommand_timedCommandOnTodo_throwsDedicatedInputErrors() {
        taskMaster.loadTasksFromCsvStorageRecord(
                List.of(new TodoTask("read book")));

        assertInputError(
                LuckyNoQuips.cannotScheduleTodoMessage("snooze"),
                "snooze 1", 1);
        assertInputError(
                LuckyNoQuips.cannotScheduleTodoMessage("resched"),
                "resched 1 /to tomorrow", 1);
    }

    /** Verifies negative durations and malformed slash syntax are rejected. */
    @Test
    void parseCommand_invalidSnoozeArguments_throwsInputException() {
        taskMaster.loadTasksFromCsvStorageRecord(List.of(
                new DeadlineTask("return book", LocalDateTime.of(2026, 8, 26, 12, 0))));

        assertInputError(
                "Siao ah time where got negative one",
                "snooze 1 /by -2 hours", 1);
        assertInputError(
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.SNOOZE,
                        LuckyNoFormatMessages.snoozeByFormat(),
                        LuckyNoFormatMessages.snoozeToFormat()),
                "snooze 1 /by 2 hours /to tomorrow", 1);
        assertInputError(
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.SNOOZE,
                        LuckyNoFormatMessages.snoozeByFormat(),
                        LuckyNoFormatMessages.snoozeToFormat()),
                "snooze 1 /by 2 hours /extra", 1);
    }

    /** Verifies unrepresentable deadline and event snoozes use the dedicated error. */
    @Test
    void parseCommand_snoozeDurationOverflowForTimedTask_throwsDedicatedInputException() {
        taskMaster.loadTasksFromCsvStorageRecord(List.of(
                new DeadlineTask("return book", LocalDateTime.of(2026, 8, 26, 12, 0)),
                new EventTask(
                        "project meeting",
                        LocalDateTime.of(2026, 8, 26, 12, 0),
                        LocalDateTime.of(2026, 8, 26, 13, 0))));

        assertInputError(
                LuckyNoQuips.snoozeOverflowMessage(),
                "snooze 1 /by 1000000000 years", 1);
        assertInputError(
                LuckyNoQuips.snoozeOverflowMessage(),
                "snooze 2 /by 1000000000 years", 2);
    }

    /** Verifies invalid rescheduling formats depend on the selected task type. */
    @Test
    void parseCommand_rescheduleMissingFormat_usesTaskTypeFormat() {
        taskMaster.loadTasksFromCsvStorageRecord(List.of(
                new DeadlineTask("return book", LocalDateTime.of(2026, 8, 26, 12, 0)),
                new EventTask(
                        "project meeting",
                        LocalDateTime.of(2026, 8, 26, 12, 0),
                        LocalDateTime.of(2026, 8, 26, 13, 0))));

        assertInputError(
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.RESCHED,
                        LuckyNoFormatMessages.reschedDeadlineFormat()),
                "resched 1 /from tomorrow /to tomorrow", 2);
        assertInputError(
                LuckyNoFormatMessages.invalidFormatMessage(
                        LuckyNoParser.CommandName.RESCHED,
                        LuckyNoFormatMessages.reschedEventFormat()),
                "resched 2", 2);
    }

    /** Verifies invalid, zero, and out-of-range task numbers are rejected. */
    @Test
    void parseCommand_invalidTaskNumbers_throwsInputException() {
        assertInputError("Eh which task you talking about har? Can say clearly anot.", "mark", 1);
        assertInputError("You siao ah how to spin this task from thin air?", "mark 2", 1);
        assertInputError("Eh which task you talking about har? Can say clearly anot.",
                "unmark 1 extra", 1);
        assertInputError("You siao ah how to spin this task from thin air?", "unmark -1", 1);
        assertInputError("You siao ah how to spin this task from thin air?", "mark one", 1);
        assertInputError("Eh which task you talking about har? Can say clearly anot.",
                "delete", 1);
        assertInputError("You siao ah how to spin this task from thin air?",
                "delete 2", 1);
        assertInputError("Eh which task you talking about har? Can say clearly anot.",
                "delete 1 extra", 1);
    }

    /** Verifies list and bye reject extra arguments. */
    @Test
    void parseCommand_extraArgumentsForListAndBye_throwsInputException() {
        assertInputError("Why you so losor! Leave the list command to do its own thing lah", "list now", 0);
        assertInputError("Why you so losor! Leave the bye command to do its own thing lah", "bye now", 0);
        assertInputError("Why you so losor! Leave the list command to do its own thing lah", "list /now", 0);
        assertInputError("Why you so losor! Leave the bye command to do its own thing lah", "bye /now", 0);
    }
}
