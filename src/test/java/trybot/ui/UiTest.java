package trybot.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import trybot.task.TaskList;
import trybot.task.Todo;

/** Tests console output formatting and input handling without starting JavaFX. */
class UiTest {
    @Test
    void ui_inputMethods_readLinesUntilEndOfInput() {
        java.io.InputStream originalInput = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("todo read\nbye\n".getBytes(StandardCharsets.UTF_8));
        try {
            System.setIn(input);
            Ui ui = new Ui();

            assertTrue(ui.hasMoreCommands());
            assertEquals("todo read", ui.readCommand());
            assertTrue(ui.hasMoreCommands());
            assertEquals("bye", ui.readCommand());
            assertFalse(ui.hasMoreCommands());
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    void ui_taskAndSearchOutput_handlesEmptyAndNonEmptyLists() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui ui = new Ui(printStream(buffer), printStream(buffer));
        TaskList empty = new TaskList();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        ui.showWelcome();
        ui.showTaskList(empty);
        ui.showTaskList(tasks);
        ui.showMatchingTasks(List.of());
        ui.showMatchingTasks(tasks.toList());
        ui.showTaskAdded(tasks.get(0), 1);
        ui.showTaskMarkedDone(tasks.get(0));
        ui.showTaskMarkedNotDone(tasks.get(0));
        ui.showTaskDeleted(tasks.get(0), 0);

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hello! I'm TryBot."));
        assertTrue(output.contains("Here are the tasks in your list:"));
        assertTrue(output.contains("No tasks match that keyword."));
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("Now you have 1 tasks in the list."));
        assertTrue(output.contains("Now you have 0 tasks in the list."));
    }

    @Test
    void ui_helpGoodbyeAndWarnings_writeExpectedMessagesToTheirStreams() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        Ui ui = new Ui(printStream(output), printStream(errors));

        ui.showHelp();
        ui.showGoodbye();
        ui.showLoadingError();
        ui.showSavingError();
        ui.showTagCreated(2, "school");
        ui.showTagsDeleted(2);

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("deadline: deadline <task> /by <date or time>"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Bye. Hope to see you again soon!"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Tag Created for Task 2: school"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Tags Deleted for Task 2"));
        assertTrue(errors.toString(StandardCharsets.UTF_8).contains("could not load"));
        assertTrue(errors.toString(StandardCharsets.UTF_8).contains("could not save"));
    }

    private static PrintStream printStream(ByteArrayOutputStream buffer) {
        return new PrintStream(buffer, true, StandardCharsets.UTF_8);
    }
}
