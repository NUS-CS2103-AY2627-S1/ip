package panda.ui;

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

import panda.exception.EmptySearchTermException;
import panda.exception.InvalidCommandException;
import panda.exception.InvalidDateException;
import panda.exception.PandaException;
import panda.task.Deadline;
import panda.task.TaskList;
import panda.task.Todo;

/**
 * Tests the exact console text produced for commands and application lifecycle events.
 */
class UiTest {
    private static final String DIVIDER =
            "____________________________________________________________";

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private InputStream originalInput;
    private PrintStream originalOutput;

    @BeforeEach
    void redirectOutput() {
        originalInput = System.in;
        originalOutput = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOutput() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    void showWelcomeAndGoodbye_printAndReturnLifecycleMessages() {
        Ui ui = createUiWithInput("");

        String welcome = ui.showWelcome();
        String goodbye = ui.showGoodbye();

        assertTrue(welcome.contains("Hello! I'm Panda."));
        assertTrue(welcome.endsWith("What can I do for you?"));
        assertEquals("Bye. Hope to see you again soon!", goodbye);
        assertEquals(DIVIDER + System.lineSeparator()
                        + welcome + System.lineSeparator()
                        + DIVIDER + System.lineSeparator()
                        + goodbye + System.lineSeparator()
                        + DIVIDER + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void commandInput_multipleLines_readsUntilEndOfInput() {
        Ui ui = createUiWithInput("list\ntodo read book\n");

        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("todo read book", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void showTaskList_emptyAndPopulated_formatsHeadingNumbersTypesAndStatuses()
            throws InvalidDateException {
        Ui ui = createUiWithInput("");
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", "2026-09-14 18:00");
        deadline.mark();

        String emptyResponse = ui.showTaskList(List.of());
        String populatedResponse = ui.showTaskList(List.of(
                new TaskList.NumberedTask(2, todo),
                new TaskList.NumberedTask(5, deadline)));

        assertEquals(lines("Here are the tasks in your list:"), emptyResponse);
        assertEquals(lines(
                "Here are the tasks in your list:",
                "2.[T][ ] read book",
                "5.[D][X] submit report (by: Sep 14 2026 18:00)"),
                populatedResponse);
        assertEquals(emptyResponse + populatedResponse,
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showMatchingTaskList_preservesOriginalTaskNumbers() {
        Ui ui = createUiWithInput("");
        Todo todo = new Todo("read book");

        String response = ui.showMatchingTaskList(List.of(
                new TaskList.NumberedTask(3, todo)));

        assertEquals(lines(
                "Here are the matching tasks in your list:",
                "3.[T][ ] read book"), response);
        assertEquals(response, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showMutationResponses_allStates_useCorrectTextAndGrammar()
            throws InvalidDateException {
        Ui ui = createUiWithInput("");
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", "2026-09-14 18:00");
        deadline.mark();

        assertEquals(lines(
                "Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 task in the list."), ui.showAdded(todo, 1));
        assertEquals(lines(
                "Nice! I've marked this task as done:",
                "  [X] read book"), ui.showMarked(todo));
        assertEquals(lines(
                "OK, I've marked this task as not done yet:",
                "  [ ] read book"), ui.showUnmarked(todo));
        assertEquals(lines(
                "Got it. I've updated this task:",
                "  [D][X] submit report (by: Sep 14 2026 18:00)"),
                ui.showUpdated(deadline));
        assertEquals(lines(
                "Noted. I've removed this task:",
                "  [D][X] submit report (by: Sep 14 2026 18:00)",
                "Now you have 0 tasks in the list."), ui.showDeleted(deadline, 0));
    }

    @Test
    void showErrors_singleAndLoadingErrors_printExpectedMessagesAndDividers() {
        Ui ui = createUiWithInput("");
        PandaException firstError = new InvalidCommandException();
        PandaException secondError = new EmptySearchTermException();

        assertEquals(firstError.getMessage(), ui.showError(firstError));
        ui.showLoadingErrors(List.of(firstError, secondError));

        assertEquals(firstError.getMessage() + System.lineSeparator()
                        + DIVIDER + System.lineSeparator()
                        + firstError.getMessage() + System.lineSeparator()
                        + secondError.getMessage() + System.lineSeparator()
                        + DIVIDER + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    private Ui createUiWithInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Ui();
    }

    private String lines(String... lines) {
        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }
}
