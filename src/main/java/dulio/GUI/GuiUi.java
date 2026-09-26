package dulio.gui;

import dulio.task.Task;
import dulio.ui.Ui;

/**
 * Collects Dulio responses for display in the JavaFX interface.
 */
public class GuiUi extends Ui {
    private StringBuilder response;

    /**
     * Creates an empty GUI response collector.
     */
    public GuiUi() {
        response = new StringBuilder();
    }

    public String getResponse() {
        return response.toString();
    }

    @Override
    public void showAdded(Task task, int taskCount) {
        append("Got it. I've added this task:\n");
        appendTask(task);
        append("Now you have " + taskCount + " tasks in the list.\n");
    }

    @Override
    public void showDeleted(Task task, int taskCount) {
        append("Noted. I've removed this task:\n");
        appendTask(task);
        append("Now you have " + taskCount + " tasks in the list.\n");
    }

    @Override
    public void showFind(String matchingTasks) {
        append("Here are the matching tasks in your list:\n");
        append(matchingTasks.isEmpty() ? "(no matching tasks)\n" : matchingTasks);
    }

    @Override
    public void showInvalidIndex() {
        append("Invalid task index\n");
    }

    @Override
    public void showList(String listed) {
        append("Here are the tasks in your list:\n");
        append(listed.isEmpty() ? "(no tasks)\n" : listed);
    }

    @Override
    public void showMarked(Task task, boolean marked) {
        append(marked
            ? "Nice! I've marked this task as done:\n"
            : "OK, I've marked this task as not done yet:\n");
        appendTask(task);
    }

    @Override
    public void showError(String message) {
        append(message + "\n");
    }

    @Override
    public void showGoodbye() {
        append("Bye. Hope to see you again soon!\n");
    }

    private void appendTask(Task task) {
        append("[" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] " + task + "\n");
    }

    private void append(String message) {
        response.append(message);
    }
}
