package swell.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import swell.task.TaskList;
import swell.task.Todo;

public class UiTest {
    private static final String NEWLINE = System.lineSeparator();

    @Test
    public void getGreeting_returnsPersonalizedGreeting() {
        Ui ui = new Ui();

        assertEquals("Hi, I'm Swell, your calm task navigator." + NEWLINE
                + "Send me a task, and we'll chart a steady course.", ui.getGreeting());
    }

    @Test
    public void getTasksText_emptyTaskList_returnsEmptyMessage() {
        Ui ui = new Ui();

        assertEquals("Clear waters for now. Your task list is empty.",
                ui.getTasksText(new TaskList()));
    }

    @Test
    public void getTaskAddedText_oneTask_usesSingularTaskCount() {
        Ui ui = new Ui();

        String message = ui.getTaskAddedText(new Todo("read book"), 1);

        assertTrue(message.contains("There is now 1 task on board."));
    }

    @Test
    public void getTaskAddedText_multipleTasks_usesPluralTaskCount() {
        Ui ui = new Ui();

        String message = ui.getTaskAddedText(new Todo("read book"), 2);

        assertTrue(message.contains("There are now 2 tasks on board."));
    }

    @Test
    public void getErrorText_errorMessage_addsErrorPrefix() {
        Ui ui = new Ui();

        assertEquals("Oops! Choppy water ahead: Try: todo read book",
                ui.getErrorText("Try: todo read book"));
    }
}
