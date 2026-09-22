package longfrog.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import longfrog.task.TaskList;
import longfrog.task.Todo;
import longfrog.ui.Ui;

class ListCommandTest {
    @Test
    void execute_emptyTaskList_displaysEmptyPondMessage() {
        List<String> messages = new ArrayList<>();

        boolean shouldExit = new ListCommand(new TaskList()).execute(new Ui(messages::add));

        assertFalse(shouldExit);
        assertEquals(List.of(
                "Tasks currently on the lily pads:",
                "The pond is clear—no tasks waiting."), messages);
    }

    @Test
    void execute_populatedTaskList_displaysNumberedTasksInOrder() {
        TaskList taskList = new TaskList(List.of(new Todo("first"), new Todo("second")));
        List<String> messages = new ArrayList<>();

        boolean shouldExit = new ListCommand(taskList).execute(new Ui(messages::add));

        assertFalse(shouldExit);
        assertEquals(List.of(
                "Tasks currently on the lily pads:",
                "1: [T][ ] first",
                "2: [T][ ] second"), messages);
    }
}
