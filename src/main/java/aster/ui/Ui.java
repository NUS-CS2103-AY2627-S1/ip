package aster.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.IntStream;

import aster.task.Task;
import aster.task.TaskList;

/**
 * Handles all command-line input and output for the Aster application.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER =
            "    _    ____ _____ _____ ____\n"
                    + "   / \\  / ___|_   _| ____|  _ \\\n"
                    + "  / _ \\ \\___ \\ | | |  _| | |_) |\n"
                    + " / ___ \\ ___) || | | |___|  _ <\n"
                    + "/_/   \\_\\____/ |_| |_____|_| \\_\\\n";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final Scanner input;

    /**
     * Creates the CLI user interface.
     */
    public Ui() {
        input = new Scanner(System.in);
    }

    /**
     * Shows the startup greeting.
     */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println("Hello! I'm Aster, your stargazing task companion.");
        System.out.println("Let's plot a clear course through your day.");
        showLine();
    }

    /**
     * Shows the goodbye message.
     */
    public void showGoodbye() {
        System.out.println("Until next time - keep your goals in orbit!");
    }

    /**
     * Shows the shared divider line.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Returns whether there is another command line available in input.
     *
     * @return true when another command can be read
     */
    public boolean hasNextCommand() {
        return input.hasNextLine();
    }

    /**
     * Reads one full command line from input.
     *
     * @return full command line
     */
    public String readCommand() {
        return input.nextLine();
    }

    /**
     * Shows a loading error and a fallback note.
     *
     * @param reason low-level reason for the loading failure
     */
    public void showLoadingError(String reason) {
        System.out.println("Warning: couldn't load saved tasks (" + reason + ").");
        System.out.println("Starting with an empty task list.");
    }

    /**
     * Shows a formatted user-facing error message.
     *
     * @param errorMessage full error message
     */
    public void showError(String errorMessage) {
        System.out.println(errorMessage);
    }

    /**
     * Shows a standard task-added response.
     *
     * @param task added task
     * @param taskCount new number of tasks in the list
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows all tasks in list order.
     *
     * @param taskList task list to display
     */
    public void showTasks(TaskList taskList) {
        System.out.println("Here are the tasks in your list:");
        List<Task> tasks = taskList.getAll();
        IntStream.range(0, tasks.size())
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .forEach(System.out::println);
    }

    /**
     * Shows tasks that occur on a date.
     *
     * @param targetDate date used for filtering
     * @param matchingTasks tasks on the date
     */
    public void showTasksOnDate(LocalDate targetDate, List<Task> matchingTasks) {
        String formattedDate = targetDate.format(DISPLAY_DATE_FORMAT);
        if (matchingTasks.isEmpty()) {
            System.out.println("No tasks are scheduled for " + formattedDate + ".");
            return;
        }

        System.out.println("Here are the tasks on " + formattedDate + ":");
        IntStream.range(0, matchingTasks.size())
                .mapToObj(index -> (index + 1) + "." + matchingTasks.get(index))
                .forEach(System.out::println);
    }

    /**
     * Shows tasks whose description matches a find keyword.
     *
     * @param matchingTasks tasks matching the keyword
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println("Here are the matching tasks in your list:");
        IntStream.range(0, matchingTasks.size())
                .mapToObj(index -> (index + 1) + "." + matchingTasks.get(index))
                .forEach(System.out::println);
    }

    /**
     * Shows that a task was marked complete.
     *
     * @param task marked task
     */
    public void showTaskMarkedDone(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Shows that a task was marked incomplete.
     *
     * @param task unmarked task
     */
    public void showTaskMarkedNotDone(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Shows that a task was deleted.
     *
     * @param removedTask deleted task
     * @param remainingTaskCount task count after deletion
     */
    public void showTaskDeleted(Task removedTask, int remainingTaskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + remainingTaskCount + " tasks in the list.");
    }

    /**
     * Shows that a tag was added to a task.
     *
     * @param task tagged task
     */
    public void showTaskTagged(Task task) {
        System.out.println("Got it. I've tagged this task:");
        System.out.println("  " + task);
    }
}
