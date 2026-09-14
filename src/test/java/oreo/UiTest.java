package oreo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import oreo.task.TaskList;
import oreo.task.Todo;

/** Tests the text formatter independently of the graphical interface. */
class UiTest {
    @Test
    void messages_formatTaskActionsAndSessionMessages() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");

        assertTrue(ui.getWelcomeMessage().contains("Hello! I'm OREO"));
        assertEquals("Bye. Hope to see you again soon!", ui.getGoodbyeMessage());
        assertTrue(ui.getTaskAddedMessage(task, 1).contains("Now you have 1 tasks"));
        assertEquals("Nice! I've marked this task as done:" + System.lineSeparator() + "  [X] read book",
                ui.getTaskMarkedMessage(task));
        assertEquals("OK, I've marked this task as not done yet:" + System.lineSeparator() + "  [ ] read book",
                ui.getTaskUnmarkedMessage(task));
        assertTrue(ui.getTaskDeletedMessage(task, 0).contains("Now you have 0 tasks"));
    }

    @Test
    void messages_trimListOutputAndTrackErrorState() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();

        assertEquals("Here are the tasks in your list:", ui.getTaskListMessage(tasks));
        assertEquals("No matches", ui.getMatchingTasksMessage(" No matches "));
        assertEquals("No tags", ui.getTagListMessage(" No tags "));
        assertFalse(ui.wasLastMessageAnError());
        assertEquals("Problem", ui.getErrorMessage("Problem"));
        assertTrue(ui.wasLastMessageAnError());

        ui.beginResponse();

        assertFalse(ui.wasLastMessageAnError());
    }
}
