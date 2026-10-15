package sioet.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sioet.task.Task;
import sioet.task.TaskList;
import sioet.task.Todo;

public class UiTest {

    @Test
    public void showWelcome_setsWelcomeResponse() {
        Ui ui = new Ui();

        ui.showWelcome();

        assertEquals(
                "Hello! I'm sioet.ui.Sioet.\n"
                        + "What can I do for you?",
                ui.getLastResponse()
        );
    }

    @Test
    public void showBye_setsByeResponse() {
        Ui ui = new Ui();

        ui.showBye();

        assertEquals(
                "Bye! Hope to see you again soon!",
                ui.getLastResponse()
        );
    }

    @Test
    public void showError_setsErrorResponse() {
        Ui ui = new Ui();

        ui.showError("invalid command");

        assertEquals(
                "I couldn't do that: invalid command",
                ui.getLastResponse()
        );
    }

    @Test
    public void showTaskAdded_setsCorrectResponse() {
        Ui ui = new Ui();
        Task task = new Todo("read book");

        ui.showTaskAdded(task, 1);

        assertTrue(
                ui.getLastResponse().contains("[T][ ] read book")
        );
        assertTrue(
                ui.getLastResponse().contains("Now you have 1 tasks")
        );
    }

    @Test
    public void showTasks_showsAllTasks() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));
        tasks.add(new Todo("go shopping"));

        ui.showTasks(tasks);

        assertTrue(ui.getLastResponse().contains("1.[T][ ] read book"));
        assertTrue(ui.getLastResponse().contains("2.[T][ ] go shopping"));
    }

    @Test
    public void showMatchingTasks_noMatches_showsCorrectMessage() {
        Ui ui = new Ui();

        ui.showMatchingTasks(List.of());

        assertEquals(
                "No matching tasks found.",
                ui.getLastResponse()
        );
    }

    @Test
    public void showMatchingTasks_withMatches_showsTasks() {
        Ui ui = new Ui();

        List<Task> matches = new ArrayList<>();
        matches.add(new Todo("read book"));

        ui.showMatchingTasks(matches);

        assertTrue(
                ui.getLastResponse().contains("[T][ ] read book")
        );
    }

    @Test
    public void showTasksDeleted_singleTask_showsCorrectResponse() {
        Ui ui = new Ui();

        ui.showTasksDeleted(
                "[T][ ] read book\n",
                1,
                0
        );

        assertTrue(
                ui.getLastResponse().contains(
                        "Noted. I've removed this task:"
                )
        );
        assertTrue(
                ui.getLastResponse().contains("[T][ ] read book")
        );
    }

    @Test
    public void showTasksDeleted_multipleTasks_showsCorrectResponse() {
        Ui ui = new Ui();

        ui.showTasksDeleted(
                "[T][ ] read book\n[T][ ] go shopping\n",
                2,
                0
        );

        assertTrue(
                ui.getLastResponse().contains(
                        "Noted. I've removed these tasks:"
                )
        );
        assertTrue(
                ui.getLastResponse().contains("[T][ ] read book")
        );
        assertTrue(
                ui.getLastResponse().contains("[T][ ] go shopping")
        );
    }

    @Test
    public void showTasksMarked_singleTask_showsCorrectResponse() {
        Ui ui = new Ui();

        ui.showTasksMarked(
                "[T][X] read book\n",
                1,
                true
        );

        assertTrue(
                ui.getLastResponse().contains(
                        "Nice! I've marked this task as done:"
                )
        );
    }

    @Test
    public void showTasksMarked_multipleTasks_showsCorrectResponse() {
        Ui ui = new Ui();

        ui.showTasksMarked(
                "[T][X] read book\n[T][X] go shopping\n",
                2,
                true
        );

        assertTrue(
                ui.getLastResponse().contains(
                        "Nice! I've marked these tasks as done:"
                )
        );
    }
}
