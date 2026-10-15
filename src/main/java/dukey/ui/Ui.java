package dukey.ui;

import java.io.PrintStream;
import java.time.LocalDate;

import dukey.task.Task;
import dukey.task.TaskList;

/**
 * Handles all interactions with the user.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BOT_NAME = "Dukey";
    private final PrintStream output;

    /**
     * Creates a UI that writes to the standard output.
     */
    public Ui() {
        this(System.out);
    }

    /**
     * Creates a UI that writes to the given output stream.
     *
     * @param output Output stream used for user-facing messages.
     */
    public Ui(PrintStream output) {
        this.output = output;
    }

    /**
     * Shows the welcome banner and greeting.
     */
    public void showWelcome() {
        String banner = " ____        _              \n"
                + "|  _ \\ _   _| | _____ _   _ \n"
                + "| | | | | | | |/ / _ \\ | | |\n"
                + "| |_| | |_| |   <  __/ |_| |\n"
                + "|____/ \\__,_|_|\\_\\___|\\__, |\n"
                + "                       |___/ \n";
        showLine();
        this.output.println(banner);
        this.output.println("Hello! I'm " + BOT_NAME + ", your calm task librarian.");
        this.output.println("What shall we shelve, sort, or settle today?");
        showLine();
    }

    /**
     * Shows the divider line.
     */
    public void showLine() {
        this.output.println(LINE);
    }

    /**
     * Shows the goodbye message.
     */
    public void showBye() {
        this.output.println("Bye. The shelves are tidy for now.");
    }

    /**
     * Shows an error message for data loading failures.
     */
    public void showLoadingError() {
        this.output.println("File not found");
    }

    /**
     * Shows a user-facing error message.
     *
     * @param message Error details to show.
     */
    public void showError(String message) {
        this.output.println(" OOPS!!! " + message + " Let's keep the shelves neat.");
    }

    /**
     * Shows all tasks in the task list.
     *
     * @param tasks Tasks to show.
     */
    public void showList(TaskList tasks) {
        this.output.println("Here are the tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            this.output.println(String.format("%d.%s", i, tasks.get(i)));
        }
    }

    /**
     * Shows tasks that match a find command.
     *
     * @param tasks Matching tasks to show.
     */
    public void showMatchingTasks(TaskList tasks) {
        this.output.println("Here are the matching tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            this.output.println(String.format("%d.%s", i, tasks.get(i)));
        }
    }

    /**
     * Shows deadlines and events that occur on the given date.
     *
     * @param tasks Tasks to search.
     * @param date Date to show tasks for.
     */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        boolean hasMatchingTask = false;

        this.output.println("Here are the deadlines and events on that date:");
        for (int i = 1; i <= tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                this.output.println(String.format("%d.%s", i, task));
                hasMatchingTask = true;
            }
        }

        if (!hasMatchingTask) {
            this.output.println("There are no deadlines or events on that date.");
        }
    }

    /**
     * Shows useful statistics about the task list.
     *
     * @param tasks Tasks to summarize.
     * @param date Date used to define the current calendar week and month.
     */
    public void showStatistics(TaskList tasks, LocalDate date) {
        this.output.println("Here are your task statistics:");
        this.output.println(String.format("Total tasks: %d", tasks.size()));
        this.output.println(String.format("Completed tasks: %d", tasks.countCompletedTasks()));
        this.output.println(String.format("Pending tasks: %d", tasks.countPendingTasks()));
        this.output.println(String.format("Completed in the current calendar week: %d",
                tasks.countCompletedInCalendarWeek(date)));
        this.output.println(String.format("Completed in the current calendar month: %d",
                tasks.countCompletedInCalendarMonth(date)));
    }

    /**
     * Shows the current number of tasks.
     *
     * @param tasks Tasks to count.
     */
    public void showTaskCount(TaskList tasks) {
        this.output.println(String.format("Now you have %d tasks in the list.", tasks.size()));
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task Added task.
     * @param tasks Updated task list.
     */
    public void showTaskAdded(Task task, TaskList tasks) {
        this.output.println("Filed neatly. I've added this task:");
        this.output.println("  " + task);
        showTaskCount(tasks);
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task Marked task.
     */
    public void showTaskMarked(Task task) {
        this.output.println("Stamped and settled. I've marked this task as done:");
        this.output.println("  " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task Unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        this.output.println("Back on the shelf. I've marked this task as not done yet:");
        this.output.println("  " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param tasks Updated task list.
     */
    public void showTaskDeleted(Task task, TaskList tasks) {
        this.output.println("Removed from the shelf. I've removed this task:");
        this.output.println("  " + task);
        showTaskCount(tasks);
    }
}
