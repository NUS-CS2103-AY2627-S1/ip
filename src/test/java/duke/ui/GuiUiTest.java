package duke.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import duke.task.Deadline;
import duke.task.Task;
import duke.task.ToDo;

/** Tests the text accumulated for display in the JavaFX interface. */
public class GuiUiTest {

    @Test
    public void consumeOutput_multipleMessages_returnsLinesAndClearsOutput() {
        GuiUi ui = new GuiUi();
        ui.showMessage("first");
        ui.showMessage("second");

        assertEquals("first\nsecond", ui.consumeOutput());
        assertEquals("", ui.consumeOutput());
    }

    @Test
    public void consumeOutput_afterError_clearsErrorState() {
        GuiUi ui = new GuiUi();
        ui.showError("Something went wrong");

        assertTrue(ui.hasError());
        assertEquals("Something went wrong", ui.consumeOutput());
        assertFalse(ui.hasError());
    }

    @Test
    public void showGoodbye_returnsExpectedMessage() {
        GuiUi ui = new GuiUi();

        ui.showGoodbye();

        assertEquals("Bye! Hope to see you again soon!", ui.consumeOutput());
    }

    @Test
    public void showTaskAdded_returnsTaskAndCount() {
        GuiUi ui = new GuiUi();

        ui.showTaskAdded(new ToDo("read book"), 2);

        assertEquals("Got it! Here's the task you added:\n"
                + "  [T] [ ] read book\n"
                + "Now you have 2 tasks in your list!", ui.consumeOutput());
    }

    @Test
    public void showTaskRemoved_returnsTaskAndCount() {
        GuiUi ui = new GuiUi();

        ui.showTaskRemoved(new ToDo("read book"), 1);

        assertEquals("Noted. This task has been removed:\n"
                + "  [T] [ ] read book\n"
                + "Now you have 1 tasks in the list.", ui.consumeOutput());
    }

    @Test
    public void showMarkedTask_doneAndUndone_returnsBothStates() {
        GuiUi ui = new GuiUi();

        ui.showMarkedTask(3, true);
        ui.showMarkedTask(3, false);

        assertEquals("Gary marked task 3 as done!\nGary marked task 3 as undone!", ui.consumeOutput());
    }

    @Test
    public void showTaskList_multipleTasks_numbersEveryTask() {
        GuiUi ui = new GuiUi();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 30)));

        ui.showTaskList(tasks);

        assertEquals("These are the tasks you have in your list!\n"
                + "1. [T] [ ] read book\n"
                + "2. [D] [ ] submit report (by: 2026-09-30)", ui.consumeOutput());
    }

    @Test
    public void showMatchingTasks_emptyList_returnsNoMatchesMessage() {
        GuiUi ui = new GuiUi();

        ui.showMatchingTasks(new ArrayList<>());

        assertEquals("No matching tasks found.", ui.consumeOutput());
    }

    @Test
    public void showMatchingTasks_multipleTasks_numbersEveryMatch() {
        GuiUi ui = new GuiUi();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("return book"));

        ui.showMatchingTasks(tasks);

        assertEquals("Here are the matching tasks in your list:\n"
                + "1. [T] [ ] read book\n"
                + "2. [T] [ ] return book", ui.consumeOutput());
    }
}
