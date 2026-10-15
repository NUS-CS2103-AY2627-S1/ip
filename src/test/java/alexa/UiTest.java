package alexa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests user-facing response formatting that does not require console input. */
class UiTest {
    private final Ui ui = new Ui();

    @Test
    void getTaskListMessage_emptyAndPopulatedLists_formatsTasksWithNumbers() {
        assertEquals("Here are the tasks in your list:", ui.getTaskListMessage(new TaskList()));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book\n2.[T][ ] buy groceries",
                ui.getTaskListMessage(new TaskList(List.of(new Todo("read book"), new Todo("buy groceries")))));
    }

    @Test
    void getMatchingTasksMessage_emptyAndPopulatedLists_formatsTasksWithNumbers() {
        assertEquals("Here are the matching tasks in your list:", ui.getMatchingTasksMessage(List.of()));
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book",
                ui.getMatchingTasksMessage(List.of(new Todo("read book"))));
    }

    @Test
    void getTaskStatusMessage_doneAndUndone_formatsAppropriateConfirmation() {
        Todo task = new Todo("read book");
        task.markAsDone();

        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book",
                ui.getTaskStatusMessage(task, true));
        task.unmark();
        assertEquals("Ok, I've marked this task as not done yet:\n  [T][ ] read book",
                ui.getTaskStatusMessage(task, false));
    }

    @Test
    void getTaskAddedDeletedAndErrorMessages_formatsExpectedResponses() {
        Todo task = new Todo("read book");

        assertEquals("Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
                ui.getTaskAddedMessage(task, 1));
        assertEquals("Noted. I've removed this task:\n  [T][ ] read book\nNow you have 0 tasks in the list.",
                ui.getTaskDeletedMessage(task, 0));
        assertEquals("OOPS!!! problem", ui.getErrorMessage("problem"));
    }
}