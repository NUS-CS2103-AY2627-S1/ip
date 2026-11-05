package larp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests user-facing message formatting performed by {@link Ui}.
 */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    public void getTaskListMessage_emptyList_returnsEmptyState() {
        assertEquals("Your task list is empty.", ui.getTaskListMessage(new TaskList()));
    }

    @Test
    public void getMatchingTasksMessage_emptyList_returnsEmptyState() {
        assertEquals("No matching tasks found.",
                ui.getMatchingTasksMessage(new TaskList()));
    }

    @Test
    public void getAddedTaskMessage_oneTask_usesSingularNoun() {
        String message = ui.getAddedTaskMessage(new Todo("read book"), 1);

        assertTrue(message.contains("Now you have 1 task in the list."));
    }

    @Test
    public void getErrorMessage_regularMessage_marksOnlyErrorAsError() {
        String errorMessage = ui.getErrorMessage("Please enter a command.");

        assertEquals("Let's fix that: Please enter a command.", errorMessage);
        assertTrue(Ui.isErrorMessage(errorMessage));
        assertFalse(Ui.isErrorMessage("Your task list is empty."));
    }

    @Test
    public void getTaskListMessage_multipleTasks_numbersEveryTask() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("write report")));

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book" + System.lineSeparator()
                + "2.[T][ ] write report", ui.getTaskListMessage(tasks));
    }
}
