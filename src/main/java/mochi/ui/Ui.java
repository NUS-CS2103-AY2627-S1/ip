package mochi.ui;

import java.util.List;

import mochi.TaskList;
import mochi.task.Task;

/**
 * Handles all user interface operations: displaying messages and reading input.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    /**
     * Shows the welcome banner.
     */
    public void showWelcome() {
        String banner = "    __  ___           __    _\n"
                + "   /  |/  /___  _____/ /_  (_)\n"
                + "  / /|_/ / __ \\/ ___/ __ \\/ /\n"
                + " / /  / / /_/ / /__/ / / / /\n"
                + "/_/  /_/\\____/\\___/_/ /_/_/";
        System.out.println(LINE);
        System.out.println(banner);
        System.out.println("Hello! I'm Mochi.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Shows the divider line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Shows the goodbye message.
     */
    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Shows an error message.
     */
    public void showError(String message) {
        System.out.println(LINE);
        System.out.println(" OOPS!!! " + message);
        System.out.println(LINE);
    }

    /**
     * Shows the full task list.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println(LINE);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        System.out.println(LINE);
    }

    /**
     * Shows the confirmation after marking a task as done.
     */
    public void showMarked(Task task) {
        System.out.println(LINE);
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
        System.out.println(LINE);
    }

    /**
     * Shows the confirmation after marking a task as not done.
     */
    public void showUnmarked(Task task) {
        System.out.println(LINE);
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
        System.out.println(LINE);
    }

    /**
     * Shows the confirmation after deleting a task.
     */
    public void showDeleted(Task task, int remainingCount) {
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + remainingCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Shows the confirmation after adding a task.
     */
    public void showAdded(Task task, int count) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + count + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Shows tasks that match a search keyword.
     *
     * @param matching the list of matching tasks
     */
    public void showMatchingTasks(List<Task> matching) {
        System.out.println(LINE);
        System.out.println(" Here are the matching tasks in your list:");
        for (int i = 0; i < matching.size(); i++) {
            System.out.println(" " + (i + 1) + "." + matching.get(i));
        }
        System.out.println(LINE);
    }
}
