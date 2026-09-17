package crystal.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import crystal.exception.CrystalException;
import crystal.task.Deadline;
import crystal.task.TaskList;
import crystal.task.Todo;
import crystal.task.WithinPeriod;

/**
 * Tests Crystal's console input prompts and all public response variants.
 */
public class UiTest {
    private static final String LINE =
            "____________________________________________________________";

    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    /** Saves and redirects standard streams for isolated UI assertions. */
    @BeforeEach
    public void redirectStandardStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    /** Restores the original process streams after each UI test. */
    @AfterEach
    public void restoreStandardStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies command prompting and preservation of the complete input line. */
    @Test
    public void readCommand_inputLine_printsPromptAndReturnsCompleteLine() {
        System.setIn(new ByteArrayInputStream("todo read book  \n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        String command = ui.readCommand();

        assertEquals("todo read book  ", command);
        assertEquals("You: ", getOutput());
    }

    /** Verifies that a response-only UI writes to its supplied stream. */
    @Test
    public void createForOutput_showGoodbye_usesSuppliedOutputStream() {
        ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
        Ui ui = Ui.createForOutput(
                new PrintStream(responseOutput, true, StandardCharsets.UTF_8));

        ui.showGoodbye();

        assertEquals("Crystal: Byeee!!! You did amazing today. See you soon!\n",
                responseOutput.toString(StandardCharsets.UTF_8));
        assertEquals("", getOutput());
    }

    /** Verifies the complete startup banner, greeting, command guide, and dividers. */
    @Test
    public void showWelcome_called_printsBannerGreetingGuideAndDividers() {
        Ui ui = new Ui();

        ui.showWelcome();

        assertEquals(LINE + "\n"
                + "  ____ ______   ______ _____  _    _\n"
                + " / ___|  _ \\ \\ / / ___|_   _|/ \\  | |\n"
                + "| |   | |_) \\ V /\\___ \\ | | / _ \\ | |\n"
                + "| |___|  _ < | |  ___) || |/ ___ \\| |___\n"
                + " \\____|_| \\_\\|_| |____/ |_/_/   \\_\\_____|\n"
                + "\nHiii!!! I'm Crystal, your sparkly task bestie!\n"
                + "[Crystal's command guide:\n"
                + "- To add a todo, enter 'todo [description]'\n"
                + "- To add a deadline, enter 'deadline [description] /by [deadline]'\n"
                + "- To add an event, enter 'event [description] /from [start] /to [end]'\n"
                + "- To add a within-period task, enter "
                + "'within [description] /from [start] /to [end]'\n"
                + "- To view your task list, enter 'list'\n"
                + "- To view dated tasks on a date, enter 'list /on [date]'\n"
                + "- To find tasks by description, enter 'find [keyword]'\n"
                + "- To mark a task as done, enter 'mark [task number]'\n"
                + "- To mark a task as not done, enter 'unmark [task number]'\n"
                + "- To delete a task, enter 'delete [task number]'\n"
                + "- To view this command guide, enter 'help'\n"
                + "- To exit, enter 'bye']\n"
                + LINE + "\n", getOutput());
    }

    /** Verifies that help reproduces the command guide shown during startup. */
    @Test
    public void showHelp_called_printsCompleteCommandGuide() {
        new Ui().showHelp();

        assertEquals("[Crystal's command guide:\n"
                + "- To add a todo, enter 'todo [description]'\n"
                + "- To add a deadline, enter 'deadline [description] /by [deadline]'\n"
                + "- To add an event, enter 'event [description] /from [start] /to [end]'\n"
                + "- To add a within-period task, enter "
                + "'within [description] /from [start] /to [end]'\n"
                + "- To view your task list, enter 'list'\n"
                + "- To view dated tasks on a date, enter 'list /on [date]'\n"
                + "- To find tasks by description, enter 'find [keyword]'\n"
                + "- To mark a task as done, enter 'mark [task number]'\n"
                + "- To mark a task as not done, enter 'unmark [task number]'\n"
                + "- To delete a task, enter 'delete [task number]'\n"
                + "- To view this command guide, enter 'help'\n"
                + "- To exit, enter 'bye']\n", getOutput());
    }

    /** Verifies divider, farewell, and Crystal exception output. */
    @Test
    public void basicResponses_called_printExpectedDividerFarewellAndError() {
        Ui ui = new Ui();

        ui.showDivider();
        ui.showGoodbye();
        ui.showError(new CrystalException("That task number does not exist!"));

        assertEquals(LINE + "\n"
                + "Crystal: Byeee!!! You did amazing today. See you soon!\n"
                + "Crystal: Oopsies!!! That task number does not exist!\n", getOutput());
    }

    /** Verifies the response for an empty complete task list. */
    @Test
    public void showTaskList_emptyList_printsEmptyMessage() {
        new Ui().showTaskList(new TaskList());

        assertEquals("Crystal: Your task list is all clear, well done!\n", getOutput());
    }

    /** Verifies one-based persistent numbering for a non-empty task list. */
    @Test
    public void showTaskList_nonEmptyList_printsPersistentOneBasedIndexes() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"), new Deadline("submit report", "2Dec26")));

        new Ui().showTaskList(tasks);

        assertEquals("Crystal: Ta-da!!! Here are all your tasks:\n"
                + "         1.[T][ ] read book\n"
                + "         2.[D][ ] submit report (by: 02 Dec 2026)\n", getOutput());
    }

    /** Verifies the date-specific response when a filtered list is empty. */
    @Test
    public void showTasksOnDate_noMatches_printsDateSpecificEmptyMessage() {
        new Ui().showTasksOnDate("02 Dec 2026", List.of());

        assertEquals("Crystal: No dated tasks on 02 Dec 2026, yay!\n",
                getOutput());
    }

    /** Verifies unnumbered output for a temporary date-filtered task list. */
    @Test
    public void showTasksOnDate_matches_printsUnnumberedTemporaryList() {
        new Ui().showTasksOnDate("02 Dec 2026", List.of(
                new Deadline("submit report", "2Dec26"),
                new Deadline("send invoice", "2Dec26 1700"),
                new WithinPeriod("collect certificate", "1Dec26", "3Dec26")));

        assertEquals("Crystal: Yay, here are your dated tasks on 02 Dec 2026:\n"
                + "         - [D][ ] submit report (by: 02 Dec 2026)\n"
                + "         - [D][ ] send invoice (by: 02 Dec 2026 1700)\n"
                + "         - [W][ ] collect certificate "
                + "(from: 01 Dec 2026 to: 03 Dec 2026)\n", getOutput());
    }

    /** Verifies all status-change and repeated-status response variants. */
    @Test
    public void showMatchingTasks_noMatches_printsEmptySearchMessage() {
        new Ui().showMatchingTasks(List.of());

        assertEquals("Crystal: Aww, I couldn't find any matching tasks!\n",
                getOutput());
    }

    @Test
    public void showMatchingTasks_matches_printsTemporaryOneBasedIndexes() {
        Todo completedTodo = new Todo("read book");
        completedTodo.markAsDone();

        new Ui().showMatchingTasks(List.of(
                completedTodo, new Deadline("return book", "June 6th")));

        assertEquals("Crystal: Yay, I found these matching tasks:\n"
                + "         1.[T][X] read book\n"
                + "         2.[D][ ] return book (by: June 6th)\n", getOutput());
    }

    @Test
    public void showTaskStatusResponses_allVariants_printExpectedHeadingsAndTask() {
        Todo doneTask = new Todo("read book");
        doneTask.markAsDone();
        Todo incompleteTask = new Todo("return book");
        Ui ui = new Ui();

        ui.showTaskAlreadyDone(doneTask);
        ui.showTaskAlreadyNotDone(incompleteTask);
        ui.showTaskMarkedDone(doneTask);
        ui.showTaskMarkedNotDone(incompleteTask);

        assertEquals("Crystal: This task is already done, good job!\n"
                + "         [T][X] read book\n"
                + "Crystal: This task is already waiting for you!\n"
                + "         [T][ ] return book\n"
                + "Crystal: Yayyy!!! You finished this task:\n"
                + "         [T][X] read book\n"
                + "Crystal: Okkk!!! I've put this task back on your list:\n"
                + "         [T][ ] return book\n", getOutput());
    }

    /** Verifies singular and plural grammar after task additions. */
    @Test
    public void showTaskAdded_singularAndPluralCounts_useCorrectGrammar() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");

        ui.showTaskAdded(task, 1);
        ui.showTaskAdded(task, 2);

        assertEquals("Crystal: Okkk!!! Here's your new task!!\n"
                + "         [T][ ] read book\n"
                + "         Now you have 1 task in the list.\n"
                + "Crystal: Okkk!!! Here's your new task!!\n"
                + "         [T][ ] read book\n"
                + "         Now you have 2 tasks in the list.\n", getOutput());
    }

    /** Verifies zero-count plural and one-count singular grammar after deletions. */
    @Test
    public void showTaskDeleted_zeroAndSingularCounts_useCorrectGrammar() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");

        ui.showTaskDeleted(task, 0);
        ui.showTaskDeleted(task, 1);

        assertEquals("Crystal: All gone!!! I've removed this task:\n"
                + "         [T][ ] read book\n"
                + "         Now you have 0 tasks in the list.\n"
                + "Crystal: All gone!!! I've removed this task:\n"
                + "         [T][ ] read book\n"
                + "         Now you have 1 task in the list.\n", getOutput());
    }

    /**
     * Returns console output with platform line endings normalized.
     *
     * @return captured console output.
     */
    private String getOutput() {
        return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
