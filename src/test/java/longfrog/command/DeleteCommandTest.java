package longfrog.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import longfrog.task.TaskList;
import longfrog.task.Todo;
import longfrog.ui.Ui;

class DeleteCommandTest {
    @Test
    void execute_existingTask_removesTaskAndReportsItsName() {
        TaskList taskList = new TaskList(List.of(new Todo("swim")));
        List<String> messages = new ArrayList<>();

        boolean shouldExit = new DeleteCommand(taskList, 0).execute(new Ui(messages::add));

        assertFalse(shouldExit);
        assertTrue(taskList.getTasks().isEmpty());
        assertEquals(List.of("Released from the pond: swim"), messages);
    }

    @Test
    void execute_emptyTaskList_reportsActionSpecificError() {
        List<String> messages = new ArrayList<>();

        boolean shouldExit = new DeleteCommand(new TaskList(), 0).execute(new Ui(messages::add));

        assertFalse(shouldExit);
        assertEquals(List.of("The pond is clear—there are no tasks to delete."), messages);
    }
}
