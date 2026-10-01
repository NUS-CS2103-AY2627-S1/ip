package bo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import bo.task.Deadline;
import bo.task.Task;
import bo.task.Todo;

/** Tests the typed messages collected by {@link GuiUi}. */
public class GuiUiTest {
    @Test
    public void showWelcome_addsNormalMessageWithPersonality() {
        GuiUi ui = new GuiUi();

        ui.showWelcome("");

        GuiMessage message = ui.takeMessages().get(0);
        assertEquals(GuiMessage.MessageType.NORMAL, message.type());
        assertTrue(message.text().contains("Ready to help"));
    }

    @Test
    public void showError_addsErrorMessageWithPersonality() {
        GuiUi ui = new GuiUi();

        ui.showError("That command is invalid.");

        GuiMessage message = ui.takeMessages().get(0);
        assertEquals(GuiMessage.MessageType.ERROR, message.type());
        assertTrue(message.text().contains("confused"));
        assertTrue(message.text().contains("That command is invalid."));
    }

    @Test
    public void showTaskAdded_addsSuccessMessageWithTaskDetails() {
        GuiUi ui = new GuiUi();

        ui.showTaskAdded(new Todo("read book"), 1);

        GuiMessage message = ui.takeMessages().get(0);
        assertEquals(GuiMessage.MessageType.SUCCESS, message.type());
        assertTrue(message.text().contains("read book"));
        assertTrue(message.text().contains("You now have 1 tasks"));
    }

    @Test
    public void showTaskList_addsNormalMessageWithAllTasks() {
        GuiUi ui = new GuiUi();
        List<Task> tasks = List.of(new Todo("read book"), new Todo("buy milk"));

        ui.showTaskList(tasks);

        GuiMessage message = ui.takeMessages().get(0);
        assertEquals(GuiMessage.MessageType.NORMAL, message.type());
        assertTrue(message.text().contains("1.[T][ ] read book"));
        assertTrue(message.text().contains("2.[T][ ] buy milk"));
    }

    @Test
    public void showDeadlines_addsNormalMessageWithDeadlineDetails() {
        GuiUi ui = new GuiUi();
        List<Task> tasks = List.of(
                new Deadline("submit report", LocalDate.of(2999, 1, 1)));

        ui.showDeadlines(List.of(0), tasks, "Upcoming deadlines:");

        GuiMessage message = ui.takeMessages().get(0);
        assertEquals(GuiMessage.MessageType.NORMAL, message.type());
        assertTrue(message.text().contains("what deserves your attention"));
        assertTrue(message.text().contains("submit report"));
    }

    @Test
    public void takeMessages_returnsMessagesAndClearsQueue() {
        GuiUi ui = new GuiUi();
        ui.showError("first error");

        assertEquals(1, ui.takeMessages().size());
        assertTrue(ui.takeMessages().isEmpty());
    }
}
