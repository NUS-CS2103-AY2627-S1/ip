package floppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

class UiTest {
    @Test
    void commandInput_availableLine_readsAndReportsAvailability() {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(
                new Scanner("list"),
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8));

        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertFalse(ui.hasNextCommand());
        ui.close();
    }

    @Test
    void outputMethods_taskAndErrorMessages_printExpectedContent() {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(
                new Scanner(""),
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
        Todo todo = new Todo("read book");

        ui.showWelcome();
        ui.showTaskList(List.of(todo));
        ui.showMatchingTasks(List.of(todo));
        ui.showTaskAdded(todo, 1);
        ui.showTaskMarked(todo);
        ui.showTaskUnmarked(todo);
        ui.showTaskDeleted(todo, 0);
        ui.showError("Invalid command.");
        ui.showGoodbye();
        ui.close();

        String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hello! I'm Floppy."));
        assertTrue(output.contains("I keep tasks tidy and carrots close."));
        assertTrue(output.contains("Here are the tasks in your list:"));
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("Now you have 1 tasks in the list."));
        assertTrue(output.contains("marked this task as done"));
        assertTrue(output.contains("That deserves a celebratory carrot!"));
        assertTrue(output.contains("marked this task as not done"));
        assertTrue(output.contains("Now you have 0 tasks in the list."));
        assertTrue(output.contains("Oops! Invalid command."));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        assertTrue(output.contains("I'll be nibbling a carrot until then."));
    }

    @Test
    void showTaskAdded_everyThirdTask_addsCarrotRemark() {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(
                new Scanner(""),
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
        Todo todo = new Todo("read book");

        ui.showTaskAdded(todo, 2);
        assertFalse(outputBytes.toString(StandardCharsets.UTF_8).contains("Time for a carrot!"));

        outputBytes.reset();
        ui.showTaskAdded(todo, 3);
        assertTrue(outputBytes.toString(StandardCharsets.UTF_8).contains("Time for a carrot!"));
        ui.close();
    }
}
