package matthew.ui;

import java.util.ArrayList;

import matthew.task.Task;

/** Collects chatbot responses for display in the JavaFX interface. */
public class GuiUi extends Ui {
    private final StringBuilder output = new StringBuilder();

    /** Creates a GUI output handler. */
    public GuiUi() {
        super();
    }

    /** Returns buffered responses and clears the buffer. */
    public String consumeOutput() {
        String result = output.toString();
        output.setLength(0);
        return result;
    }

    /** Buffers Matthew's welcome message. */
    @Override
    public void showWelcome() {
        append("Hello! I'm Matthew.\nWhat can I do for you?\n\n");
    }

    /** Buffers Matthew's goodbye message. */
    @Override
    public void showGoodbye() {
        append("Goodbye! Have a nice day!\n");
    }

    /** Buffers an error message. */
    @Override
    public void showError(String message) {
        append("OOPS!!! " + message + "\n");
    }

    /** Buffers a task loading error. */
    @Override
    public void showLoadingError() {
        append("OOPS!!! I couldn't load the saved tasks.\n");
    }

    /** Buffers the complete task list. */
    @Override
    public void showTaskList(ArrayList<Task> tasks) {
        append("Here are the tasks in your list:\n");
        appendTasks(tasks);
    }

    /** Buffers confirmation that a task was added. */
    @Override
    public void showAddedTask(Task task, int taskCount) {
        append("Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.\n");
    }

    /** Buffers confirmation that a task was marked as done. */
    @Override
    public void showMarkedTask(Task task) {
        append("Nice! I've marked this task as done:\n  " + task + "\n");
    }

    /** Buffers confirmation that a task was marked as not done. */
    @Override
    public void showUnmarkedTask(Task task) {
        append("OK, I've marked this task as not done yet:\n  " + task + "\n");
    }

    /** Buffers confirmation that a task was deleted. */
    @Override
    public void showDeletedTask(Task task, int taskCount) {
        append("Noted. I've removed this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.\n");
    }

    /** Buffers tasks occurring on a specified date. */
    @Override
    public void showTasksOn(String date, ArrayList<Task> tasks) {
        append("Tasks occurring on " + date + ":\n");

        if (tasks.isEmpty()) {
            append("No deadlines or events found.\n");
        } else {
            appendTasks(tasks);
        }
    }

    /** Buffers tasks matching a keyword. */
    @Override
    public void showMatchingTasks(ArrayList<Task> tasks) {
        append("Here are the matching tasks in your list:\n");

        if (tasks.isEmpty()) {
            append("No matching tasks found.\n");
        } else {
            appendTasks(tasks);
        }
    }

    /** Adds a line to the response buffer. */
    private void append(String message) {
        output.append(message);
    }

    /** Adds numbered tasks to the response buffer. */
    private void appendTasks(ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            append((i + 1) + "." + tasks.get(i) + "\n");
        }
    }
}
