package meka.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import meka.task.TaskList;
import meka.task.Todo;

public class UiTest {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Test
    public void readCommand_paddedInput_trimsInputAndDetectsEnd() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("  todo read book  \n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui(new PrintStream(new ByteArrayOutputStream()));

            assertTrue(ui.hasNextCommand());
            assertEquals("todo read book", ui.readCommand());
            assertFalse(ui.hasNextCommand());
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    public void showWelcomeAndGoodbye_writesBannerPromptsAndSeparators() {
        OutputCapture capture = new OutputCapture();

        capture.ui.showWelcome();
        capture.ui.showGoodbye();
        capture.ui.showLine();

        String expected = """
                ____________________________________________________________
                ███╗   ███╗███████╗██╗  ██╗ █████╗
                ████╗ ████║██╔════╝██║ ██╔╝██╔══██╗
                ██╔████╔██║█████╗  █████╔╝ ███████║
                ██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║
                ██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║
                ╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝

                 Hello! I'm MEKA.
                 What can I do for you?
                 Bye. Hope to see you again soon!
                ____________________________________________________________
                """;
        assertEquals(expected, normalizeLineEndings(capture.text()));
    }

    @Test
    public void showTaskLists_multipleTasks_numbersTasksAndMatches() {
        OutputCapture capture = new OutputCapture();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        Todo second = new Todo("second");
        second.markAsDone();
        tasks.add(second);

        capture.ui.showTaskList(tasks);
        capture.ui.showMatchingTasks(tasks);

        assertEquals(lines(
                " 1. [T][ ] first",
                " 2. [T][X] second",
                " Here are the matching tasks in your list:",
                " 1. [T][ ] first",
                " 2. [T][X] second"), capture.text());
    }

    @Test
    public void showTaskChanges_taskLifecycle_writesConfirmationsAndCounts() {
        OutputCapture capture = new OutputCapture();
        Todo task = new Todo("read book");

        capture.ui.showTaskAdded(task, 1);
        task.markAsDone();
        capture.ui.showTaskMarked(task);
        task.unmark();
        capture.ui.showTaskUnmarked(task);
        capture.ui.showTaskDeleted(task, 0);

        assertEquals(lines(
                " Got it. I've added this task:",
                "   [T][ ] read book",
                " Now you have 1 tasks in the list.",
                " Nice! I've marked this task as done:",
                "   [T][X] read book",
                " OK, I've marked this task as not done yet:",
                "   [T][ ] read book",
                " Noted. I've removed this task:",
                "   [T][ ] read book",
                " Now you have 0 tasks in the list."), capture.text());
    }

    @Test
    public void showErrors_allErrorTypes_writesFriendlyMessages() {
        OutputCapture capture = new OutputCapture();

        capture.ui.showError("Invalid command.");
        capture.ui.showLoadingError();
        capture.ui.showSavingError();

        assertEquals(lines(
                " Invalid command.",
                " I could not load the saved tasks, so I started with an empty task list.",
                " I could not save the task list. Your changes are available only for this session."),
                capture.text());
    }

    private static String lines(String... lines) {
        return String.join(LINE_SEPARATOR, lines) + LINE_SEPARATOR;
    }

    private static String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n").replace("\r", "\n");
    }

    private static class OutputCapture {
        private final ByteArrayOutputStream output = new ByteArrayOutputStream();
        private final Ui ui = new Ui(new PrintStream(output, true, StandardCharsets.UTF_8));

        private String text() {
            return output.toString(StandardCharsets.UTF_8);
        }
    }
}
