package greenchonk.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Scanner;

import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.TaskStatus;

/**
 * Handles command-line interaction between Green Chonk and the user.
 */
public class Ui {
    private static final int BANNER_WIDTH = 61;
    private static final long FRAME_DELAY_MILLIS = 100;
    private static final String DIVIDER = "_".repeat(BANNER_WIDTH);
    private static final String BANNER = """
                  ____                       ____ _                 _
                 / ___|_ __ ___  ___ _ __   / ___| |__   ___  _ __ | | __
                | |  _| '__/ _ \\/ _ \\ '_ \\ | |   | '_ \\ / _ \\| '_ \\| |/ /
                | |_| | | |  __/  __/ | | || |___| | | | (_) | | | |   <
                 \\____|_|  \\___|\\___|_| |_| \\____|_| |_|\\___/|_| |_|\\_|\\_\\
                """;

    private final Scanner scanner;
    private final PrintStream output;
    private final boolean isAnimationEnabled;
    private final boolean isCliDecorationEnabled;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this(System.in, System.out, true, true);
    }

    /**
     * Creates a UI that uses the specified input and output streams.
     *
     * @param input the stream from which commands are read.
     * @param output the stream to which messages are written.
     */
    public Ui(InputStream input, PrintStream output) {
        this(input, output, false, false);
    }

    /**
     * Creates a UI with configurable command-line presentation behavior.
     *
     * @param input the stream from which commands are read.
     * @param output the stream to which messages are written.
     * @param isAnimationEnabled whether transitional animation frames are shown.
     * @param isCliDecorationEnabled whether terminal-only dividers are shown.
     */
    private Ui(InputStream input, PrintStream output, boolean isAnimationEnabled,
            boolean isCliDecorationEnabled) {
        scanner = new Scanner(input);
        this.output = output;
        this.isAnimationEnabled = isAnimationEnabled;
        this.isCliDecorationEnabled = isCliDecorationEnabled;
    }

    /**
     * Displays Green Chonk's banner and welcome messages.
     */
    public void showWelcome() {
        output.println(DIVIDER);
        output.print(BANNER);
        output.println();
        animateMessage("Green Chonk is waking up...");
        animateMessage("Hello! I'm Green Chonk—part bear, part pear.");
        animateMessage("Hand me your tasks, and I'll keep them safe.");
        animateMessage("Type help whenever you need a paw.");
        output.println();
        output.println(DIVIDER);
    }

    /**
     * Returns whether another command can be read.
     *
     * @return true if standard input contains another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return the command as entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Green Chonk's farewell.
     */
    public void showGoodbye() {
        if (!isCliDecorationEnabled) {
            output.println("Task backpack zipped! I'm rolling off for now. See you soon!");
            return;
        }

        output.println();
        output.println(DIVIDER);
        animateMessage("Task backpack zipped! I'm rolling off for now. See you soon!");
        output.println(DIVIDER);
    }

    /**
     * Displays a user-facing command error.
     *
     * @param message the explanation of the error.
     */
    public void showError(String message) {
        printLines(
                "Oops! Green Chonk couldn't chomp that:",
                "  " + message);
    }

    /**
     * Displays an error encountered while loading saved tasks.
     *
     * @param message the explanation of the loading failure.
     */
    public void showLoadingError(String message) {
        printLines(
                "Oops! Green Chonk couldn't load saved tasks:",
                "  " + message);
    }

    /**
     * Displays the heading for tasks scheduled on a date.
     *
     * @param date the requested schedule date.
     */
    public void showScheduleHeader(LocalDate date) {
        output.println("Here are the tasks scheduled for " + date + ":");
    }

    /**
     * Displays one task with its position in the full task list.
     *
     * @param taskNumber the task's one-based position.
     * @param task the task to display.
     */
    public void showNumberedTask(int taskNumber, Task task) {
        output.println(taskNumber + "." + task);
    }

    /**
     * Reports that no deadline or event occurs on the requested date.
     *
     * @param date the requested schedule date.
     */
    public void showEmptySchedule(LocalDate date) {
        output.println("Green Chonk has no deadlines or events scheduled for " + date + ".");
    }

    /**
     * Displays a task whose completion status changed.
     *
     * @param task the updated task.
     * @param status the task's new status.
     */
    public void showTaskStatusUpdated(Task task, TaskStatus status) {
        String confirmation;
        if (status == TaskStatus.DONE) {
            confirmation = "High paw! Green Chonk marked this task as done:";
        } else {
            confirmation = "OK, Green Chonk marked this task as not done yet:";
        }
        printLines(confirmation, "  " + task);
    }

    /**
     * Displays a deleted task and the number of remaining tasks.
     *
     * @param task the deleted task.
     * @param remainingTaskCount the number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        printLines(
                "Noted. Green Chonk removed this task:",
                "  " + task,
                "Green Chonk is now carrying " + taskCountText(remainingTaskCount) + ".");
    }

    /**
     * Displays an added task and the updated task count.
     *
     * @param task the added task.
     * @param taskCount the updated number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        printLines(
                "Chomped and packed this task:",
                "  " + task,
                "Green Chonk is now carrying " + taskCountText(taskCount) + ".");
    }

    /**
     * Displays a task before and after one of its details was edited.
     *
     * @param originalTask the task before the edit.
     * @param editedTask the task after the edit.
     */
    public void showTaskEdited(Task originalTask, Task editedTask) {
        printLines(
                "Green Chonk updated this task:",
                "  Before: " + originalTask,
                "  After:  " + editedTask);
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks the tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            output.println("Green Chonk is not carrying any tasks yet.");
            return;
        }

        output.println("Here are the tasks Green Chonk is carrying:");
        for (int index = 0; index < tasks.size(); index++) {
            showNumberedTask(index + 1, tasks.get(index));
        }
    }

    /**
     * Displays the heading for tasks matching a find command.
     */
    public void showFindHeader() {
        output.println("Here are the matching tasks in your list:");
    }

    /**
     * Reports that a find command has no matching tasks.
     */
    public void showNoMatchingTasks() {
        output.println("Green Chonk found no matching tasks.");
    }

    /**
     * Displays guidance for every supported command.
     */
    public void showHelp() {
        printLines(
                "Here are the commands Green Chonk understands:",
                "  todo DESCRIPTION",
                "  deadline DESCRIPTION /by DATE",
                "  event DESCRIPTION /from START_DATE /to END_DATE",
                "  list",
                "  find KEYWORD",
                "  schedule DATE",
                "  edit TASK_NUMBER /description DESCRIPTION",
                "  edit TASK_NUMBER /by DATE",
                "  edit TASK_NUMBER /from START_DATE",
                "  edit TASK_NUMBER /to END_DATE",
                "  mark TASK_NUMBER",
                "  unmark TASK_NUMBER",
                "  delete TASK_NUMBER",
                "  help",
                "  bye",
                "Dates: yyyy-MM-dd, d/M/yyyy, or d MMM yyyy (e.g., 2026-08-28, 28/8/2026, 28 Aug 2026).");
    }

    /**
     * Returns a grammatically correct task-count phrase.
     *
     * @param taskCount the number of tasks.
     * @return the task count with a singular or plural noun.
     */
    private static String taskCountText(int taskCount) {
        String taskLabel = taskCount == 1 ? "task" : "tasks";
        return taskCount + " " + taskLabel;
    }

    /**
     * Displays each supplied line in order.
     *
     * @param lines the lines to display.
     */
    private void printLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }

    /**
     * Displays a short thinking animation before replacing it with a message.
     * Restores the interrupted status if the animation is interrupted.
     *
     * @param message the message displayed after the animation
     */
    private void animateMessage(String message) {
        if (!isAnimationEnabled) {
            output.println(message);
            return;
        }

        for (int dotCount = 1; dotCount <= 3; dotCount++) {
            output.print("\r" + center(".".repeat(dotCount)));
            output.flush();
            try {
                Thread.sleep(FRAME_DELAY_MILLIS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        output.print("\r" + center(message));
        output.println();
    }

    /**
     * Pads text so its first character begins near the banner's horizontal center.
     * Text wider than the banner is returned without leading padding.
     *
     * @param text the text to align
     * @return the text prefixed with the required spaces
     */
    private static String center(String text) {
        int leftPadding = Math.max(0, (BANNER_WIDTH - text.length()) / 2);
        return " ".repeat(leftPadding) + text;
    }
}
