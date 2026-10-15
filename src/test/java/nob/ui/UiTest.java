package nob.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nob.exception.NobException;
import nob.task.Task;
import nob.task.TaskList;
import nob.task.Todo;

/**
 * Tests text produced by the console user interface.
 */
public class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        originalOutput = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOutput);
    }

    @Test
    public void showWelcome_bannerAndGreetingPrinted() {
        ui.showWelcome();

        String actualOutput = output.toString(StandardCharsets.UTF_8);
        assertEquals(2, actualOutput.split("_{60}", -1).length - 1);
        assertTrue(actualOutput.contains("WASSUP! I'm Nob :)"));
        assertTrue(actualOutput.contains("How can I help you?"));
        assertTrue(actualOutput.contains("(⌐■_■)"));
    }

    @Test
    public void showTaskList_emptyList_emptyMessagePrinted() throws NobException {
        ui.showTaskList(new TaskList(1));

        assertOutput("Your task list is empty right now. Add a task using the 'todo', "
                + "'deadline' or 'event' commands.\n");
    }

    @Test
    public void showTaskList_nonEmptyList_numberedTasksPrinted() throws NobException {
        TaskList tasks = new TaskList(2);
        tasks.addTask(new Todo("first task"));
        tasks.addTask(new Todo("second task"));

        ui.showTaskList(tasks);

        assertOutput("Here are the tasks in your list:\n"
                + "1.[T] first task [ ]\n"
                + "2.[T] second task [ ]\n");
    }

    @Test
    public void showMatchingTasks_matchesExist_matchingNumberedTasksPrinted() throws NobException {
        TaskList tasks = new TaskList(3);
        tasks.addTask(new Todo("Read book"));
        tasks.addTask(new Todo("write report"));
        tasks.addTask(new Todo("return BOOK"));

        ui.showMatchingTasks(tasks, "book");

        assertOutput("Here are the matching tasks in your list:\n"
                + "1.[T] Read book [ ]\n"
                + "3.[T] return BOOK [ ]\n");
    }

    @Test
    public void showMatchingTasks_noMatch_noMatchMessagePrinted() throws NobException {
        TaskList tasks = new TaskList(1);
        tasks.addTask(new Todo("read book"));

        ui.showMatchingTasks(tasks, "report");

        assertOutput("No matching tasks found.\n");
    }

    @Test
    public void showTaskMessages_expectedTextPrinted() {
        Task task = new Todo("read book");

        ui.showTaskLimit(100);
        ui.showTaskAdded(task, 1);
        task.markAsDone();
        ui.showTaskMarked(task, true);
        task.markAsUndone();
        ui.showTaskMarked(task, false);
        ui.showTaskDeleted(task, 0);

        assertOutput("I can only store up to 100 tasks.\n"
                + "Alrighty. I've added this task:\n  [T] read book [ ]\n"
                + "Now you have 1 tasks in the list.\n"
                + "LET'S GOOO! I've marked this task as done:\n  [T] read book [✓]\n"
                + "Okay... I've marked this task as not done yet:\n  [T] read book [ ]\n"
                + "Noted. I've removed the task:\n  [T] read book [ ]\n"
                + "Now you have 0 tasks left in the list.\n");
    }

    @Test
    public void showSimpleMessages_expectedTextPrinted() {
        ui.showDivider();
        ui.showGoodbye();
        ui.showLoadingError();
        ui.showError("invalid input");
        ui.showTasksCleared();
        ui.showSavingError();
        ui.showInputError();

        assertOutput("____________________________________________________________\n"
                + "Goodbye! Hope to see you soon mate!\n"
                + "I couldn't load the saved tasks, so I'm starting with an empty list.\n"
                + "invalid input\n"
                + "Noted. I've cleared the entire task list.\n"
                + "I couldn't save the task list to disk. Your changes are kept for this run.\n"
                + "I couldn't read your input.\n");
    }

    @Test
    public void showHelp_allCommandsPrinted() {
        ui.showHelp();

        assertOutput("Here are the commands you can use:\n"
                + "  list\n"
                + "  todo DESCRIPTION\n"
                + "  deadline DESCRIPTION /by DATE_OR_TIME\n"
                + "  event DESCRIPTION /from START /to END\n"
                + "  find KEYWORD\n"
                + "  mark TASK_NUMBER\n"
                + "  unmark TASK_NUMBER\n"
                + "  delete TASK_NUMBER\n"
                + "  clear\n"
                + "  bye\n");
    }

    @Test
    public void showCommandHints_expectedSuggestionsPrinted() {
        ui.showMissingSpaceHint("todo", "todoread book");
        ui.showUnknownCommandHint();

        assertOutput("It looks like you are missing a space after 'todo'.\n"
                + "Did you mean: todo read book\n"
                + "That isn't any of the commands I know. Did you mean one of these?\n"
                + "  todo DESCRIPTION\n"
                + "  deadline DESCRIPTION /by DATE_OR_TIME\n"
                + "  event DESCRIPTION /from START /to END\n");
    }

    private void assertOutput(String expectedOutput) {
        assertEquals(expectedOutput.replace("\n", System.lineSeparator()), output.toString(StandardCharsets.UTF_8));
    }
}
