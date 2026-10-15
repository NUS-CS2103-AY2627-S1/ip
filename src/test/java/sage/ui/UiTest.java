package sage.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sage.task.Task;
import sage.task.TaskType;

class UiTest {
    @Test
    void readCommand_readsLineFromInput() {
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream("todo read book\n".getBytes()));
        try {
            Ui ui = new Ui();
            assertEquals("todo read book", ui.readCommand());
            assertNull(ui.readCommand());
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    void displayMethods_printExpectedContent() {
        Ui ui = new Ui();
        Task task = new Task("watch lecture", TaskType.TODO);

        String welcomeOutput = captureOutput(ui::showWelcome);
        assertTrue(welcomeOutput.contains("Hello, I'm Sage."));

        String listOutput = captureOutput(() -> ui.showTaskList(List.of(task)));
        assertTrue(listOutput.contains("Here's your list:"));
        assertTrue(listOutput.contains("watch lecture"));

        String matchedOutput = captureOutput(() -> ui.showMatchingTasks(List.of(task), List.of(task)));
        assertTrue(matchedOutput.contains("Here's what I found:"));
        assertTrue(matchedOutput.contains("watch lecture"));

        String addedOutput = captureOutput(() -> ui.showAddedTask(task, 1));
        assertTrue(addedOutput.contains("Noted. One less thing to remember:"));

        String removedOutput = captureOutput(() -> ui.showRemovedTask(task, 0));
        assertTrue(removedOutput.contains("Cleared from your list:"));

        String markedOutput = captureOutput(() -> ui.showMarkedDone(task));
        assertTrue(markedOutput.contains("A little progress. Marked as done:"));

        String unmarkedOutput = captureOutput(() -> ui.showMarkedUndone(task));
        assertTrue(unmarkedOutput.contains("Back on your list. Marked as not done:"));

        String errorOutput = captureOutput(() -> ui.showError("bad input"));
        assertTrue(errorOutput.contains("Let's try that again. bad input"));

        String byeOutput = captureOutput(ui::showBye);
        assertTrue(byeOutput.contains("Take care. One step at a time."));
    }

    @Test
    void displayMethods_emptyLists_offerPracticalNextSteps() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showTaskList(List.of());
        ui.showMatchingTasks(List.of(), List.of());

        assertEquals(List.of("A clear page. Add a task with todo <task> or a note with note <text>.", Ui.LINE,
                "No matches this time. Try another word from the description.", Ui.LINE), output);
    }

    @Test
    void outputOnlyInterface_rejectsInputAndMissingConsumer() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        assertThrows(IllegalStateException.class, ui::readCommand);
        assertThrows(NullPointerException.class, () -> new Ui(null));
    }

    @Test
    void showWelcome_sendsTheSharedGreetingToAnyOutputDestination() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showWelcome();

        assertTrue(output.containsAll(Ui.WELCOME_MESSAGE.lines().toList()));
        assertEquals(Ui.LINE, output.getFirst());
        assertEquals(Ui.LINE, output.getLast());
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            action.run();
            return captured.toString();
        } finally {
            System.setOut(originalOut);
        }
    }
}
