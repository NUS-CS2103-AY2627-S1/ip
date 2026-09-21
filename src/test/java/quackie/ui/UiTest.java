package quackie.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import quackie.task.TaskList;
import quackie.task.ToDo;

/** Tests the console UI using in-memory input and output streams. */
class UiTest {
    /** Verifies command reading, end-of-input handling, and task messages. */
    @Test
    void readsCommandsAndDisplaysMessages() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner("list\n"), new PrintStream(captured, true, StandardCharsets.UTF_8));
        TaskList tasks = new TaskList();
        ToDo task = new ToDo("read book");
        tasks.add(task);

        assertEquals("list", ui.readCommand());
        assertNull(ui.readCommand());
        ui.showWelcome();
        ui.showTasks(tasks);
        ui.showMatchingTasks(tasks);
        ui.showTaskAdded(task, 1);
        ui.showTaskDeleted(task, 0);
        ui.showTaskUpdated(task);
        ui.showTaskMarked(task);
        ui.showTaskUnmarked(task);
        ui.showError("problem");
        ui.showBye();

        String output = captured.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hello! I'm Quackie."));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains(String.join(System.lineSeparator(),
                " Got it. I've added this task:",
                "   [T][ ] read book",
                " Now you have 1 task in the list.")));
        assertTrue(output.contains(String.join(System.lineSeparator(),
                " Noted. I've removed this task:",
                "   [T][ ] read book",
                " Now you have 0 tasks in the list.")));
        assertTrue(output.contains(String.join(System.lineSeparator(),
                " Nice! I've marked this task as done:",
                "   [T][ ] read book")));
        assertTrue(output.contains(String.join(System.lineSeparator(),
                " Updated this task:",
                "   [T][ ] read book")));
        assertTrue(output.contains("OOPS!!! problem"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    /** Verifies that the task-count sentence uses the singular noun only for exactly one task. */
    @Test
    void formatsTaskCountWithCorrectPlural() {
        assertEquals(" Now you have 0 tasks in the list.", Ui.formatTaskCount(0));
        assertEquals(" Now you have 1 task in the list.", Ui.formatTaskCount(1));
        assertEquals(" Now you have 2 tasks in the list.", Ui.formatTaskCount(2));
    }
}
