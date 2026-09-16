package della.ui;

import java.util.ArrayList;

import della.task.Task;

/**
 * Displays messages and task information to the user.
 */
public class UI {
    private UI() {
    }

    /**
     * Displays the welcome banner and greeting.
     */
    public static String showWelcome() {
        return "Hi! I'm Della :))\nHow can I help you?";
    }

    /**
     * Displays the farewell message.
     */
    public static String showFarewell() {
        return "Byee! Rest well!";
    }

    /**
     * Returns an error message.
     *
     * @param errorMsg Error message to display.
     */
    public static String showError(String errorMsg) {
        return errorMsg;
    }

    /**
     * Displays the tasks in a numbered list.
     *
     * @param taskList Tasks to display.
     */
    public static String showTasks(ArrayList<Task> taskList) {
        if (taskList.isEmpty()) {
            return "Wow you have no tasks!";
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            output.append(String.format("%d. %s\n", i + 1, taskList.get(i)));
        }
        return output.toString().stripTrailing();
    }

    /**
     * Displays confirmation that a task has been marked as complete.
     *
     * @param task Task that was marked as complete.
     */
    public static String showMarkedTask(Task task) {
        return "Nice! I have marked this task as done:\n" + task;
    }

    /**
     * Displays confirmation that a task has been marked as incomplete.
     *
     * @param task Task that was marked as incomplete.
     */
    public static String showUnmarkedTask(Task task) {
        return "OK, I've marked this task as not done yet:\n" + task;
    }

    /**
     * Displays confirmation that a task has been added.
     *
     * @param newTask Task that was added.
     * @param numTasks Total number of tasks after the addition.
     */
    public static String showAddedTask(Task newTask, int numTasks) {
        return String.format(
                "Got it. I've added this task:\n%s\now you have %d tasks in the list.",
                newTask, numTasks);
    }

    /**
     * Displays confirmation that a task has been deleted.
     *
     * @param task Task that was deleted.
     * @param numTasks Total number of tasks after the deletion.
     */
    public static String showDeletedTask(Task task, int numTasks) {
        return String.format(
                "Noted. I've removed this task:\n%s\nNow you have %d tasks in the list.",
                task, numTasks);
    }

    /**
     * Displays the search result in a numbered list.
     *
     * @param taskList Tasks to display.
     */
    public static String showSearchResult(ArrayList<Task> taskList) {
        if (taskList.isEmpty()) {
            return "No results found! Sorry!";
        }
        return UI.showTasks(taskList);
    }
}
