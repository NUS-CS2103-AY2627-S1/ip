package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import bob.storage.Storage;
import bob.task.TaskList;
import bob.task.ToDo;

/** Tests task searching by word-prefix terms. */
class SearchCommandTest {

    @Test
    void execute_matchingTasksPresent_returnsMatchingTasks() {
        TaskList taskList = new TaskList(List.of(
                new ToDo("buy books"), new ToDo("write report"), new ToDo("read a book")));

        String response = new SearchCommand("boo").execute(taskList, new Storage());

        assertEquals("Here are the matching missions:\n"
                + "1.[T][ ] buy books\n3.[T][ ] read a book", response);
    }

    @Test
    void execute_noTasksMatch_returnsNoMatchMessage() {
        TaskList taskList = TaskList.of(new ToDo("write report"));

        assertEquals("I couldn't find any matching missions.",
                new SearchCommand("book").execute(taskList, new Storage()));
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new SearchCommand("book").isExit());
    }
}
