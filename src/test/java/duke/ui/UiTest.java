package duke.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import duke.task.Deadline;
import duke.task.Task;
import duke.task.ToDo;

/** Tests the terminal UI by capturing text written to standard output. */
public class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;
    private Ui ui;

    @BeforeEach
    public void redirectStandardOutput() {
        originalOutput = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    @AfterEach
    public void restoreStandardOutput() {
        System.setOut(originalOutput);
    }

    @Test
    public void getWelcomeMessage_returnsGreeting() {
        assertEquals("HELLO! I'm GARY!\nHow can I help you today?\n(Type bye to exit)", ui.getWelcomeMessage());
    }

    @Test
    public void showWelcome_printsBannerAndGreeting() {
        ui.showWelcome();

        String displayedText = getOutput();
        assertTrue(displayedText.contains("____"));
        assertTrue(displayedText.contains(ui.getWelcomeMessage()));
    }

    @Test
    public void showGoodbye_printsGoodbyeMessage() {
        ui.showGoodbye();

        assertEquals("Bye! Hope to see you again soon!\n\n", getOutput());
    }

    @Test
    public void showLine_printsSeparator() {
        ui.showLine();

        assertEquals("____________________________________________________________\n", getOutput());
    }

    @Test
    public void showEmptyInputError_printsErrorBetweenSeparators() {
        ui.showEmptyInputError();

        assertEquals("____________________________________________________________\n"
                + "Please enter a command.\n"
                + "____________________________________________________________\n", getOutput());
    }

    @Test
    public void showMessage_printsMessage() {
        ui.showMessage("Saved successfully");

        assertEquals("Saved successfully\n", getOutput());
    }

    @Test
    public void showError_printsMessageBetweenSeparators() {
        ui.showError("Something went wrong");

        assertEquals("____________________________________________________________\n"
                + "Something went wrong\n"
                + "____________________________________________________________\n", getOutput());
    }

    @Test
    public void showTaskAdded_printsTaskAndCount() {
        ui.showTaskAdded(new ToDo("read book"), 2);

        assertEquals("____________________________________________________________\n"
                + "Got it! Here's the task you added:\n"
                + "  [T] [ ] read book\n"
                + "Now you have 2 tasks in your list!\n"
                + "____________________________________________________________\n", getOutput());
    }

    @Test
    public void showTaskRemoved_printsTaskAndCount() {
        ui.showTaskRemoved(new ToDo("read book"), 1);

        assertEquals("____________________________________________________________\n"
                + "Noted. This task has been removed:\n"
                + "  [T] [ ] read book\n"
                + "Now you have 1 tasks in the list.\n"
                + "____________________________________________________________\n", getOutput());
    }

    @Test
    public void showMarkedTask_doneAndUndone_printsBothStates() {
        ui.showMarkedTask(3, true);
        ui.showMarkedTask(3, false);

        assertEquals("____________________________________________________________\n"
                + "Gary marked task 3 as done!\n"
                + "____________________________________________________________\n"
                + "____________________________________________________________\n"
                + "Gary marked task 3 as undone!\n"
                + "____________________________________________________________\n", getOutput());
    }

    @Test
    public void showTaskList_multipleTasks_numbersEveryTask() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 30)));

        ui.showTaskList(tasks);

        assertEquals("____________________________________________________________\n"
                + "These are the tasks you have in your list!\n"
                + "1. [T] [ ] read book\n"
                + "2. [D] [ ] submit report (by: 2026-09-30)\n"
                + "____________________________________________________________\n", getOutput());
    }

    @Test
    public void showMatchingTasks_emptyList_printsNoMatchesMessage() {
        ui.showMatchingTasks(new ArrayList<>());

        assertEquals("____________________________________________________________\n"
                + "No matching tasks found.\n"
                + "____________________________________________________________\n", getOutput());
    }

    @Test
    public void showMatchingTasks_multipleTasks_numbersEveryMatch() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("return book"));

        ui.showMatchingTasks(tasks);

        assertEquals("____________________________________________________________\n"
                + "Here are the matching tasks in your list:\n"
                + "1. [T] [ ] read book\n"
                + "2. [T] [ ] return book\n"
                + "____________________________________________________________\n", getOutput());
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
