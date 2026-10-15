package remy.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import remy.task.Task;
import remy.task.Todo;

/**
 * Tests task-related messages displayed by {@link Ui}.
 */
class UiTest {
    private InputStream originalStandardIn;
    private PrintStream originalStandardOut;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        originalStandardIn = System.in;
        originalStandardOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalStandardIn);
        System.setOut(originalStandardOut);
    }

    @Test
    void readCommand_consoleInput_commandReturnedAndInputCanBeClosed() {
        System.setIn(new ByteArrayInputStream("todo read book\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        String command = ui.readCommand();
        ui.close();

        assertEquals("todo read book", command);
    }

    @Test
    void readCommand_outputOnlyUi_illegalStateExceptionThrownAndCloseIsSafe() {
        Ui ui = new Ui(System.out);

        IllegalStateException exception = assertThrows(IllegalStateException.class, ui::readCommand);
        ui.close();

        assertEquals("This user interface does not accept input.", exception.getMessage());
    }

    @Test
    void showGreetingAndError_messagesDisplayed() {
        Ui ui = new Ui();

        ui.showGreeting();
        ui.showError("Invalid command");

        String actualOutput = getNormalizedOutput();
        assertTrue(actualOutput.contains("Yo! I am Remy the rat from Ratatouille."));
        assertTrue(actualOutput.contains("How can I serve you today? :D"));
        assertTrue(actualOutput.contains("Invalid command\n"));
    }

    @Test
    void showMatchingTasks_matchingTasks_expectedHeadingAndNumberedTasksDisplayed() {
        Task firstTask = new Todo("read book");
        firstTask.markAsDone();
        Task secondTask = new Todo("return book");
        secondTask.markAsDone();

        new Ui().showMatchingTasks(List.of(firstTask, secondTask));

        String expectedOutput = """
                ____________________________________________________________

                Here are the matching tasks in your list:
                1. [T][X] read book
                2. [T][X] return book
                ____________________________________________________________

                """;
        assertEquals(expectedOutput, getNormalizedOutput());
    }

    @Test
    void showTaskList_tasks_expectedHeadingAndNumberedTasksDisplayed() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");

        new Ui().showTaskList(List.of(firstTask, secondTask));

        String expectedOutput = """
                ____________________________________________________________

                Here are the tasks in your list:
                1. [T][ ] read book
                2. [T][ ] return book
                ____________________________________________________________

                """;
        assertEquals(expectedOutput, getNormalizedOutput());
    }

    @Test
    void showTaskList_noTasks_emptyListMessageDisplayed() {
        new Ui().showTaskList(List.of());

        assertTrue(getNormalizedOutput().contains("There are no tasks in your list yet."));
    }

    @Test
    void showTaskChanges_addedDeletedAndMarked_confirmationsDisplayed() {
        Ui ui = new Ui();
        Task task = new Todo("read book");

        ui.showTaskAdded(task, 1);
        ui.showTaskDeleted(task, 0);
        task.markAsDone();
        ui.showTaskMarkedAsDone(task);
        task.markAsUndone();
        ui.showTaskMarkedAsUndone(task);

        String actualOutput = getNormalizedOutput();
        assertTrue(actualOutput.contains("helped you create a task"));
        assertTrue(actualOutput.contains("Now you have 1 task(s)"));
        assertTrue(actualOutput.contains("helped you removed a task"));
        assertTrue(actualOutput.contains("Now you have 0 tasks"));
        assertTrue(actualOutput.contains("marked this task as done"));
        assertTrue(actualOutput.contains("marked this task as not done yet"));
    }

    @Test
    void showFarewell_noArguments_farewellBetweenDividersDisplayed() {
        new Ui().showFarewell();

        String expectedOutput = """
                ____________________________________________________________
                Cya. Call me again when you need me!
                ____________________________________________________________

                """;
        assertEquals(expectedOutput, getNormalizedOutput());
    }

    @Test
    void showSavingError_actionableMessage_messageDisplayed() {
        new Ui().showSavingError("Unable to save: access was denied.");

        assertEquals("Unable to save: access was denied.\n", getNormalizedOutput());
    }

    @Test
    void showLoadingWarning_recoveryMessage_messageDisplayed() {
        new Ui().showLoadingWarning("Skipped invalid data on line 2.");

        assertEquals("Skipped invalid data on line 2.\n", getNormalizedOutput());
    }

    private String getNormalizedOutput() {
        return output.toString().replace(System.lineSeparator(), "\n");
    }
}
