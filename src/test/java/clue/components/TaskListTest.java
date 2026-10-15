package clue.components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import clue.tasks.ToDo;
import clue.ui.Ui;

/** Tests searching for tasks in a {@link TaskList}. */
class TaskListTest {

    @Test
    void find_keyword_returnsMatchingTasksWithNumbers() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("buy milk"));
        tasks.add(new ToDo("return BOOKS"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(null, System.in, output);

        tasks.find(ui, "books");

        String result = output.toString(StandardCharsets.UTF_8);
        System.out.println(result);
        assertTrue(result.contains("Here are some tasks found, sorted by relevance:"));
        assertTrue(result.contains("1. [T][ ] return BOOKS"));
        assertTrue(result.contains("2. [T][ ] read book"));
        assertFalse(result.contains("buy milk"));
    }

    @Test
    void find_unknownKeyword_reportsNoMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(null, System.in, output);

        tasks.find(ui, "movie");

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("No matching tasks found."));
    }
}
