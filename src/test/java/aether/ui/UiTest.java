package aether.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import aether.task.Todo;

class UiTest {
    @Test
    void readsCommandsUntilInputEnds() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("list\nbye\n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui();

            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertTrue(ui.hasNextCommand());
            assertEquals("bye", ui.readCommand());
            assertFalse(ui.hasNextCommand());
            ui.close();
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    void formatsPersonalityMessagesForEveryTaskAction() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");

        assertTrue(ui.getWelcomeMessage().contains("calm guide"));
        assertTrue(ui.getTaskAddedMessage(task, 1).contains("brought into focus"));
        assertTrue(ui.getTaskAddedMessage(task, 1).contains("1 task in the list"));
        assertTrue(ui.getTaskMarkedMessage(task).contains("complete"));
        assertTrue(ui.getTaskUnmarkedMessage(task).contains("back in motion"));
        assertTrue(ui.getTaskDeletedMessage(task, 0).contains("cleared from your orbit"));
        assertTrue(ui.getTaskDeletedMessage(task, 0).contains("0 tasks in the list"));
        assertTrue(ui.getTasksSortedMessage("tasks").startsWith("Your schedule is aligned"));
        assertTrue(ui.getGoodbyeMessage().contains("clear and focused"));
    }

    @Test
    void showMethodsPrintMessagesBetweenDividerLines() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
            Ui ui = new Ui();

            ui.showWelcome();
            ui.showError("problem");
            ui.showGoodbye();

            String output = capturedOutput.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Hello! I'm Aether"));
            assertTrue(output.contains("problem"));
            assertTrue(output.contains("Until next time"));
        } finally {
            System.setOut(originalOutput);
        }
    }
}
