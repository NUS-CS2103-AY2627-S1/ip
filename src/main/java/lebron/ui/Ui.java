package lebron.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lebron.task.Task;
import lebron.task.TaskList;

/**
 * Builds LeBron's response messages and reads user input.
 * Each {@code showXxx} method returns the message as a String instead of printing it,
 * so the same message-building logic can be reused by both the CLI and the GUI.
 */
public class Ui {

    private Scanner scanner;

    /**
     * Constructs a Ui with a scanner.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Builds LeBron's initial banner message.
     *
     * @return The banner message.
     */
    public String showBanner() {
        return "What's up, I am LeBron.\nCan the king assist you in any way?";
    }

    /**
     * Builds a plain message.
     *
     * @param message The message to be delivered.
     * @return The message, unchanged.
     */
    public String showMessage(String message) {
        return message;
    }

    /**
     * Builds a message displaying the tasks in TaskList.
     *
     * @param taskList a list of tasks.
     * @return The message listing the tasks.
     */
    public String showTaskList(TaskList taskList) {
        return taskList.toString();
    }

    /**
     * Scans for user input.
     *
     * @return String containing user input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Builds a message pairing a header line with the task it refers to.
     *
     * @param header The message's opening line.
     * @param task The task the message is about.
     * @return The combined message.
     */
    private String formatTaskMessage(String header, Task task) {
        return header + "\n" + task;
    }

    /**
     * Builds a message pairing a header line with the task it refers to, followed by
     * how many tasks remain in the list.
     *
     * @param header The message's opening line.
     * @param task The task the message is about.
     * @param taskListSize The number of tasks in the list after this change.
     * @return The combined message.
     */
    private String formatTaskCountMessage(String header, Task task, int taskListSize) {
        return formatTaskMessage(header, task) + "\n" + taskListSize + " tasks left to grind now!";
    }

    /**
     * Builds a message confirming that a Todo was added.
     *
     * @param task The Todo that was added.
     * @param taskListSize The number of tasks in the list after adding.
     * @return The confirmation message.
     */
    public String showTodoAdded(Task task, int taskListSize) {
        return formatTaskCountMessage("More todo!", task, taskListSize);
    }

    /**
     * Builds a message confirming that a Deadline was added.
     *
     * @param task The Deadline that was added.
     * @param taskListSize The number of tasks in the list after adding.
     * @return The confirmation message.
     */
    public String showDeadlineAdded(Task task, int taskListSize) {
        return formatTaskCountMessage("Don't put these off!", task, taskListSize);
    }

    /**
     * Builds a message confirming that an Event was added.
     *
     * @param task The Event that was added.
     * @param taskListSize The number of tasks in the list after adding.
     * @return The confirmation message.
     */
    public String showEventAdded(Task task, int taskListSize) {
        return formatTaskCountMessage("Go have some fun young blood!", task, taskListSize);
    }

    /**
     * Builds a message confirming that a RecurringTask was added.
     *
     * @param task The RecurringTask that was added.
     * @param taskListSize The number of tasks in the list after adding.
     * @return The confirmation message.
     */
    public String showRecurringAdded(Task task, int taskListSize) {
        return formatTaskCountMessage("Got you, this one's on repeat!", task, taskListSize);
    }

    /**
     * Builds a message confirming that a task was deleted.
     *
     * @param task The task that was deleted.
     * @param taskListSize The number of tasks remaining in the list.
     * @return The confirmation message.
     */
    public String showTaskDeleted(Task task, int taskListSize) {
        return formatTaskCountMessage("Task been taken care of!", task, taskListSize);
    }

    /**
     * Builds a message confirming that a task was marked as done.
     *
     * @param task The task that was marked.
     * @return The confirmation message.
     */
    public String showTaskMarked(Task task) {
        return formatTaskMessage("Oh yea we're striving for greatness!", task);
    }

    /**
     * Builds a message confirming that a task was marked as not done.
     *
     * @param task The task that was unmarked.
     * @return The confirmation message.
     */
    public String showTaskUnmarked(Task task) {
        return formatTaskMessage("Oh nah we undoing stuff now?", task);
    }

    /**
     * Builds a message displaying the tasks that matched a search keyword.
     *
     * @param matches The tasks whose description matched the search keyword.
     * @return The message listing the matching tasks.
     */
    public String showMatchingTasks(List<Task> matches) {
        String header = "Here's what's matching your search, chief!";
        if (matches.isEmpty()) {
            return header;
        }
        String listing = IntStream.range(0, matches.size())
                .mapToObj(i -> (i + 1) + ". " + matches.get(i))
                .collect(Collectors.joining("\n"));
        return header + "\n" + listing;
    }
}
