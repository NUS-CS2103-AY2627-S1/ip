package chim.ui;

import java.util.List;

import chim.task.Task;

/**
 * Formats messages for the chatbot's responses and handles printing
 * for the command-line interface.
 */
public class Ui {

    private static final String LINE = "____________________________________________________________";

    /**
     * Prints the welcome banner and greeting shown when Chim starts up (CLI only).
     */
    public void showWelcome() {
        String logo =
                "  #####  #     # ###  #     # \n"
                        + " #     # #     #  #   ##   ## \n"
                        + " #       #     #  #   # # # # \n"
                        + " #       #######  #   #  #  # \n"
                        + " #       #     #  #   #     # \n"
                        + " #     # #     #  #   #     # \n"
                        + "  #####  #     # ###  #     # \n";
        System.out.println(LINE);
        System.out.println(logo);
        System.out.println("Ooh ooh ah ah! I'm Chim! \uD83D\uDC12");
        System.out.println("Swing on by and tell me what you need!");
        System.out.println(LINE);
    }

    /**
     * Prints a message wrapped in Chim's standard divider lines (CLI only).
     *
     * @param message Message to print.
     */
    public void printMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

    /**
     * Returns the goodbye message shown when the user exits Chim.
     *
     * @return Goodbye message.
     */
    public String getGoodbyeMessage() {
        return "Bye bye! \uD83D\uDC12 Swing back anytime!";
    }

    /**
     * Returns every task in the given list, numbered from 1.
     *
     * @param tasks Tasks to display.
     * @return Formatted task list message.
     */
    public String getTaskListMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return " Your list is empty! Nothing to swing on yet.";
        }
        return formatTaskList(" Here's everything on your vine:", tasks);
    }

    /**
     * Returns every task that matches a search keyword, numbered from 1.
     *
     * @param matchingTasks Tasks that matched the search.
     * @return Formatted matching-tasks message.
     */
    public String getMatchingTasksMessage(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return " Hmm, I couldn't find anything matching that. Try another word?";
        }
        return formatTaskList(" I found these for you:", matchingTasks);
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Total number of tasks after adding.
     * @return Formatted task-added message.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return " Got it! Adding that to the pile:\n"
                + "   " + task + "\n"
                + " You've got " + taskCount + " " + (taskCount == 1 ? "task" : "tasks") + " to swing through now!";
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task Task that was removed.
     * @param taskCount Total number of tasks after removal.
     * @return Formatted task-deleted message.
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return " Poof! Tossed that one away:\n"
                + "   " + task + "\n"
                + " " + taskCount + " " + (taskCount == 1 ? "task" : "tasks") + " left on your vine.";
    }

    /**
     * Returns confirmation that a task was marked as done.
     *
     * @param task Task that was marked.
     * @return Formatted task-marked message.
     */
    public String getTaskMarkedMessage(Task task) {
        return " Woohoo, nice work! Marked as done:\n"
                + "   " + task;
    }

    /**
     * Returns confirmation that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     * @return Formatted task-unmarked message.
     */
    public String getTaskUnmarkedMessage(Task task) {
        return " No worries, marked as not done yet:\n"
                + "   " + task;
    }

    /**
     * Returns confirmation that a task's priority was changed.
     *
     * @param task Task whose priority was changed.
     * @return Formatted priority-changed message.
     */
    public String getTaskPriorityMessage(Task task) {
        return joinLines(
                " Got it. I've updated the priority of this task:",
                "   " + task
        );
    }

    private String joinLines(String... lines) {
        return String.join("\n", lines);
    }

    private String formatTaskList(String header, List<Task> tasks) {
        StringBuilder sb = new StringBuilder(header);
        for (int i = 0; i < tasks.size(); i++) {
            sb.append("\n ").append(i + 1).append(".").append(tasks.get(i));
        }
        return sb.toString();
    }
}
