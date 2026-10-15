package shrekanddonkey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Tests executing {@link DeadlineCommand}.
 */
public class DeadlineCommandTest {

    @Test
    public void execute_validDeadline_addsTaskSuccessfully() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new DeadlineCommand("submit report /by 2026-10-15 18:00").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("Alright, added to the swamp list:"));
        assertTrue(ui.getRecordedOutput().contains("[D][ ] submit report"));
    }

    @Test
    public void execute_missingByMarker_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new DeadlineCommand("submit report").execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("Please specify a deadline using /by."));
    }

    @Test
    public void execute_emptyDescription_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new DeadlineCommand("/by 2026-10-15").execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("description of a deadline cannot be empty"));
    }

    @Test
    public void execute_invalidDateFormat_showsErrorMessage() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/test.txt");

        new DeadlineCommand("submit report /by tomorrow").execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(ui.getRecordedOutput().contains("Please enter a valid deadline date"));
    }
}
