package grower.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import grower.tasks.Task;
import grower.tasks.ToDo;

/**
 * Verifies command input and task-list response formatting.
 */
public class UiTest {
    @Test
    public void showTaskLists_multipleTasks_preservesOrderAndNumbering() {
        Ui ui = new Ui(false);
        Task completedTask = new ToDo("read book");
        completedTask.mark();
        List<Task> tasks = List.of(completedTask, new ToDo("exercise"));
        String expected = "1. [T][X] read book" + System.lineSeparator() + "2. [T][ ] exercise";

        ui.showTaskList(tasks);
        assertEquals(expected, ui.getOutput());

        ui.clearOutput();
        ui.showSearchResults(tasks);
        assertEquals(expected, ui.getOutput());
        assertEquals(ResponseType.NORMAL, ui.getResponseType());
    }

    @Test
    public void showTaskLists_emptyLists_preservesDistinctMessagesAndResponseTypes() {
        Ui ui = new Ui(false);

        ui.showTaskList(List.of());
        assertEquals("Time to get to work! Grow the list.", ui.getOutput());
        assertEquals(ResponseType.NORMAL, ui.getResponseType());

        ui.clearOutput();
        ui.showSearchResults(List.of());
        assertEquals("No results!!!!", ui.getOutput());
        assertEquals(ResponseType.WARNING, ui.getResponseType());
    }

    @Test
    public void readCommand_endOfInput_returnsNull() {
        InputStream original = System.in;
        try {
            System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui(false);
            assertEquals("list", ui.readCommand());
            assertNull(ui.readCommand());
            assertNull(ui.readCommand());
            ui.close();
        } finally {
            System.setIn(original);
        }
    }
}
