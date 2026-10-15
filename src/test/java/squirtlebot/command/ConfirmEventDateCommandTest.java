package squirtlebot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import squirtlebot.TemporalPair;
import squirtlebot.exception.CommandException;
import squirtlebot.storage.Storage;
import squirtlebot.task.Event;
import squirtlebot.task.TaskList;
import squirtlebot.task.ToDo;
import squirtlebot.ui.Ui;

/**
 * Tests selecting a final schedule for an event.
 */
public class ConfirmEventDateCommandTest {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        storage = new Storage();
        storage.disable();
        tasks = new TaskList();
        ui = new Ui();
    }

    @Test
    public void execute_eventWithMultipleSchedules_keepsSelectedSchedule() {
        Event event = createEventWithTwoSchedules();
        tasks.add(event);

        new ConfirmEventDateCommand(new String[]{"confirm", "1", "2"}).execute(tasks, ui, storage);

        assertFalse(event.toString().contains("2026-09-13"));
        assertTrue(event.toString().contains("from: 2026-09-20 to: 2026-09-21"));
        assertEquals("Event updated:\n" + event, ui.getSavedMessage());
    }

    @Test
    public void execute_selectedTaskIsNotEvent_throwsCommandException() {
        tasks.add(new ToDo("read textbook"));
        ConfirmEventDateCommand command = new ConfirmEventDateCommand(new String[]{"confirm", "1", "1"});

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("Selected event was not an Event!", exception.getMessage());
    }

    @Test
    public void constructor_nonNumericIndex_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new ConfirmEventDateCommand(
                        new String[]{"confirm", "one", "two"}));

        assertEquals("Please enter a valid index :(", exception.getMessage());
    }

    @Test
    public void constructor_missingScheduleIndex_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> new ConfirmEventDateCommand(
                        new String[]{"confirm", "1"}));

        assertEquals("Please enter an index to confirm dates for :(", exception.getMessage());
    }

    @Test
    public void execute_taskIndexOutsideList_throwsCommandException() {
        ConfirmEventDateCommand command = new ConfirmEventDateCommand(
                new String[]{"confirm", "1", "1"});

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("Please enter a valid index :(", exception.getMessage());
    }

    @Test
    public void execute_eventAlreadyConfirmed_ignoresFurtherConfirmation() {
        Event event = createEventWithTwoSchedules();
        tasks.add(event);
        new ConfirmEventDateCommand(new String[]{"confirm", "1", "2"})
                .execute(tasks, ui, storage);

        new ConfirmEventDateCommand(new String[]{"confirm", "1", "99"})
                .execute(tasks, ui, storage);

        assertFalse(event.toString().contains("2026-09-13"));
        assertTrue(event.toString().contains("from: 2026-09-20 to: 2026-09-21"));
    }

    private Event createEventWithTwoSchedules() {
        ArrayList<TemporalPair> schedules = new ArrayList<>();
        schedules.add(new TemporalPair(LocalDate.of(2026, 9, 13), LocalDate.of(2026, 9, 14)));
        schedules.add(new TemporalPair(LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 21)));
        return new Event("project meeting", schedules);
    }
}
