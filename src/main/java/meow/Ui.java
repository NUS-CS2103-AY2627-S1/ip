package meow;

/**
 * Handles all user output for the chatbot.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";

    /**
     * Displays the chatbot greeting message.
     */
    public void showGreeting() {
        System.out.println(SEPARATOR);
        String banner = "███   ███ ███████  █████  ██     ██\n"
                + "████ ████ ██      ██   ██ ██     ██\n"
                + "██ ███ ██ █████   ██   ██ ██  █  ██\n"
                + "██     ██ ██      ██   ██ ██ ███ ██\n"
                + "██     ██ ███████  █████   ███ ███\n";
        System.out.println(banner);
        String greeting = "Meow! Welcome back. \n" + "Start yapping, I'm all ears!";
        System.out.println(greeting);
        System.out.println(SEPARATOR);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.println(SEPARATOR);
        System.out.println(message);
        System.out.println(SEPARATOR);
    }

    /**
     * Returns the farewell message.
     *
     * @return the farewell message
     */
    public String getFarewellMessage() {
        return "That's all for now. Stay pawsitive, and I'll catch you later!";
    }

    /**
     * Returns the task list as a formatted message.
     * If the task list is empty, returns a message indicating that there are no tasks.
     *
     * @param taskList the task list to display
     * @return the formatted task list, or an empty-list message if there are no tasks
     */
    public String getTaskListMessage(TaskList taskList) {
        if (taskList.size() == 0) {
            return "Meow! Your task list is empty. Time to add something 😼";
        }

        return formatTaskList(
                "Meow! Here's what you've got on your paws:",
                taskList);
    }

    /**
     * Returns a message confirming that a task was marked as completed.
     *
     * @param task the task that was marked
     * @return the confirmation message
     */
    public String getTaskMarkedMessage(Task task) {
        return formatLines(
                "Purrfect! This task is done:",
                task.toString());
    }

    /**
     * Returns a message confirming that a task was marked as not completed.
     *
     * @param task the task that was unmarked
     * @return the confirmation message
     */
    public String getTaskUnmarkedMessage(Task task) {
        return formatLines(
                "Meow! Back onto the to-do pile it goes:",
                task.toString());
    }

    /**
     * Returns a message confirming that a task was added.
     *
     * @param task the task that was added
     * @param taskCount the total number of tasks
     * @return the confirmation message
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return formatLines(
                "Got it! I've tucked this task into your list:",
                task.toString(),
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Returns a message confirming that a task was deleted.
     *
     * @param task the task that was deleted
     * @param taskCount the total number of tasks
     * @return the confirmation message
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return formatLines(
                "Poof! This task is out of your paws:",
                task.toString(),
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Returns matching tasks as a formatted message.
     * If there are no matching tasks, returns a message indicating that no matches were found.
     *
     * @param matches the matching tasks
     * @return the formatted matching task list, or a no-matches message if no tasks match
     */
    public String getMatchingTasksMessage(TaskList matches) {
        if (matches.size() == 0) {
            return "Meow! I couldn't find any tasks matching that search.";
        }

        return formatTaskList(
                "Found them! These tasks match your search:",
                matches);
    }

    /**
     * Formats a task list with the given heading.
     *
     * @param heading the heading to display
     * @param taskList the task list to format
     * @return the formatted message
     */
    private String formatTaskList(String heading, TaskList taskList) {
        StringBuilder message = new StringBuilder(heading);

        for (int i = 0; i < taskList.size(); i++) {
            message.append("\n")
                    .append(i + 1)
                    .append(". ")
                    .append(taskList.getTask(i));
        }

        return message.toString();
    }

    /**
     * Combines multiple lines into one message.
     *
     * @param lines the lines to combine
     * @return the combined message
     */
    private String formatLines(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Returns a message confirming that the tasks were sorted.
     *
     * @return the confirmation message
     */
    public String getTasksSortedMessage() {
        return "All neat and tidy! Your tasks are sorted chronologically.";
    }
}
