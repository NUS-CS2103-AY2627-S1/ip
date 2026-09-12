package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import bob.storage.Storage;
import bob.task.TaskList;
import bob.task.ToDo;

/** Tests task-list display behavior of {@link ListCommand}. */
class ListCommandTest {

    @Test
    void execute_tasksPresent_returnsNumberedTasks() {
        TaskList taskList = new TaskList(List.of(new ToDo("first task"), new ToDo("second task")));

        String response = new ListCommand().execute(taskList, new Storage());

        assertTrue(response.contains("1.[T][ ] first task"));
        assertTrue(response.contains("2.[T][ ] second task"));
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new ListCommand().isExit());
    }

    @Test
    void execute_emptyTaskList_returnsHeadingWithoutTaskLines() {
        assertEquals("Here's your mission list:",
                new ListCommand().execute(new TaskList(), new Storage()));
    }
}
