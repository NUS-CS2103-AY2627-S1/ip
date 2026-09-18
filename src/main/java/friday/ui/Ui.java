package friday.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import friday.model.Task;
import friday.model.TaskList;

/**
 * Handles Friday's console input and user-facing output.
 *
 * Keeping console details here lets {@link Friday} coordinate application
 * flow without knowing how messages are formatted or how commands are read.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String NAME = "FRIDAY";
    private static final String BANNER = """
             _____    _     _
            |  ___| _(_) __| | __ _ _   _
            | |_ | '__| |/ _` |/ _` | | | |
            |  _|| |  | | (_| | (_| | |_| |
            |_|  |_|  |_|\\__,_|\\__,_|\\__, |
                                     |___/
            """;

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI backed by standard input and output.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI with explicit input and output streams.
     *
     * @param scanner Source used for console input.
     * @param output Destination used for user-facing output.
     */
    public Ui(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = output;
    }

    /**
     * Prints the greeting shown at the start of every session.
     */
    public void showGreeting() {
        output.println(LINE);
        output.println(BANNER);
        output.println("Good day, Mr Stark. I am " + NAME + ".");
        output.println("What can I assist you with?");
        output.println(LINE);
    }

    /**
     * Checks whether another command is available from standard input.
     *
     * @return true when another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command from standard input.
     *
     * @return The next input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints all tasks currently stored in the list.
     *
     * @param tasks The task list to display.
     */
    public void showTaskList(TaskList tasks) {
        output.println(LINE);
        output.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(String.format(" %d. %s", i + 1, tasks.get(i)));
        }
        output.println(LINE);
    }

    /**
     * Prints all tasks currently stored in the archive.
     *
     * @param archivedTasks Archived tasks to display.
     */
    public void showArchivedTaskList(TaskList archivedTasks) {
        output.println(LINE);
        if (archivedTasks.size() == 0) {
            output.println(" There are no archived tasks.");
        } else {
            output.println(" Here are your archived tasks:");
            for (int i = 0; i < archivedTasks.size(); i++) {
                output.println(String.format(" %d. %s", i + 1, archivedTasks.get(i)));
            }
        }
        output.println(LINE);
    }

    /**
     * Prints tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks Tasks selected by the find command.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        output.println(LINE);
        output.println(" Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.println(String.format(" %d. %s", i + 1, matchingTasks.get(i)));
        }
        if (matchingTasks.isEmpty()) {
            output.println(" No matching tasks found.");
        }
        output.println(LINE);
    }

    /**
     * Prints the standard response after a task is added successfully.
     *
     * @param task The task that was added.
     * @param taskCount The total number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(LINE);
        output.println(" Understood. I've added this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
        output.println(LINE);
    }

    /**
     * Prints the standard response after a task is deleted successfully.
     *
     * @param task The task that was removed.
     * @param taskCount The number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(LINE);
        output.println(" Confirmed. I've removed this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
        output.println(LINE);
    }

    /**
     * Prints confirmation that one task was moved to the archive.
     *
     * @param task Task moved to the archive.
     */
    public void showTaskArchived(Task task) {
        output.println(LINE);
        output.println(" Confirmed. I've archived this task:");
        output.println(" " + task);
        output.println(LINE);
    }

    /**
     * Prints confirmation that all active tasks were archived.
     *
     * @param taskCount Number of archived tasks.
     */
    public void showTasksArchived(int taskCount) {
        output.println(LINE);
        output.println(String.format(" Archived %d %s.", taskCount, taskCount == 1 ? "task" : "tasks"));
        output.println(" Your task list is now empty.");
        output.println(LINE);
    }

    /**
     * Prints confirmation that one archived task was restored.
     *
     * @param task Task restored to the active list.
     */
    public void showTaskUnarchived(Task task) {
        output.println(LINE);
        output.println(" Confirmed. I've restored this task:");
        output.println(" " + task);
        output.println(LINE);
    }

    /**
     * Prints confirmation that all archived tasks were restored.
     *
     * @param taskCount Number of restored tasks.
     */
    public void showTasksUnarchived(int taskCount) {
        output.println(LINE);
        output.println(String.format(" Restored %d %s.", taskCount, taskCount == 1 ? "task" : "tasks"));
        output.println(" Your archive is now empty.");
        output.println(LINE);
    }

    /**
     * Prints the response for an archive-all request with no active tasks.
     */
    public void showNoTasksToArchive() {
        output.println(LINE);
        output.println(" There are no tasks to archive.");
        output.println(LINE);
    }

    /**
     * Prints the response for an unarchive-all request with no archived tasks.
     */
    public void showNoArchivedTasksToRestore() {
        output.println(LINE);
        output.println(" There are no archived tasks to restore.");
        output.println(LINE);
    }

    /**
     * Prints the response after changing a task's completion status.
     *
     * @param task The task whose status was changed.
     * @param markDone Whether the task was marked done or unmarked.
     */
    public void showTaskStatus(Task task, boolean markDone) {
        output.println(LINE);
        if (markDone) {
            output.println(" Confirmed. I've marked this task as done:");
        } else {
            output.println(" Noted. I've marked this task as not done yet:");
        }
        output.println(" " + task);
        output.println(LINE);
    }

    /**
     * Prints a user-friendly error message.
     *
     * @param message The message to display.
     */
    public void showError(String message) {
        output.println(LINE);
        output.println(" That presents a complication: " + message);
        output.println(LINE);
    }

    /**
     * Prints the farewell message before the chatbot exits.
     */
    public void showExitMessage() {
        output.println(LINE);
        output.println(" Standing by.");
        output.println(LINE);
    }

    /**
     * Releases the console input resource.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Flushes output so callers using a buffered stream can read the complete response.
     */
    public void flush() {
        output.flush();
    }
}
