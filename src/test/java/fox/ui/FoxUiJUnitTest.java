package fox.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fox.task.TaskList;
import fox.task.Todo;

/** JUnit tests for user-visible Fox UI responses. */
class FoxUiJUnitTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;

    @BeforeEach
    void redirectOutput() {
        originalOutput = System.out;
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    void displaysTasksAndErrorsWithExpectedMessages() throws Exception {
        FoxUi ui = new FoxUi();
        TaskList tasks = new TaskList(1);
        tasks.add(new Todo("read book"));

        ui.showTasks(tasks);
        ui.showError("bad command");
        String text = output.toString();

        assertTrue(text.contains("Here are the tasks in your list:"));
        assertTrue(text.contains("1.[T][ ] read book"));
        assertTrue(text.contains("bad command"));
        assertTrue(text.contains("•︵•"));
    }
}
