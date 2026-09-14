package penguin.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import penguin.task.TaskList;
import penguin.task.Todo;

/**
 * Tests console output and GUI response buffering.
 */
class UiTest {
    @Test
    void showMessage_validMessage_printsPenguinPrefix() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            new Ui().showMessage("Test message.");
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Pip: Test message."));
    }

    @Test
    void showTasks_emptyList_showsEmptyMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            new Ui().showTasks(new TaskList());
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Your task list is empty!"));
    }

    @Test
    void showTasksOnDate_noMatchingTasks_showsNoTasksMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            new Ui().showTasksOnDate(LocalDate.of(2099, 12, 31),
                    new ArrayList<>());
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains(
                "No deadlines or events occur on 31 Dec 2099."));
    }

    @Test
    void showTasks_nonEmptyList_displaysNumberedTasks() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        try {
            System.setOut(new PrintStream(output));
            new Ui().showTasks(tasks);
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("1. [T][ ] read book"));
    }

    @Test
    void showError_validMessage_usesPenguinPrefix() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            new Ui().showError("Invalid command.");
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Pip: Invalid command."));
    }

    @Test
    void showMessage_guiMode_buffersResponseWithoutConsoleOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            Ui ui = new Ui(false);
            ui.showMessage("GUI message.");

            assertEquals("Pip: GUI message.", ui.getResponse());
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("", output.toString());
    }
}
