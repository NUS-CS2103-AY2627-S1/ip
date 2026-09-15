package ekud.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import ekud.EkudException;
import ekud.task.Deadline;
import ekud.task.Task;
import ekud.task.TaskDateTime;
import ekud.task.TaskList;
import ekud.task.Todo;

/**
 * Tests {@link Ui} GUI-mode replies without using the terminal.
 * JavaFX windows are omitted; those are checked by running the GUI.
 */
public class UiTest {
    @Test
    public void forGui_readCommand_returnsByeAndCloseIsSafe() {
        Ui ui = Ui.forGui();
        assertEquals("bye", ui.readCommand());
        ui.close();
        ui.showPrompt();
        assertEquals("bye", ui.readCommand());
    }

    @Test
    public void showWelcome_recordsGreetingWithoutBanner() {
        Ui ui = Ui.forGui();
        ui.showWelcome();
        String message = ui.getLastMessage();
        assertTrue(message.contains("Hello! I'm Ekud."));
        assertTrue(message.contains("Add a task with todo, deadline, or event."));
    }

    @Test
    public void showTaskList_numbersTasksFromOne() {
        Ui ui = Ui.forGui();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        ui.showTaskList(tasks);
        String message = ui.getLastMessage();
        assertTrue(message.contains("1. [T][ ] first"));
        assertTrue(message.contains("2. [T][ ] second"));
    }

    @Test
    public void showFoundAndShowTasksOn_emptyLists_useShortMessages() throws EkudException {
        Ui ui = Ui.forGui();
        ui.showFound("lecture", List.of());
        assertEquals("No tasks matching \"lecture\".", ui.getLastMessage());

        ui.showTasksOn(LocalDate.of(2019, 12, 2), List.of());
        assertEquals("No deadlines or events on Dec 02 2019.", ui.getLastMessage());
    }

    @Test
    public void showTasksOn_withMatches_includesFormattedDate() throws EkudException {
        Ui ui = Ui.forGui();
        Task deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02"));
        ui.showTasksOn(LocalDate.of(2019, 12, 2), List.of(deadline));
        String message = ui.getLastMessage();
        assertTrue(message.contains("Here are the deadlines/events on Dec 02 2019:"));
        assertTrue(message.contains("1. [D][ ] return book (by: Dec 02 2019)"));
    }

    @Test
    public void showError_recordsMessage() {
        Ui ui = Ui.forGui();
        ui.showError("Something went wrong.");
        assertEquals("Something went wrong.", ui.getLastMessage());
    }
}
