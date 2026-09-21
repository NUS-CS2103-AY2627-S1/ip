package janet.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import janet.task.Task;

/**
 * Handles Janet's console input and output.
 */
public class Ui {
    /** Divider printed between Janet's responses. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Reads commands entered through the console. */
    private final Scanner scanner = new Scanner(System.in);

    /** Receives Janet's displayed messages. */
    private final PrintStream output;

    /**
     * Creates a console user interface.
     */
    public Ui() {
        this(System.out);
    }

    /**
     * Creates a user interface that writes to a specific output stream.
     *
     * @param output destination for Janet's messages.
     */
    public Ui(PrintStream output) {
        this.output = output;
    }

    /**
     * Shows Janet's welcome banner.
     */
    public void showWelcome() {
        showLines(
                DIVIDER,
                "     _                  _",
                "    | |                | |",
                "    | | __ _ _ __   ___| |_",
                " _  | |/ _` | '_ \\ / _ \\ __|",
                "| |_| | (_| | | | |  __/ |_",
                " \\___/ \\__,_|_| |_|\\___|\\__|",
                DIVIDER,
                "Hi there! I'm Janet, your cheerful task assistant.",
                "Fun fact: not a robot. What can I help you organize?",
                DIVIDER);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return whether a command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one complete command from standard input.
     *
     * @return the user's command
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the list of tasks.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            output.println(" Your task list is empty. Everything is wonderfully under control.");
            return;
        }
        output.println(" Absolutely! Here is everything on your list:");
        showTasks(tasks);
    }

    /**
     * Shows tasks matching a user-entered keyword.
     *
     * @param tasks matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            output.println(" I checked the whole list. No matching tasks found.");
            return;
        }
        output.println(" I found these matching tasks:");
        showTasks(tasks);
    }

    /**
     * Shows tasks as a numbered list.
     *
     * @param tasks tasks to display.
     */
    private void showTasks(List<Task> tasks) {
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            output.println(" " + (index + 1) + "." + task.getTypeIcon()
                    + "[" + task.getStatusIcon() + "] " + task.getDescription());
        }
    }

    /**
     * Shows confirmation that a task has been added.
     *
     * @param task the task added.
     * @param taskCount total number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLines(
                " Done and done! I added this task:",
                "   " + task.getTypeIcon() + "[ ] " + task.getDescription(),
                " You now have " + formatTaskCount(taskCount) + ".");
    }

    /**
     * Shows confirmation that a task has been marked as done.
     *
     * @param task the completed task.
     */
    public void showTaskMarked(Task task) {
        output.println(" Excellent! This task is now complete:");
        showTask(task);
    }

    /**
     * Shows confirmation that a task has been marked as incomplete.
     *
     * @param task the incomplete task.
     */
    public void showTaskUnmarked(Task task) {
        output.println(" No problem! This task is back in progress:");
        showTask(task);
    }

    /**
     * Shows confirmation that a task has been removed.
     *
     * @param task the removed task.
     * @param taskCount total number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(" Done! I removed this task:");
        showTask(task);
        output.println(" You now have " + formatTaskCount(taskCount) + ".");
    }

    /**
     * Shows confirmation that multiple tasks have been removed.
     *
     * @param tasks the removed tasks in the user's requested order.
     * @param taskCount total number of tasks remaining.
     */
    public void showTasksDeleted(List<Task> tasks, int taskCount) {
        output.println(" Done! I removed these tasks:");
        for (Task task : tasks) {
            showTask(task);
        }
        output.println(" You now have " + formatTaskCount(taskCount) + ".");
    }

    /**
     * Shows a user-facing error message.
     *
     * @param message the error message.
     */
    public void showError(String message) {
        output.println(" " + message);
    }

    /**
     * Shows a non-error warning message.
     *
     * @param message the warning message.
     */
    public void showWarning(String message) {
        output.println(" " + message);
    }

    /**
     * Shows Janet's goodbye message.
     */
    public void showGoodbye() {
        showLines(DIVIDER, " All set! I'll be right here if you need me. Bye!");
    }

    /**
     * Shows the standard divider line.
     */
    public void showLine() {
        output.println(DIVIDER);
    }

    /**
     * Shows one task in Janet's indented response format.
     *
     * @param task the task to display.
     */
    private void showTask(Task task) {
        output.println("   " + task.getTypeIcon() + "[" + task.getStatusIcon() + "] "
                + task.getDescription());
    }

    /**
     * Formats a task count with the correct singular or plural noun.
     *
     * @param taskCount number of tasks.
     * @return readable task count
     */
    private String formatTaskCount(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        return taskCount + " " + noun;
    }

    /**
     * Shows any number of lines in their supplied order.
     *
     * @param lines lines to display.
     */
    private void showLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
