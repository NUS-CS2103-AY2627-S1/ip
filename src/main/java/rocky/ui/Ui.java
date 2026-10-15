package rocky.ui;

import java.util.List;
import java.util.Scanner;

import rocky.TaskList;
import rocky.parser.Parser;
import rocky.task.Task;

/**
 * Handles Rocky's console input and output.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner scanner;

    /**
     * Creates a console UI connected to standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays Rocky's welcome message.
     */
    public void showWelcome() {
        String banner = " ____             _\n"
                + "|  _ \\ ___   ___| | ___   _\n"
                + "| |_) / _ \\ / __| |/ / | | |\n"
                + "|  _ < (_) | (__|   <| |_| |\n"
                + "|_| \\_\\___/ \\___|_|\\_\\__, |\n"
                + "                         |___/\n";
        showDivider();
        System.out.println(banner);
        System.out.println("Hello! I Rocky.");
        System.out.println("Amaze, what a special human being! What rocky do for you?");
        showDivider();
    }

    /** Returns whether another console command is available.
     *
     * @return {@code true} when another line can be read.
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /** Reads the next console command.
     *
     * @return the next line entered by the user.
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /** Displays the current task list.
     *
     * @param tasks the task list to display.
     */
    public void showTasks(TaskList tasks) {
        System.out.println("Rocky remember you have these tasks");
        showDivider();
        if (tasks.isEmpty()) {
            System.out.println("Rocky don't see anything!");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
        showDivider();
    }

    /** Displays the confirmation message and the sorted task list.
     *
     * @param tasks the sorted task list to display.
     */
    public void showSortedTasks(TaskList tasks) {
        System.out.println("Rocky help you sort the task from earliest to latest!");
        showTasks(tasks);
    }

    /** Displays tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks the tasks returned by a search.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showDivider();
        System.out.println("Here are the matching tasks in your list:");
        if (matchingTasks.isEmpty()) {
            System.out.println("Rocky don't see any matching tasks!");
        } else {
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println((i + 1) + ". " + matchingTasks.get(i));
            }
        }
        showDivider();
    }

    /** Displays feedback after a task is added.
     *
     * @param task the newly added task.
     * @param taskCount the number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showDivider();
        System.out.println("Amaze! Rocky add this to task...:");
        System.out.println(task);
        System.out.println("Rocky see " + taskCount + " tasks in the list.");
        showDivider();
    }

    /** Displays feedback after a task is marked or unmarked.
     *
     * @param task the task whose status changed.
     * @param completed whether the task is now complete.
     */
    public void showTaskStatus(Task task, boolean completed) {
        showDivider();
        System.out.println(completed ? "Nice! Rocky marked this task as done:"
                : "Oh No! Rocky marked this task as not done yet:");
        System.out.println(task);
        showDivider();
    }

    /** Displays feedback before deleting a task.
     *
     * @param task the task about to be deleted.
     */
    public void showTaskDeleted(Task task) {
        showDivider();
        System.out.println("Rocky will remove that annoying task for you!:");
        System.out.println(task);
        showDivider();
    }

    /** Displays an invalid task-number message.
     *
     * @param command the command whose task number was invalid.
     */
    public void showInvalidTaskNumber(String command) {
        System.out.println("Please provide a task number, for example: " + command + " 2");
    }

    /**
     * Displays a missing-task message.
     */
    public void showTaskNotFound() {
        System.out.println("Rocky cannot find that task number.");
    }

    /** Displays a parser error using the appropriate divider style.
     *
     * @param command the parsed command containing the error message.
     */
    public void showError(Parser.Command command) {
        if (command.showWithDivider()) {
            showDivider();
        }
        System.out.println(command.getMessage());
        if (command.showWithDivider()) {
            showDivider();
        }
    }

    /**
     * Displays Rocky's goodbye message.
     */
    public void showBye() {
        System.out.println("Bye. We meet again soon!");
        showDivider();
    }

    private void showDivider() {
        System.out.println(DIVIDER);
    }
}
