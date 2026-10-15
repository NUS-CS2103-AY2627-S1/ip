package orion.format;

import java.util.List;

import orion.task.Task;
import orion.task.TaskList;

/**
 * Formats text responses for the Orion chatbot.
 */
public class ResponseFormatter {

    /**
     * Returns the welcome message to the user.
     */
    public String getWelcomeMessage() {
        return formatLines("Hello! I'm Orion, your friendly chatbot.", "What can I do for you?");
    }

    /**
     * Returns the goodbye message to the user.
     */
    public String getGoodbyeMessage() {
        return "Hope to see you again soon. Goodbye!";
    }

    /**
     * Formats and returns the list of all tasks.
     *
     * @param tasks The TaskList containing the tasks.
     * @return A formatted string of all tasks.
     */
    public String getTaskListMessage(TaskList tasks) {
        if (tasks.getSize() == 0) {
            return "You currently do not have any tasks!";
        }
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.getSize(); i++) {
            sb.append(i + 1).append(". ").append(tasks.getTask(i)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Formats the message for a successfully added task.
     *
     * @param task The task that was added.
     * @param size The current size of the task list.
     * @return The formatted success message.
     */
    public String getTaskAddedMessage(Task task, int size) {
        return formatLines("Got it. I've added this task:",
                "  " + task,
                "You now have " + size + " task(s) in the list."
        );
    }

    /**
     * Formats the message for marking a task as done or not done.
     *
     * @param task The task that was marked.
     * @param isDone {@code true} if the task was marked done, {@code false} otherwise.
     * @return The formatted status message.
     */
    public String getTaskMarkedMessage(Task task, boolean isDone) {
        String status = isDone ? "Nice! I've marked this task as done:\n"
                : "OK, I've marked this task as not done yet:\n";
        return status + task;
    }

    /**
     * Formats the message for a successfully deleted task.
     *
     * @param task The task that was removed.
     * @param size The current size of the task list.
     * @return The formatted deletion message.
     */
    public String getTaskDeletedMessage(Task task, int size) {
        return formatLines("Got it. I've removed this task:",
                "  " + task,
                "You now have " + size + " task(s) in the list."
        );
    }

    /**
     * Formats the list of tasks that match a specific search keyword.
     *
     * @param keyword The search keyword.
     * @param tasks The TaskList to search within.
     * @return A formatted string of matching tasks.
     */
    public String getMatchingTasksMessage(String keyword, TaskList tasks) {
        List<Task> matchingTasks = tasks.findTasks(keyword);
        if (matchingTasks.isEmpty()) {
            return "There aren't any tasks that matched your search keyword of \"" + keyword + "\"!";
        }
        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");
        int count = 1;
        for (Task task : matchingTasks) {
            sb.append(count++).append(". ").append(task).append("\n");
        }
        return sb.toString().trim();
    }

    public String getHelpMessage() {
        return "Here are the commands you can use:\n"
                + "• todo (desc) - Add a Todo task\n"
                + "• deadline (desc) /by (date) - Add a Deadline task\n"
                + "• event (desc) /from (date) /to (date) - Add an Event task\n"
                + "• list - List all your tasks\n"
                + "• mark (number) - Set task as completed\n"
                + "• unmark (number) - Set task as uncompleted\n"
                + "• delete (number) - Remove a task\n"
                + "• find (keyword) - Search for tasks with the keyword\n"
                + "• undo - Reverts your last action\n"
                + "• help - Shows this list\n"
                + "• bye - Exit the application";
    }

    private String formatLines(String... lines) {
        return String.join("\n", lines);
    }

}
