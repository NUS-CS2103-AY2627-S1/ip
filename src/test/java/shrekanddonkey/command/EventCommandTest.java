package shrekanddonkey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Tests executing {@link EventCommand}.
 */
public class EventCommandTest {

    @Test
    public void execute_validEvent_addsTaskSuccessfully() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new EventCommand("meeting /from 2026-10-15 10:00 /to 2026-10-15 12:00").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("Alright, added to the swamp list:"));
        assertTrue(ui.getRecordedOutput().contains("[E][ ] meeting"));
    }

    @Test
    public void execute_missingMarkers_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new EventCommand("meeting /from 2026-10-15 10:00").execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("Please specify an event using /from and /to."));
    }

    @Test
    public void execute_emptyDescription_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new EventCommand("/from 2026-10-15 10:00 /to 2026-10-15 12:00").execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("description of a event cannot be empty"));
    }

    @Test
    public void execute_endBeforeStart_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new EventCommand("meeting /from 2026-10-15 12:00 /to 2026-10-15 10:00").execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("Please ensure the event end is not before its start."));
    }

    @Test
    public void execute_invalidDateFormat_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new EventCommand("meeting /from today /to tomorrow").execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("Please enter valid event dates"));
    }
}
