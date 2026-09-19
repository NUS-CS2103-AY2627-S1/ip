package carl.ui;

import java.util.List;

import carl.task.Task;

/**
 * Represents the user interface for the application.
 * Handles displaying messages to the user.
 */
public class Ui {
    private String buildMessage(String... inputs) {
        StringBuilder sb = new StringBuilder();

        for (String str : inputs) {
            sb.append(str).append("\n");
        }

        return sb.toString();
    }

    /**
     * Displays the welcome message to the user.
     *
     * @return Carl's welcome message.
     */
    public String showWelcome() {
        return "Carl online. What are we getting done?";
    }

    /**
     * Displays the goodbye message.
     *
     * @return Carl's goodbye message.
     */
    public String showGoodbye() {
        return "Shift complete. See you next time.";
    }

    /**
     * Displays a message confirming a task has been added.
     *
     * @param task  The task that was added.
     * @param count The total number of tasks in the list.
     * @return confirmation containing the task and updated count.
     */
    public String showAddTask(Task task, int count) {
        return buildMessage("Logged. I’ll keep that on the radar.",
                "  " + task, showTasksLeft(count));
    }

    private String showTasksLeft(int count) {
        return "Now you have " + count + " tasks in the list.";
    }

    /**
     * Displays a message confirming a task has been marked as done.
     *
     * @param task The task that was marked as done.
     * @return confirmation containing the completed task.
     */
    public String showMarkTaskAsDone(Task task) {
        return "Checked off. One less thing to worry about.\n  " + task;
    }

    /**
     * Displays a message confirming a task has been unmarked.
     *
     * @param task The task that was unmarked.
     * @return confirmation containing the reopened task.
     */
    public String showUnmarkTask(Task task) {
        return "Successfully unmarked this task as not done! \n  " + task;
    }

    /**
     * Displays a message confirming a task has been deleted.
     *
     * @param task  The task that was deleted.
     * @param count The remaining number of tasks in the list.
     * @return confirmation containing the deleted task and remaining count.
     */
    public String showDeleteTask(Task task, int count) {
        return "Successfully removed this task! \n  " + task + "\n" + showTasksLeft(count);
    }

    /**
     * Displays the list of tasks to the user.
     *
     * @param tasks The list of tasks to display.
     * @return numbered task list or the empty-list message.
     */
    public String showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Radar clear — no tasks here.";
        }

        String[] taskStrings = new String[tasks.size()];

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            taskStrings[i] = (i + 1) + ". " + task;
        }

        return buildMessage(taskStrings);
    }
}
