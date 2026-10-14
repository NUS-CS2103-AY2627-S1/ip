package bingus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import bingus.task.TaskList;
import bingus.task.Todo;
import bingus.ui.Ui;

/**
 * Tests the read-only behaviour of {@link FindCommand}.
 */
class FindCommandTest {

    /**
     * Verifies that a find command reports its category and lists case-insensitive matches.
     */
    @Test
    void execute_matchingKeyword_returnsMatchingTasksWithoutChangingTaskList() {
        Todo matchingTask = new Todo("read book");
        Todo unrelatedTask = new Todo("buy groceries");
        TaskList tasks = new TaskList(List.of(matchingTask, unrelatedTask));
        FindCommand command = new FindCommand("BOOK");

        String response = command.execute(tasks, new Ui(), null);

        assertEquals(CommandType.FIND, command.getType());
        assertEquals("I found these matching tasks:\n1. [T][○] read book", response);
        assertEquals(List.of(matchingTask, unrelatedTask), tasks.getTasksSnapshot());
    }

    /**
     * Verifies that a find command reports a helpful message when no descriptions match.
     */
    @Test
    void execute_noMatchingKeyword_returnsNoMatchesMessage() {
        FindCommand command = new FindCommand("groceries");

        String response = command.execute(new TaskList(List.of(new Todo("read book"))), new Ui(), null);

        assertEquals("I could not find anything matching `groceries`.", response);
    }
}
