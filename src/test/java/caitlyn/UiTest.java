package caitlyn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

/** Tests the UI output adapter used by both Caitlyn interfaces. */
public class UiTest {
    @Test
    public void outputConstructor_showTaskAdded_sendsMessagesToSink() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showTaskAdded(new Todo("read book"), 1);

        assertEquals(List.of(
                "     Got it. I've added this task:",
                "       [T][ ] read book",
                "     Now you have 1 tasks in the list."), messages);
    }

    @Test
    public void outputConstructor_hasNoInputSource() {
        Ui ui = new Ui(message -> { });

        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void showLoadingError_failedLoad_explainsProtectionAndRecovery() {
        List<String> messages = new ArrayList<>();
        new Ui(messages::add).showLoadingError();
        assertEquals(List.of("     I could not read data/duke.txt. Task changes are disabled "
                + "to protect your saved data. Repair the file or its access permissions, "
                + "then restart Caitlyn."), messages);
    }

    @Test
    public void graphicalAdapter_welcome_omitsConsoleDecoration() {
        List<String> messages = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Ui ui = new Ui(messages::add, errors::add);

        ui.showWelcome();
        ui.showSeparator();

        assertEquals(List.of("Good day, master. I am Caitlyn, humbly at your service.",
                "How may I serve you today?"), messages);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void graphicalAdapter_errors_routesSeparatelyFromReplies() {
        List<String> messages = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Ui ui = new Ui(messages::add, errors::add);

        ui.showError("Invalid command");
        ui.showLoadingError();
        ui.showTasks(List.of(new Todo("keep this task")));

        assertEquals(2, errors.size());
        assertEquals("     Invalid command", errors.getFirst());
        assertTrue(errors.get(1).contains("Task changes are disabled"));
        assertEquals(List.of("     Here are the tasks in your list:",
                "     1.[T][ ] keep this task"), messages);
    }

    @Test
    public void consoleAdapter_errorsAndWelcome_retainsOriginalOutput() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showWelcome();
        ui.showError("Invalid command");

        assertEquals(6, messages.size());
        assertTrue(messages.getFirst().startsWith("___"));
        assertTrue(messages.get(1).contains("____"));
        assertEquals(messages.getFirst(), messages.get(4));
        assertEquals("     Invalid command", messages.getLast());
    }

    @Test
    public void graphicalAdapter_missingDestination_rejectsConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new Ui(message -> { }, null));
        assertThrows(IllegalArgumentException.class, () -> new Ui(null, message -> { }));
    }

    @Test
    public void readCommand_scannerInput_trimsLinesAndDetectsEndOfInput() {
        try (Scanner scanner = new Scanner("  todo read book  \r\n\t \nlist")) {
            Ui ui = new Ui(scanner);
            assertTrue(ui.hasNextCommand());
            assertTrue(ui.hasNextCommand());
            assertEquals("todo read book", ui.readCommand());
            assertTrue(ui.hasNextCommand());
            assertEquals("", ui.readCommand());
            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertFalse(ui.hasNextCommand());
            assertThrows(NoSuchElementException.class, ui::readCommand);
        }
    }

    @Test
    public void readCommand_outputOnlyAdapter_reportsMissingInput() {
        Ui ui = new Ui(message -> { });
        assertThrows(IllegalStateException.class, ui::readCommand);
        assertThrows(IllegalArgumentException.class, () -> new Ui((Scanner) null));
        assertThrows(IllegalArgumentException.class, () -> new Ui((Consumer<String>) null));
    }

    @Test
    public void showTasks_emptyLists_emitsOnlyHeadings() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        ui.showTasks(List.of());
        ui.showMatchingTasks(List.of(new Todo("unmatched")), List.of());
        assertEquals(List.of("     Here are the tasks in your list:",
                "     Here are the matching tasks in your list:"), messages);
    }

    @Test
    public void showTaskChanges_completedTask_emitsCompleteConfirmations() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        Task task = new Todo("read book");
        task.markAsDone();
        ui.showTaskStatus(task, true);
        task.markAsNotDone();
        ui.showTaskStatus(task, false);
        ui.showTaskDeleted(task, 0);
        ui.showFarewell();
        assertEquals(List.of("     As you wish, master. I have marked this task as done:",
                "       [T][X] read book", "     Of course, master. I have marked this task as not done yet:",
                "       [T][ ] read book", "     Noted. I've removed this task:", "       [T][ ] read book",
                "     Now you have 0 tasks in the list.",
                "     Farewell, master. It has been my pleasure to serve you."), messages);
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    public void scannerAdapter_responses_writesToConsole() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(captured, true, StandardCharsets.UTF_8);
                Scanner scanner = new Scanner("")) {
            System.setOut(output);
            new Ui(scanner).showError("example error");
            assertEquals("     example error" + System.lineSeparator(), captured.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOutput);
        }
    }
}
