package chudgpt.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import chudgpt.task.Priority;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.task.ToDo;

/** Tests console interaction and every user-facing message produced by the UI helper. */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    public void getWelcomeMessage_includesLogoGreetingAndDividers() throws Exception {
        String welcomeMessage = ui.getWelcomeMessage();

        assertTrue(welcomeMessage.startsWith(ui.getDivider() + "\n" + ui.getLogo()));
        assertTrue(welcomeMessage.contains("Hello! I'm ChudGPT.\nWhat can I do for you?"));
        assertTrue(welcomeMessage.endsWith(ui.getDivider()));
        assertEquals("____________________________________________________________", ui.getDivider());
    }

    @Test
    public void getTaskListMessage_emptyNormalAndSortedLists_returnsExpectedMessages() {
        TaskList emptyTasks = new TaskList();
        TaskList tasks = new TaskList(List.of(
                new ToDo("first"), new ToDo("urgent").setPriority(Priority.EXTREME)));

        assertEquals("You have no tasks in your list! Try adding some", ui.getTaskListMessage(emptyTasks));
        assertEquals("Here are the tasks in your list:\n"
                + "1. [T][ ][P:NONE   ] first\n2. [T][ ][P:EXTREME] urgent",
                ui.getTaskListMessage(tasks));
        assertEquals("Here are the tasks in your list:\n"
                + "2. [T][ ][P:EXTREME] urgent\n1. [T][ ][P:NONE   ] first",
                ui.getTaskListMessage(tasks, true));
    }

    @Test
    public void getMatchesMessage_emptyAndNonEmptyLists_returnsExpectedMessages() {
        assertEquals("I couldn't find any tasks that contains that keyword.",
                ui.getMatchesMessage(new TaskList()));
        assertEquals("Here are the matching tasks in your list:\n1. [T][ ][P:NONE   ] task",
                ui.getMatchesMessage(new TaskList(List.of(new ToDo("task")))));
    }

    @Test
    public void getSimpleMessages_returnsExpectedText() {
        assertEquals("Hi! I'm ChudGPT. How can I help you?", ui.getGreetingMessage());
        assertEquals("Bye. Hope to see you again soon!", ui.getGoodbyeMessage());
        assertEquals("I've saved your current list of tasks.", ui.getSaveMessage());
        assertEquals("OOPS!!! I couldn't retrieve the save file :( I'm such a chud...",
                ui.getLoadErrorMessage());
        assertEquals("OOPS!!! I've run into an error :( I'm such a chud...\nDetails:\n  broken",
                ui.getErrorMessage("broken"));
    }

    @Test
    public void getTaskChangeMessages_eachOutcome_returnsExpectedText() {
        Task task = new ToDo("task");

        assertEquals("Got it. I've added this task:\n  [T][ ][P:NONE   ] task\n"
                + "Now you have 1 tasks in your list.", ui.getTaskAddedMessage(task, 1));
        assertEquals("Got it. Noted. I've removed this task:\n  [T][ ][P:NONE   ] task\n"
                + "Now you have 0 tasks in your list.", ui.getTaskDeletedMessage(task, 0));
        assertEquals("OK, I've marked this task as incomplete.\n  [T][ ][P:NONE   ] task",
                ui.getTaskStatusMessage(task));

        task.setCompleted(true);
        assertEquals("Nice! I've marked this task as completed!\n  [T][X][P:NONE   ] task",
                ui.getTaskStatusMessage(task));
        assertEquals("Got it. I've removed the priority from this task:\n  [T][X][P:NONE   ] task",
                ui.getTaskPriorityMessage(task));

        task.setPriority(Priority.HIGH);
        assertEquals("Got it. I've set this task's priority to HIGH:\n  [T][X][P:HIGH   ] task",
                ui.getTaskPriorityMessage(task));
    }

    @Test
    public void getLoadWarningMessage_multipleWarnings_indentsEveryWarning() {
        String message = ui.getLoadWarningMessage(List.of("first warning", "second warning"));

        assertEquals("WARNING: Some saved tasks could not be loaded.\nDetails:\n"
                + "  first warning\n  second warning\n"
                + "A backup will be created before the recovered task list is saved.", message);
        assertThrows(AssertionError.class, () -> ui.getLoadWarningMessage(null));
        assertThrows(AssertionError.class, () -> ui.getLoadWarningMessage(List.of()));
    }

    @Test
    public void readCommand_consoleInput_returnsNextLine() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("first command\nsecond command\n"
                    .getBytes(StandardCharsets.UTF_8)));
            Ui consoleUi = new Ui();

            assertEquals("first command", consoleUi.readCommand());
            assertEquals("second command", consoleUi.readCommand());
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    public void display_message_printsMessageAndLineSeparator() throws Exception {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);

            ui.display("message");
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("message" + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
        assertFalse(output.toString(StandardCharsets.UTF_8).isBlank());
    }
}
