package carl.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carl.task.TaskStatus;
import carl.task.Todo;

public class UiTest {
    private Ui ui;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
    }

    @Test
    public void welcomeAndGoodbye_returnDispatcherPhrases() {
        assertEquals("Carl online. What are we getting done?", ui.showWelcome());
        assertEquals("Shift complete. See you next time.", ui.showGoodbye());
    }

    @Test
    public void showAddTask_includesTaskAndUpdatedCount() {
        String message = ui.showAddTask(new Todo("buy milk"), 2);

        assertTrue(message.startsWith("Logged. I’ll keep that on the radar."));
        assertTrue(message.contains("[T][ ] buy milk"));
        assertTrue(message.contains("Now you have 2 tasks in the list."));
    }

    @Test
    public void showTaskStateChanges_includeTaskDetails() {
        Todo completed = new Todo("buy milk", TaskStatus.DONE);

        assertEquals("Checked off. One less thing to worry about.\n  [T][X] buy milk",
                ui.showMarkTaskAsDone(completed));
        assertEquals("Successfully unmarked this task as not done! \n  [T][X] buy milk",
                ui.showUnmarkTask(completed));
        assertEquals("Successfully removed this task! \n  [T][X] buy milk\n"
                + "Now you have 0 tasks in the list.", ui.showDeleteTask(completed, 0));
    }

    @Test
    public void showTaskList_emptyAndPopulated_returnExpectedNumbering() {
        assertEquals("Radar clear — no tasks here.", ui.showTaskList(List.of()));
        assertEquals("1. [T][ ] first\n2. [T][ ] second\n",
                ui.showTaskList(List.of(new Todo("first"), new Todo("second"))));
    }
}
