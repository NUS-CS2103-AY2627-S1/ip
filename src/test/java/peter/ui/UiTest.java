package peter.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import peter.task.Todo;

public class UiTest {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    public void redirectSystemStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void readCommand_lineAndEndOfInput_returnsTrimmedLineThenEmpty() {
        System.setIn(new ByteArrayInputStream("  todo read book  \n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertEquals("todo read book", ui.readCommand());
        assertEquals("", ui.readCommand());
        ui.close();
    }

    @Test
    public void outputMethods_emptyAndPopulatedLists_renderExpectedMessages() {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        Ui ui = new Ui();

        ui.showWelcome();
        ui.showError("problem");
        ui.showTasksOnDate(List.of());
        ui.showTasksOnDate(List.of(new Todo("read book")));
        ui.showFoundTasks(List.of());
        ui.showFoundTasks(List.of(new Todo("write code")));
        ui.showMessage("first line\nsecond line");
        ui.close();

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("I'm Peter"));
        assertTrue(output.contains("problem"));
        assertTrue(output.contains("No tasks are charted"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("found no tasks"));
        assertTrue(output.contains("1.[T][ ] write code"));
        assertTrue(output.contains("     first line" + System.lineSeparator() + "     second line"));
    }
}
