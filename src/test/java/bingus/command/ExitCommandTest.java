package bingus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import bingus.task.TaskList;
import bingus.task.Todo;
import bingus.ui.Ui;

/**
 * Tests the behaviour of {@link ExitCommand}.
 */
class ExitCommandTest {

    /**
     * Verifies that an exit command returns a farewell without changing the task list.
     */
    @Test
    void execute_returnsFarewellWithoutChangingTaskList() {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));
        ExitCommand command = new ExitCommand();

        String response = command.execute(tasks, new Ui(), null);

        assertEquals(CommandType.EXIT, command.getType());
        assertEquals("Bingus is curling up for a nap. See you soon!", response);
        assertEquals(List.of(todo), tasks.getTasksSnapshot());
    }
}
