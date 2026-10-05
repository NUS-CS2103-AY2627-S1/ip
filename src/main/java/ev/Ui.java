package ev;

import java.util.ArrayList;
import java.util.Scanner;

import ev.task.Task;

/**
 * Handles interaction with the user, reading input and building E.V.'s replies.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Constructs a user interface that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns the next line of user input with surrounding whitespace removed.
     *
     * @return the trimmed command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Returns whether there is another line of console input to read.
     *
     * @return true if the input source has not been exhausted.
     */
    public boolean hasCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns E.V.'s startup banner.
     *
     * @return the greeting shown when E.V. starts.
     */
    public String getWelcome() {
        return "E.V. online.\n"
                + "Suit systems nominal, Spidey.\n"
                + "What's on the list for the friendly neighbourhood spiderman today?\n";
    }

    /**
     * Returns E.V.'s shutdown message.
     *
     * @return the farewell shown when the user says bye.
     */
    public String getGoodbye() {
        return "E.V. offline. Swing safe, Spidey.";
    }

    /**
     * Returns every task currently in the registry, in registry order.
     *
     * @param tasks the registry to list.
     * @return the numbered registry listing.
     */
    public String getList(TaskList tasks) {
        return formatPositions("Current task registry:", "The registry is empty.",
                tasks, tasks.positions());
    }

    /**
     * Returns the tasks matching a search, labelled with their registry positions.
     *
     * @param tasks the registry the positions refer to.
     * @param positions the one-based positions of the matching tasks.
     * @return the numbered listing of matches.
     */
    public String getFound(TaskList tasks, ArrayList<Integer> positions) {
        return formatPositions("Matching entries in the registry:",
                "Nothing in the registry matches that.", tasks, positions);
    }

    /**
     * Returns the tasks in description order, labelled with their registry positions.
     *
     * @param tasks the registry the positions refer to.
     * @param positions the one-based positions in description order.
     * @return the numbered sorted listing.
     */
    public String getSorted(TaskList tasks, ArrayList<Integer> positions) {
        return formatPositions("Registry sorted by description:", "The registry is empty.",
                tasks, positions);
    }

    /**
     * Returns the given positions rendered one per line, each labelled with the number
     * mark, unmark and delete accept for that task.
     *
     * @param header the line shown above the tasks.
     * @param emptyMessage the text returned instead when there are no positions.
     * @param tasks the registry the positions refer to.
     * @param positions the one-based positions to render, in display order.
     * @return the header followed by the numbered tasks, or the empty message.
     */
    private String formatPositions(String header, String emptyMessage, TaskList tasks,
            ArrayList<Integer> positions) {
        if (positions.isEmpty()) {
            return emptyMessage;
        }

        StringBuilder builder = new StringBuilder(header);

        for (int position : positions) {
            builder.append("\n").append(position).append(".").append(tasks.get(position - 1));
        }

        return builder.toString();
    }

    /**
     * Returns the confirmation shown after a task is added to the registry.
     *
     * @param task the task that was just added.
     * @param taskCount the number of tasks now in the registry.
     * @return the confirmation message.
     */
    public String getAdded(Task task, int taskCount) {
        return "Task logged:\n  " + task + "\n" + getRegistryCount(taskCount);
    }

    /**
     * Returns the confirmation shown after a task is removed from the registry.
     *
     * @param task the task that was just removed.
     * @param taskCount the number of tasks now in the registry.
     * @return the confirmation message.
     */
    public String getRemoved(Task task, int taskCount) {
        return "Task removed:\n  " + task + "\n" + getRegistryCount(taskCount);
    }

    /**
     * Returns the confirmation shown after a task is marked done.
     *
     * @param task the task that was marked done.
     * @return the confirmation message.
     */
    public String getMarked(Task task) {
        return "Task completed.\n" + task;
    }

    /**
     * Returns the confirmation shown after a task is marked not done.
     *
     * @param task the task that was reopened.
     * @return the confirmation message.
     */
    public String getUnmarked(Task task) {
        return "Task reopened.\n" + task;
    }

    /**
     * Returns how many tasks the registry currently holds.
     *
     * @param taskCount the number of tasks currently in the registry.
     * @return the registry count message.
     */
    public String getRegistryCount(int taskCount) {
        return "Registry holds " + taskCount + (taskCount == 1 ? " task." : " tasks.");
    }

    /**
     * Returns the warning shown when the data file held lines E.V. could not read.
     *
     * @param skippedLineCount how many lines were left out.
     * @return the warning message.
     */
    public String getSkippedLineWarning(int skippedLineCount) {
        return skippedLineCount == 1
                ? "Warning: 1 damaged entry in my memory banks was left out."
                : "Warning: " + skippedLineCount
                        + " damaged entries in my memory banks were left out.";
    }

    /**
     * Returns the commands E.V. understands and the format of each.
     *
     * @return the help message.
     */
    public String getHelp() {
        return "Commands I understand:\n\n"
                + "  todo DESCRIPTION\n"
                + "  deadline DESCRIPTION /by yyyy-MM-dd HHmm\n"
                + "  event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm\n"
                + "  list\n"
                + "  mark TASK_NUMBER\n"
                + "  unmark TASK_NUMBER\n"
                + "  delete TASK_NUMBER\n"
                + "  find KEYWORD\n"
                + "  sort\n"
                + "  bye\n"
                + "  help\n"
                + "\n"
                + "Numbers shown by find and sort are registry positions, so you can use\n"
                + "them directly with mark, unmark and delete.";
    }

    /**
     * Closes the input source.
     */
    public void close() {
        scanner.close();
    }
}
