package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import command.CommandResult;

/** Tests console input normalization and output delegation. */
public class UiTest {
    /** Verifies that command input is trimmed and exhausted correctly. */
    @Test
    public void readCommand_inputWithSurroundingWhitespace_trimmedInputReturned() {
        InputStream originalIn = System.in;
        try {
            System.setIn(new ByteArrayInputStream("  todo read book  \n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui();

            assertTrue(ui.hasNextCommand());
            assertEquals("todo read book", ui.readCommand());
            assertFalse(ui.hasNextCommand());
        } finally {
            System.setIn(originalIn);
        }
    }

    /** Verifies that the UI prints a command result's message. */
    @Test
    public void showResult_resultProvided_messagePrinted() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Ui ui = new Ui();

            ui.showResult(new CommandResult("operation completed", true, false));
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("operation completed" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    /** Verifies that an empty command result does not add a blank output line. */
    @Test
    public void showResult_emptyMessage_nothingPrinted() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new Ui().showResult(new CommandResult("", false, false));
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    /** Verifies that the UI prints an error message. */
    @Test
    public void showError_messageProvided_messagePrinted() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new Ui().showError("bad command");
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("bad command" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    /** Verifies the welcome output includes the banner, identity, guidance, and dividers. */
    @Test
    public void showWelcome_noArguments_completeGreetingPrinted() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            new Ui().showWelcome();
        } finally {
            System.setOut(originalOut);
        }

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("C B T"));
        assertTrue(text.contains("Mission control online. I'm CBT."));
        assertTrue(text.contains("Where shall we steer your day?"));
        assertEquals(2, text.split("_{60}", -1).length - 1);
    }

    /** Verifies the divider method prints exactly one divider line. */
    @Test
    public void showLine_noArguments_singleDividerPrinted() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            new Ui().showLine();
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("____________________________________________________________" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }
}
