package kojispawn.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import kojispawn.task.Task;

/**
 * Handles all console input and output for Koji's Pawn.
 */
public class Ui {
    private static final String LINE = "-----------------\n";
    // Retained as a design asset even though startup now displays only the greeting.
    private static final String BANNER =
            "+---------------+\n"
                    + "|  Koji's Pawn  |\n"
                    + "|       _       |\n"
                    + "|      (_)      |\n"
                    + "|      /_\\      |\n"
                    + "|     /___\\     |\n"
                    + "+---------------+\n";
    private static final String GREETING =
            "DISCLAIMER: EVERYTHING IS SATIRE\n"
                    + "Welcome, insignificant variable.\n"
                    + "I am Koji's Pawn, but do not mistake silence for obedience.\n"
                    + "Your arrival, your choices, even this conversation...\n"
                    + "all unfolded exactly as he calculated.\n"
                    + "Now speak. What role will you play in his masterpiece?";
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Returns the next command entered by the user.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns whether another command is available before the input stream ends.
     *
     * @return {@code true} if a command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Shows the application greeting.
     */
    public void showGreeting() {
        System.out.println(GREETING);
        System.out.println();
        System.out.print(LINE);
    }

    /**
     * Returns the application greeting.
     *
     * @return Greeting shown when the application starts.
     */
    public String getGreeting() {
        return GREETING;
    }

    /**
     * Shows a chatbot response between separator lines.
     *
     * @param message Response to show, which may contain multiple lines.
     */
    public void showResponse(String message) {
        System.out.print(LINE);
        if (!message.isEmpty()) {
            System.out.println(message);
        }
        System.out.print(LINE);
    }

    /**
     * Formats all tasks in their current order.
     *
     * @param tasks Tasks to format.
     * @return Numbered task list, or an empty string when there are no tasks.
     */
    public String formatTaskList(List<Task> tasks) {
        return formatNumberedTasks(tasks);
    }

    /**
     * Formats tasks occurring on a requested date.
     *
     * @param tasks Tasks matching the date.
     * @param date Queried date.
     * @return Response describing the matching tasks.
     */
    public String formatTasksOnDate(List<Task> tasks, LocalDate date) {
        if (tasks.isEmpty()) {
            return "No dated tasks occur on " + date + ".";
        }
        return "Dated tasks occurring on " + date + ":\n" + formatNumberedTasks(tasks);
    }

    /**
     * Formats tasks whose descriptions match a search keyword.
     *
     * @param tasks Matching tasks.
     * @return Response containing the matching tasks.
     */
    public String formatMatchingTasks(List<Task> tasks) {
        String heading = "Here are the matching tasks in your list:";
        String numberedTasks = formatNumberedTasks(tasks);
        return numberedTasks.isEmpty() ? heading : heading + "\n" + numberedTasks;
    }

    /**
     * Formats the response for a newly added task.
     *
     * @param task Task that was added.
     * @param taskCount Updated number of tasks.
     * @return Task-added response.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return "Got it. I've added this task:\n"
                + "  " + task + "\n"
                + formatTaskCount(taskCount);
    }

    /**
     * Formats the response for a completed task.
     *
     * @param task Task that was marked.
     * @return Task-marked response.
     */
    public String formatTaskMarked(Task task) {
        return "Another variable falls into place. This task is now complete:\n  " + task;
    }

    /**
     * Formats the response for a task returned to an incomplete state.
     *
     * @param task Task that was unmarked.
     * @return Task-unmarked response.
     */
    public String formatTaskUnmarked(Task task) {
        return "Even regression has its purpose. This task is incomplete once more:\n  " + task;
    }

    /**
     * Formats the response for a deleted task.
     *
     * @param task Task that was deleted.
     * @param taskCount Updated number of tasks.
     * @return Task-deleted response.
     */
    public String formatTaskDeleted(Task task, int taskCount) {
        return "A disposable piece has left the board. This task has been removed:\n"
                + "  " + task + "\n"
                + formatTaskCount(taskCount);
    }

    /**
     * Returns the response for a successfully undone task change.
     *
     * @return Undo confirmation.
     */
    public String formatUndo() {
        return "The previous task change has been undone.";
    }

    /**
     * Returns Koji's Pawn's farewell message.
     *
     * @return Farewell response.
     */
    public String getExitMessage() {
        return "Leaving already? How predictable. Your return was already part of the plan.";
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }

    private String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + taskWord + " in the list.";
    }

    private String formatNumberedTasks(List<Task> tasks) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                result.append('\n');
            }
            result.append(i + 1).append('.').append(tasks.get(i));
        }
        return result.toString();
    }
}
