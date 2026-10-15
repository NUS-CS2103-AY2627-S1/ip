package charliek.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import charliek.model.Task;
import charliek.model.ToDo;

/**
 * Tests UI message rendering for output destinations shared by the console and GUI.
 */
class UiTest {
    /**
     * Verifies that a custom output sink receives the complete formatted response.
     */
    @Test
    void outputSink_taskAddedMessage_receivesFormattedResponse() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);

        ui.showTaskAdded(new ToDo("read book"), 1);

        String lineSeparator = System.lineSeparator();
        assertEquals("     Big win! I've added this task:" + lineSeparator
                + "       [T][ ] read book" + lineSeparator
                + "     Now you have 1 tasks in the list." + lineSeparator, output.toString());
    }

    /**
     * Verifies that normal and matching task lists share consistent numbering and formatting.
     */
    @Test
    void taskListMessages_differentHeadings_preserveTaskFormatting() {
        List<Task> tasks = List.of(new ToDo("read book"));
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);
        String lineSeparator = System.lineSeparator();

        ui.showTasks(tasks);
        ui.showMatchingTasks(tasks);

        assertEquals("     Here are the tasks in your winning list:" + lineSeparator
                + "     1.[T][ ] read book" + lineSeparator
                + "     Here are the matching tasks in your winning list:" + lineSeparator
                + "     1.[T][ ] read book" + lineSeparator, output.toString());
    }

    /**
     * Verifies that errors are reported separately from ordinary rendered output.
     */
    @Test
    void errorOutputSink_errorMessage_notifiesErrorSinkOnlyForErrors() {
        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        Ui ui = new Ui(output::append, errors::append);
        String lineSeparator = System.lineSeparator();

        ui.showTaskAdded(new ToDo("read book"), 1);
        assertEquals("", errors.toString());

        ui.showError("Unknown command.");

        assertEquals("     Unknown command." + lineSeparator, errors.toString());
    }

    /**
     * Verifies welcome rendering with and without a task-loading error.
     */
    @Test
    void welcomeMessage_successAndFailure_renderExpectedSections() {
        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        Ui ui = new Ui(output::append, errors::append);

        ui.showWelcome(null);
        assertTrue(output.toString().contains("Hello! I'm CharlieK."));
        assertEquals("", errors.toString());

        ui.showWelcome("loading failed");
        assertTrue(output.toString().contains("loading failed"));
        assertEquals("     loading failed" + System.lineSeparator(), errors.toString());
    }

    /**
     * Verifies all task-state messages are sent to normal output and include the task.
     */
    @Test
    void taskStateMessages_renderTaskDetails() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);
        Task task = new ToDo("read book");

        ui.showTaskMarked(task);
        ui.showTaskAlreadyMarked(task);
        ui.showTaskUnmarked(task);
        ui.showTaskAlreadyUnmarked(task);
        ui.showTaskDeleted(task, 0);

        String rendered = output.toString();
        assertTrue(rendered.contains("marked this task as done"));
        assertTrue(rendered.contains("already winning"));
        assertTrue(rendered.contains("marked this task as not done"));
        assertTrue(rendered.contains("already not done"));
        assertTrue(rendered.contains("removed this task"));
        assertTrue(rendered.contains("[T][ ] read book"));
    }

    /**
     * Verifies that the empty task list still displays its heading without task rows.
     */
    @Test
    void taskListMessages_emptyList_showHeadingsOnly() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);

        ui.showTasks(List.of());
        ui.showMatchingTasks(List.of());

        assertEquals("     Here are the tasks in your winning list:" + System.lineSeparator()
                + "     Here are the matching tasks in your winning list:" + System.lineSeparator(), output.toString());
    }

    /**
     * Verifies that unexpected processing errors use the dedicated error sink.
     */
    @Test
    void processingError_isHighlightedAsError() {
        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        Ui ui = new Ui(output::append, errors::append);

        ui.showProcessingError();

        assertEquals(output.toString(), errors.toString());
        assertTrue(errors.toString().contains("couldn't process"));
    }

    /**
     * Verifies that output-only UIs reject console input operations.
     */
    @Test
    void outputOnlyUi_inputMethods_throwIllegalStateException() {
        Ui ui = new Ui(ignored -> { });

        assertThrows(IllegalStateException.class, ui::hasNextCommand);
        assertThrows(IllegalStateException.class, ui::readCommand);
    }

    /**
     * Verifies that the default UI reads complete input lines from standard input.
     */
    @Test
    void defaultUi_readsCommandsFromStandardInput() {
        InputStream originalInput = System.in;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        java.io.PrintStream originalOutput = System.out;
        try {
            System.setIn(new ByteArrayInputStream("list\nbye\n".getBytes()));
            System.setOut(new java.io.PrintStream(output));
            Ui ui = new Ui();

            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertTrue(ui.hasNextCommand());
            assertEquals("bye", ui.readCommand());
            assertFalse(ui.hasNextCommand());
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    /**
     * Verifies that output collaborators cannot be omitted.
     */
    @Test
    void ui_nullOutputCollaborator_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Ui(null));
        assertThrows(NullPointerException.class, () -> new Ui(ignored -> { }, null));
    }
}
