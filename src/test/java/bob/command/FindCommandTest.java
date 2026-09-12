package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import bob.storage.Storage;
import bob.task.TaskList;
import bob.task.ToDo;

/** Tests task-search behavior of {@link FindCommand}. */
class FindCommandTest {

    @Test
    void execute_matchingTasksPresent_returnsOnlyMatchingTasks() {
        TaskList taskList = new TaskList(List.of(
                new ToDo("read book"), new ToDo("write report"), new ToDo("return BOOK")));

        String response = new FindCommand("book").execute(taskList, new Storage());

        assertTrue(response.contains("Here are the matching missions:"));
        assertTrue(response.contains("1.[T][ ] read book"));
        assertTrue(response.contains("2.[T][ ] return BOOK"));
        assertFalse(response.contains("write report"));
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new FindCommand("book").isExit());
    }

    @Test
    void execute_noTasksMatch_returnsHeadingWithoutTaskLines() {
        String response = new FindCommand("book").execute(TaskList.of(new ToDo("write report")), new Storage());

        assertEquals("Here are the matching missions:", response);
    }
}
