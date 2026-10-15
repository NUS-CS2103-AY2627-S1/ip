package bubu.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import bubu.task.Deadline;
import bubu.task.ToDo;

/** Tests formatting of user-facing chatbot responses. */
class UiTest {
    @Test
    void showWelcome_containsGreeting() {
        Ui ui = new Ui();

        ui.showWelcome();

        assertTrue(ui.getResponse().contains("Hello! I'm BUBU!"));
        assertTrue(ui.getResponse().contains("What can I do for you?"));
    }

    @Test
    void showError_appendsErrorMessage() {
        Ui ui = new Ui();

        ui.showError("Invalid command");

        assertTrue(ui.getResponse().contains("Invalid command"));
    }

    @Test
    void showTaskList_emptyList_showsEmptyMessage() {
        Ui ui = new Ui();

        ui.showTaskList(List.of());

        assertTrue(ui.getResponse().contains("task list is empty"));
    }

    @Test
    void showTaskList_multipleTasks_showsNumberedTasks() {
        Ui ui = new Ui();

        ui.showTaskList(List.of(new ToDo("read notes"), new ToDo("submit report")));

        assertTrue(ui.getResponse().contains("1. [T][ ] read notes"));
        assertTrue(ui.getResponse().contains("2. [T][ ] submit report"));
    }

    @Test
    void showUpcomingTasks_showsScheduledTask() {
        Ui ui = new Ui();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 18, 18, 0));

        ui.showUpcomingTasks(List.of(deadline));

        assertTrue(ui.getResponse().contains("submit report"));
    }

    @Test
    void clearResponse_removesPreviousMessages() {
        Ui ui = new Ui();
        ui.showError("old message");

        ui.clearResponse();

        assertTrue(ui.getResponse().isEmpty());
    }
}
