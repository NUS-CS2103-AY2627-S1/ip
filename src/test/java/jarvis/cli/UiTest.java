package jarvis.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import jarvis.classes.ToDo;

/** Tests cli input and output behavior. */
public class UiTest {
    /** Verifies that commands are read from standard input. */
    @Test
    public void readCommand_returnsNextInputLine() {
        PrintStream originalOutput = System.out;
        java.io.InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("todo Read book\n".getBytes(StandardCharsets.UTF_8)));
            assertEquals("todo Read book", new Ui().readCommand());
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    /** Verifies that the welcome output identifies Jarvis. */
    @Test
    public void showWelcome_printsJarvisGreeting() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new Ui().showWelcome();
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("I am Jarvis"));
        } finally {
            System.setOut(originalOutput);
        }
    }

    /** Verifies that task listing includes task numbers and descriptions. */
    @Test
    public void listAllTasks_printsNumberedTasks() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new Ui().listAllTasks(List.of(new ToDo("Read book")));
            String displayedTasks = output.toString(StandardCharsets.UTF_8);
            assertTrue(displayedTasks.contains("1. [T][] Read book"));
        } finally {
            System.setOut(originalOutput);
        }
    }
}
