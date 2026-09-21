package nova;

import java.util.List;
import java.util.Scanner;

/**
 * Handles console input and formats Nova's responses.
 */
public class Ui {

    private final Scanner scanner;

    /**
     * Creates the console user interface.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads the next user command.
     *
     * @return User command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Formats Nova's initial greeting.
     *
     * @return Welcome message.
     */
    public static String formatWelcomeMessage() {
        return joinLines(
                "Nova online! Your tasks are ready for launch.",
                "What shall we accomplish today?");
    }

    /**
     * Formats all tasks with their one-based task numbers.
     *
     * @param tasks Tasks to format.
     * @return Formatted task list.
     */
    public static String formatTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            return "Your orbit is clear. There are no tasks yet.";
        }

        StringBuilder response = new StringBuilder();
        for (int i = 1; i <= tasks.size(); i++) {
            if (i > 1) {
                response.append(System.lineSeparator());
            }
            response.append(i)
                    .append(".")
                    .append(tasks.get(i).toDisplayString());
        }
        return response.toString();
    }

    /**
     * Formats tasks that match a search keyword.
     *
     * @param tasks Matching tasks.
     * @return Formatted matching tasks.
     */
    public static String formatMatchingTasks(List<Task> tasks) {
        StringBuilder response = new StringBuilder(
                "Scan complete. Here are the matching tasks:");

        for (int i = 0; i < tasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append(".")
                    .append(tasks.get(i).toDisplayString());
        }
        return response.toString();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Total number of tasks.
     * @return Task-added confirmation.
     */
    public static String formatAddedTask(Task task, int taskCount) {
        return joinLines(
                "Task added to your orbit:",
                task.toDisplayString(),
                formatTaskCount(taskCount));
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of remaining tasks.
     * @return Task-deleted confirmation.
     */
    public static String formatDeletedTask(Task task, int taskCount) {
        return joinLines(
                "Task cleared from your orbit:",
                task.toDisplayString(),
                formatTaskCount(taskCount));
    }

    /**
     * Formats confirmation that a task was marked as done.
     *
     * @return Task-marked confirmation.
     */
    public static String formatMarkedMessage() {
        return "Mission accomplished! I've marked this task as done.";
    }

    /**
     * Formats confirmation that a task was updated.
     *
     * @param task Updated task.
     * @return Task-updated confirmation.
     */
    public static String formatUpdatedTask(Task task) {
        return joinLines("Course corrected! I've updated this task:",
                task.toDisplayString());
    }

    /**
     * Formats the response for an unrecognised command.
     *
     * @return Unknown-command response.
     */
    public static String formatUnknownCommand() {
        return "That command is outside my orbit. Please try another one.";
    }

    /**
     * Displays a response in the console.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        if (!response.isEmpty()) {
            System.out.println(response);
        }
    }

    /**
     * Formats Nova's farewell message.
     *
     * @return Farewell message.
     */
    public static String formatByeMessage() {
        return "Nova signing off. Keep reaching for the stars!";
    }

    private static String formatTaskCount(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        return "You now have " + taskCount + " " + noun + " in orbit.";
    }

    private static String joinLines(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }

    /**
     * Closes the user interface.
     */
    public void close() {
        scanner.close();
    }
}
