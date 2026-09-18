package student.project.bob.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import student.project.bob.exception.BobException;
import student.project.bob.model.Task;
import student.project.bob.util.DateTimeParser;

/**
 * Handles console input and output for Bob.
 */
public class Ui {
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private static final String BANNER = " ____        _     \n"
            + "| __ )  ___ | |__  \n"
            + "|  _ \\ / _ \\| '_ \\ \n"
            + "| |_) | (_) | |_) |\n"
            + "|____/ \\___/|_.__/ \n";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Bob's welcome message.
     */
    public void showWelcome() {
        System.out.println(HORIZONTAL_LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Bob.\n" + "What can I do for you?");
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Checks whether another command is available from standard input.
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command from standard input.
     *
     * @return the next user command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays Bob's goodbye message.
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        showSeparator();
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:\n");
        showNumberedTasks(tasks);
        showSeparator();
    }

    /**
     * Displays tasks scheduled in the upcoming date range.
     *
     * @param tasks tasks selected for display
     * @param days number of days in the date range
     */
    public void showUpcomingTasks(List<Task> tasks, int days) {
        System.out.printf("Here are the upcoming tasks in the next %d days:%n%n", days);
        showNumberedTasks(tasks);
        showSeparator();
    }

    /**
     * Displays deadlines and events scheduled on a date.
     *
     * @param tasks tasks selected for display
     * @param date date being displayed
     */
    public void showTasksOnDate(List<Task> tasks, LocalDate date) {
        System.out.printf("Here are the tasks on %s:%n%n", DateTimeParser.format(date));
        showNumberedTasks(tasks);
        showSeparator();
    }

    /**
     * Displays incomplete deadlines that are past their due date or time.
     *
     * @param tasks overdue tasks to display
     */
    public void showOverdueTasks(List<Task> tasks) {
        System.out.println("Here are your overdue tasks:\n");
        showNumberedTasks(tasks);
        showSeparator();
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param tasks tasks selected by the find command
     */
    public void showMatchingTasks(List<Task> tasks) {
        System.out.println("Here are the matching tasks in your list:\n");
        showNumberedTasks(tasks);
        showSeparator();
    }

    /**
     * Displays a confirmation after adding a task.
     *
     * @param task task that was added
     * @param taskCount updated number of tasks
     */
    public void showAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:\n");
        System.out.println(task);
        System.out.printf("Now you have %d tasks in the list.%n", taskCount);
        showSeparator();
    }

    /**
     * Displays a confirmation after marking a task as done.
     *
     * @param task task that was marked as done
     */
    public void showMarkedTask(Task task) {
        System.out.println("Nice! I've marked this task as done:\n" + "  [X] " + task.getDescription());
        showSeparator();
    }

    /**
     * Displays a confirmation before marking a task as not done.
     *
     * @param task task that will be marked as not done
     */
    public void showUnmarkedTask(Task task) {
        System.out.println("OK, I've marked this task as not done yet:\n" + "  [ ] " + task.getDescription());
    }

    /**
     * Displays a confirmation after deleting a task.
     *
     * @param task task that was removed
     * @param taskCount updated number of tasks
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:\n" + "    " + task);
        System.out.printf("Now you have %d tasks in the list.%n", taskCount);
        showSeparator();
    }

    /**
     * Displays the result of undoing the most recent state-changing command.
     *
     * @param message explanation of the undone command
     */
    public void showUndoResult(String message) {
        System.out.println(message);
        showSeparator();
    }

    /**
     * Displays an input error without terminating the command loop.
     *
     * @param exception input error to explain
     */
    public void showError(BobException exception) {
        System.out.println("Oops! " + exception.getMessage());
        showSeparator();
    }

    /**
     * Displays a storage error without terminating Bob.
     *
     * @param message user-facing explanation of the storage problem
     */
    public void showStorageError(String message) {
        System.out.println("Oops! " + message);
        showSeparator();
    }

    /**
     * Displays the separator used between Bob's responses.
     */
    public void showSeparator() {
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays tasks with one-based list numbers.
     *
     * @param tasks tasks to display
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("%d.%s%n", i + 1, tasks.get(i));
        }
    }
}
