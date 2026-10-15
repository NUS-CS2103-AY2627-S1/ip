package squirtlebot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import squirtlebot.TemporalPair;
import squirtlebot.exception.CommandException;
import squirtlebot.storage.Storage;
import squirtlebot.task.Event;
import squirtlebot.task.TaskList;
import squirtlebot.ui.Ui;

/**
 * Tests validation performed when creating an event command.
 */
public class AddEventCommandTest {
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
    public void setAttributes_noTaskDescription_throwsCommandException() {
        String input = "event /from 01-01-2026 /to 01-01-2027";

        CommandException exception = assertThrows(
                CommandException.class, () -> new AddEventCommand(input.split(" ")));

        assertEquals("Please provide the correct arguments for Event!", exception.getMessage());
    }

    @Test
    public void execute_validSchedule_addsEvent() {
        AddEventCommand command = new AddEventCommand(
                "event project meeting /from 13-09-2026 /to 14-09-2026".split(" "));
        ArrayList<TemporalPair> schedules = new ArrayList<>();
        schedules.add(new TemporalPair(LocalDate.of(2026, 9, 13), LocalDate.of(2026, 9, 14)));

        command.execute(tasks, ui, storage);

        assertEquals(new Event("project meeting", schedules), tasks.get(0));
        assertEquals(1, tasks.size());
    }

    @Test
    public void constructor_invalidStartDate_throwsCommandException() {
        String input = "event meeting /from tomorrow /to 14-09-2026";

        CommandException exception = assertThrows(
                CommandException.class, () -> new AddEventCommand(input.split(" ")));

        assertEquals("Invalid date/datetime detected!", exception.getMessage());
    }

    @Test
    public void setAttributes_startDateAfterEndDate_throwsCommandException() {
        String input = "event do cs2103 /from 01-01-2027 /to 01-01-2026";

        CommandException exception = assertThrows(
                CommandException.class, () -> new AddEventCommand(input.split(" ")));

        assertEquals("Event start date has to be earlier than end date!", exception.getMessage());
    }
}
