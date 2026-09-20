package woofer.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import woofer.task.TaskList;
import woofer.task.Todo;

/** Captures terminal output and always restores the process streams after each test. */
public class UiTest {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream output;
    private PrintStream capture;
    private Ui ui;

    /** Installs isolated input/output before constructing the UI's scanner. */
    @BeforeEach
    public void setUp() {
        originalInput = System.in;
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        capture = new PrintStream(output, true, StandardCharsets.UTF_8);
        System.setIn(new ByteArrayInputStream("list\n\ntodo 遛狗\n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(capture);
        ui = new Ui();
    }

    /** Restores global streams even when an assertion fails. */
    @AfterEach
    public void tearDown() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        capture.close();
    }

    /** Checks input preserves blank lines and Unicode, and detects end of input. */
    @Test
    public void readsCommandsUntilEndOfInput() {
        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertEquals("", ui.readCommand());
        assertEquals("todo 遛狗", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    /** Checks greeting, exit, and error details are visible in terminal output. */
    @Test
    public void displaysMessagesAndWarnings() {
        ui.showWelcome();
        ui.showResponse("custom response");
        ui.showError("Required format: todo <description>.");
        ui.showLoadingError();
        ui.showSavingError();
        ui.showBye();
        String text = output.toString(StandardCharsets.UTF_8);
        for (String expected : List.of("Woofer", "custom response", "Required format: todo <description>.",
                "Could not load saved tasks", "Could not save your tasks", "Time for a nap", "_".repeat(100))) {
            assertTrue(text.contains(expected), expected);
        }
    }

    /** Checks task output contains numbering, descriptions, status changes, and counts. */
    @Test
    public void displaysTaskOperationsAndSearchResults() {
        Todo task = new Todo("walk dog");
        TaskList tasks = new TaskList();
        tasks.addTask(task);
        ui.showTaskList(tasks);
        ui.showMatchingTasks(List.of(task));
        ui.showMatchingTasks(List.of());
        ui.showAddedTask(task, 1);
        ui.showMarkedTask(task, true);
        ui.showMarkedTask(task, false);
        ui.showDeletedTask(task, 0);
        String text = output.toString(StandardCharsets.UTF_8);
        for (String expected : List.of("1.[T][ ] walk dog", "No matching tasks", "[X] walk dog",
                "[ ] walk dog", "1 tasks", "0 tasks", "removed this task")) {
            assertTrue(text.contains(expected), expected);
        }
    }
}
