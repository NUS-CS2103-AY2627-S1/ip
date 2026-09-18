package mochi.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import mochi.task.TaskList;
import mochi.task.Todo;

class UiTest {
    @Test
    void input_completeLinesAndEndOfInput_areHandled() {
        Ui ui = new Ui(new Scanner("todo book\nbye\n"), new PrintStream(new ByteArrayOutputStream()));
        assertTrue(ui.hasNextCommand());
        assertEquals("todo book", ui.readCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void responses_includePersonalityTaskDetailsAndClearErrors() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(buffer, true, StandardCharsets.UTF_8));
        Todo task = new Todo("read book");
        TaskList tasks = new TaskList();
        tasks.add(task);
        ui.showWelcome();
        ui.showAdded(task, 1);
        ui.showMarked(task);
        ui.showUnmarked(task);
        ui.showDeleted(task, 0);
        ui.showTaskList(tasks);
        ui.showMatchingTasks(new TaskList());
        ui.showError("A todo needs a description.");
        ui.showLine();
        ui.showGoodbye();

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("one bite at a time"));
        assertTrue(output.contains("Tucked away!"));
        assertTrue(output.contains("Now you have 1 task in the list."));
        assertTrue(output.contains("Now you have 0 tasks in the list."));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("Nothing here yet."));
        assertTrue(output.contains("Oops! A todo needs a description."));
        assertTrue(output.contains("Mochi will be here"));
    }
}
