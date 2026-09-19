package yuno.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import yuno.exception.InvalidTaskNumberException;
import yuno.task.Deadline;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.task.Todo;

class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;
    private InputStream originalInput;
    private Ui ui;

    @BeforeEach
    void redirectSystemStreams() {
        originalOutput = System.out;
        originalInput = System.in;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    @AfterEach
    void restoreSystemStreams() {
        System.setOut(originalOutput);
        System.setIn(originalInput);
    }

    @Test
    void printGreeting_printsBannerAndGreeting() {
        ui.printGreeting();

        assertEquals("""
                __________________________________________________
                __   __ _   _ _   _  ___
                \\ \\ / /| | | | \\ | |/ _ \\
                 \\ V / | | | |  \\| | | | |
                  | |  | |_| | |\\  | |_| |
                  |_|   \\___/|_| \\_|\\___/

                I'm Yuno.
                Can we just get this over quickly?
                __________________________________________________
                """, normalizedOutput());
    }

    @Test
    void printHelp_printsAvailableCommands() {
        ui.printHelp();

        assertEquals("""
                You forgot the commands already? Fine. Pay attention this time.

                Dates accept yyyy-MM-dd or yyyy-MM-dd HHmm.

                TASKS
                  todo <description>
                    Adds a task without a date.

                  deadline <description> /by <yyyy-MM-dd HHmm>
                    Adds a task that must be completed by a particular time.

                  event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>
                    Adds an event with a start and end time.

                MANAGE
                  list
                    Shows all your stored responsibilities.

                  mark <task number>
                    Marks a task as complete. Miracles do happen.

                  unmark <task number>
                    Marks a task as incomplete again. Of course.

                  delete <task number>
                    Removes a task.

                  clear
                    Removes every task.

                SEARCH AND SORT
                  find <text>
                    Finds tasks containing the given text.

                  find /date <yyyy-MM-dd>
                    Finds tasks relevant to the given date.

                  sort
                    Sorts dated tasks from earliest to latest.

                  sort /order <asc|desc>
                    Sorts dated tasks in the chosen order.

                OTHER
                  help
                    Shows this list, since apparently you need it.

                  bye
                    Exits Yuno. Finally.
                __________________________________________________
                """, normalizedOutput());
    }

    @Test
    void printList_emptyAndPopulatedLists_printsCorrectBranches()
            throws InvalidTaskNumberException {
        ui.printList(new TaskList());
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book", true));
        ui.printList(tasks);

        assertEquals("""
                Wow, not even a single task? You are so lazy.
                __________________________________________________
                Wow. Look at how slow you are at completing these tasks.
                1. [T][X] read book
                __________________________________________________
                """, normalizedOutput());
    }

    @Test
    void printTasksForDate_emptyAndPopulatedLists_printsCorrectBranches() {
        ui.printTasksForDate(List.of());
        Task task = new Todo("read book", false);
        ui.printTasksForDate(List.of(task));

        assertEquals("""
                You have nothing. What I see is just someone being lazy on this particular date.
                __________________________________________________
                You are not even capable to finish all these in one go.
                - [T][ ] read book
                __________________________________________________
                """, normalizedOutput());
    }

    @Test
    void printTasksForName_emptyAndPopulatedLists_printsCorrectBranches() {
        ui.printTasksForName(List.of());
        ui.printTasksForName(List.of(
                new Todo("first task", false),
                new Todo("second task", true)));

        assertEquals("""
                You have nothing. You must be really happy since you are so lazy.
                __________________________________________________
                So many task sharing a word. Could you be repeating task to feel accomplished?
                - [T][ ] first task
                - [T][X] second task
                __________________________________________________
                """, normalizedOutput());
    }

    @Test
    void printAddTask_printsAddedTask() {
        ui.printAddTask(new Deadline(
                "submit report", false, LocalDateTime.of(2026, 8, 30, 18, 0)));

        assertEquals("""
                Added:
                [D][ ] submit report (by: Aug 30 2026, 06:00 PM)
                Just another task you won't finish.
                __________________________________________________
                """, normalizedOutput());
    }

    @Test
    void printTaskUpdates_printsAllTaskUpdateMessages() {
        Task task = new Todo("read book", true);

        ui.printMarkTask(task);
        ui.printUnmarkTask(task);
        ui.printDeleteTask(task);
        ui.printTasksCleared();

        assertEquals("""
                You actually completed a task? Bet it's the only task you'll ever complete.
                [T][X] read book
                __________________________________________________
                Wow! So you lied about completing it? Typical behavior from you.
                [T][X] read book
                __________________________________________________
                Wow! Did you give up, or did you actually finish it?
                [T][X] read book
                __________________________________________________
                Finally. Now that everything is gone, can I go now?
                __________________________________________________
                """, normalizedOutput());
    }

    @Test
    void printByeAndException_printsMessagesWithDividers() {
        ui.printException("Something went wrong.");
        ui.printBye();

        assertEquals("""
                Something went wrong.
                __________________________________________________
                Finally! Bye. I'm leaving!
                __________________________________________________
                """, normalizedOutput());
    }

    @Test
    void readCommand_availableInput_returnsNextLine() {
        System.setIn(new ByteArrayInputStream("todo read book\n".getBytes(StandardCharsets.UTF_8)));
        Ui inputUi = new Ui();

        assertEquals("todo read book", inputUi.readCommand());
    }

    @Test
    void getResponse_multipleMessages_returnsMostRecentResponse() {
        ui.printException("Something went wrong.");
        ui.printBye();

        assertEquals("Finally! Bye. I'm leaving!", ui.getResponse());
    }

    private String normalizedOutput() {
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
