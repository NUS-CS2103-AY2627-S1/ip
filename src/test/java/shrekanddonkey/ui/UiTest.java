package shrekanddonkey.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import shrekanddonkey.task.Task;
import shrekanddonkey.task.Todo;

/**
 * Tests message buffer recording and presentation in {@link Ui}.
 */
public class UiTest {

    @Test
    public void showMessage_singleMessage_recordedInOutputBuffer() {
        Ui ui = new Ui();
        ui.showMessage("Hello world");
        assertEquals("Hello world", ui.getRecordedOutput());
    }

    @Test
    public void clearOutput_recordedMessages_clearsBuffer() {
        Ui ui = new Ui();
        ui.showMessage("Hello world");
        ui.clearOutput();
        assertEquals("", ui.getRecordedOutput());
    }

    @Test
    public void showTaskAdded_validTask_formatsConfirmation() {
        Ui ui = new Ui();
        Task task = new Todo("read book");
        ui.showTaskAdded(task, 1);
        String output = ui.getRecordedOutput();
        assertTrue(output.contains("Alright, added to the swamp list:"));
        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("Now ye have 1 tasks in the swamp."));
    }

    @Test
    public void showTaskList_nonEmptyList_formatsNumberedTasks() {
        Ui ui = new Ui();
        Task task1 = new Todo("read book");
        Task task2 = new Todo("wash car");
        ui.showTaskList(List.of(task1, task2));
        String output = ui.getRecordedOutput();
        assertTrue(output.contains("Here's what's lurkin' in the swamp:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] wash car"));
    }

    @Test
    public void showMatchingTasks_nonEmptyList_formatsNumberedMatches() {
        Ui ui = new Ui();
        Task task = new Todo("read book");
        ui.showMatchingTasks(List.of(task));
        String output = ui.getRecordedOutput();
        assertTrue(output.contains("Donkey found these in the swamp:"));
        assertTrue(output.contains("1.[T][ ] read book"));
    }

    @Test
    public void showError_errorMessage_setsErrorFlagAndRecordsOutput() {
        Ui ui = new Ui();
        ui.showError("Something went wrong");
        assertTrue(ui.isError());
        assertEquals("Something went wrong", ui.getRecordedOutput());
    }

    @Test
    public void clearOutput_afterError_resetsErrorFlag() {
        Ui ui = new Ui();
        ui.showError("Something went wrong");
        ui.clearOutput();
        assertFalse(ui.isError());
        assertEquals("", ui.getRecordedOutput());
    }
}
