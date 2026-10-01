package bo.ui;

import java.util.List;
import java.util.Scanner;

import bo.task.Task;

/**
 * Handles console input and output for Bo.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reports whether another command can be read.
     *
     * @return True if another input line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command from the user.
     *
     * @return The entered command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays Bo's welcome message.
     *
     * @param banner The banner to display.
     */
    public void showWelcome(String banner) {
        showSeparator();
        System.out.println(banner);
        System.out.println("Hello! I'm Bo.");
        System.out.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Displays an error message for an invalid command.
     *
     * @param message The error message.
     */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /**
     * Explains that saved tasks could not be loaded and Bo will start empty.
     */
    public void showLoadingError() {
        System.out.println(" I could not load your saved tasks, so Bo is starting with an empty list.");
    }

    /**
     * Displays Bo's farewell message.
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task The added task.
     * @param taskCount The current number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays every task in the task list.
     *
     * @param tasks The tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays selected deadlines using their original task numbers.
     *
     * @param taskIndexes The indexes of the deadlines to display.
     * @param tasks The full task list.
     * @param heading The heading to print before the deadlines.
     */
    public void showDeadlines(List<Integer> taskIndexes, List<Task> tasks, String heading) {
        System.out.println(heading);
        if (taskIndexes.isEmpty()) {
            System.out.println(" No matching deadlines found.");
            return;
        }

        showIndexedTasks(taskIndexes, tasks);
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task The marked task.
     */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task The unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task The removed task.
     * @param taskCount The current number of tasks.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a deadline was snoozed.
     *
     * @param task The snoozed deadline.
     */
    public void showTaskSnoozed(Task task) {
        System.out.println(" I've snoozed this deadline:");
        System.out.println("   " + task);
    }

    /**
     * Displays confirmation that a deadline was rescheduled.
     *
     * @param task The rescheduled deadline.
     */
    public void showTaskRescheduled(Task task) {
        System.out.println(" I've rescheduled this deadline:");
        System.out.println("   " + task);
    }

    /**
     * Prints a horizontal line to separate sections of Bo's messages.
     */
    public void showSeparator() {
        System.out.println("----------------------------------------");
    }

    /**
     * Displays tasks that matched a search, using their original task numbers.
     *
     * @param taskIndexes the indexes of the matching tasks
     * @param tasks the full task list
     */
    public void showMatchingTasks(List<Integer> taskIndexes, List<Task> tasks) {
        System.out.println(" Here are the matching tasks in your list:");
        if (taskIndexes.isEmpty()) {
            System.out.println(" No matching tasks found.");
            return;
        }
        showIndexedTasks(taskIndexes, tasks);
    }

    /**
     * Displays tasks using their zero-based indexes and one-based task numbers.
     *
     * @param taskIndexes The indexes of the tasks to display.
     * @param tasks The full task list.
     */
    private void showIndexedTasks(List<Integer> taskIndexes, List<Task> tasks) {
        for (int taskIndex : taskIndexes) {
            System.out.println(" " + (taskIndex + 1) + "." + tasks.get(taskIndex));
        }
    }
}
