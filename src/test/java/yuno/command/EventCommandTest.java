package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.task.Event;

class EventCommandTest extends CommandTestSupport {
    @Test
    void execute_validEvent_addsEvent() throws YunoException {
        CommandResult commandResult = new EventCommand(
                "meeting /from 2026-08-30 0900 /to 2026-08-30 1030")
                .execute(taskList, ui, storage);

        Event event = assertInstanceOf(Event.class, taskList.getTask(1));
        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(LocalDateTime.of(2026, 8, 30, 9, 0), event.getStartDateTime());
        assertEquals(LocalDateTime.of(2026, 8, 30, 10, 30), event.getEndDateTime());
        assertEquals(event, ui.getAddedTask());
    }

    @Test
    void execute_missingDescription_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand(" /from 2026-08-30 /to 2026-08-31")
                        .execute(taskList, ui, storage));
    }

    @Test
    void execute_missingEnd_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand("meeting /from 2026-08-30")
                        .execute(taskList, ui, storage));
    }

    @Test
    void execute_missingStart_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand("meeting /to 2026-08-31")
                        .execute(taskList, ui, storage));
    }

    @Test
    void execute_invalidStartOrEnd_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand("meeting /from invalid /to 2026-08-31")
                        .execute(taskList, ui, storage));
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand("meeting /from 2026-08-30 /to invalid")
                        .execute(taskList, ui, storage));
    }

    @Test
    void execute_repeatedDateParameter_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand(
                                "meeting /from 2026-08-30 /from 2026-08-31 /to 2026-09-01")
                        .execute(taskList, ui, storage));
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand(
                                "meeting /from 2026-08-30 /to 2026-08-31 /to 2026-09-01")
                        .execute(taskList, ui, storage));
    }

    @Test
    void execute_dateOnlyEvent_addsEventAtMidnight() throws YunoException {
        new EventCommand("meeting /from 2026-08-30 /to 2026-08-31")
                .execute(taskList, ui, storage);

        Event event = (Event) taskList.getTask(1);
        assertEquals(LocalDateTime.of(2026, 8, 30, 0, 0), event.getStartDateTime());
        assertEquals(LocalDateTime.of(2026, 8, 31, 0, 0), event.getEndDateTime());
    }

    @Test
    void execute_toBeforeFrom_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand("meeting /to 2026-08-31 /from 2026-08-30")
                        .execute(taskList, ui, storage));
    }

    @Test
    void execute_endBeforeStart_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand("meeting /from 2026-08-31 /to 2026-08-30")
                        .execute(taskList, ui, storage));
    }

    @Test
    void execute_endEqualsStart_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new EventCommand("meeting /from 2026-08-31 1000 /to 2026-08-31 1000")
                        .execute(taskList, ui, storage));
    }
}
