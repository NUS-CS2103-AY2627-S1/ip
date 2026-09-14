package nova;

import java.util.List;
import java.util.stream.IntStream;

import nova.task.Task;

/**
 * Builds everything the chatbot says to the user.
 *
 * <p>Nova's voice is deliberately understated: it states what happened and
 * stops. Confirmations are short, errors say what to type instead of only
 * what went wrong, and nothing is exclaimed. The aim is a tool that stays out
 * of the way, not a companion that performs.
 *
 * <p>Messages are collected into a buffer rather than printed. Whoever is
 * driving the chatbot calls {@link #flush()} once the current command is done
 * and decides how to present the text: the console front end frames it with
 * divider lines, while the GUI puts it in a dialog bubble. That is what lets
 * both front ends share one set of messages.
 */
public class Ui {
    private final StringBuilder buffer = new StringBuilder();

    /** Creates a source of chatbot messages. */
    public Ui() {
    }

    /**
     * Returns everything said since the last call, and empties the buffer.
     *
     * @return the accumulated message, without a trailing line separator
     */
    public String flush() {
        String message = buffer.toString();
        buffer.setLength(0);
        return message.stripTrailing();
    }

    /** Greets the user on startup. */
    public void showWelcome() {
        addLines("Nova ready.",
                "Type a command, or help to see what I understand.");
    }

    /** Says goodbye just before the chatbot exits. */
    public void showGoodbye() {
        addLines("Saved. See you next time.");
    }

    /**
     * Reports a problem to the user.
     *
     * <p>No prefix is added. The console front end frames it and the GUI
     * colours it, so the text itself is left to say only what is wrong and
     * what to do instead.
     *
     * @param message the explanation to show
     */
    public void showError(String message) {
        addLines(message);
    }

    /**
     * Lists what the chatbot understands.
     *
     * @param entries one "command - description" line per supported command
     */
    public void showHelp(List<String> entries) {
        addLines("Commands:");
        addLines(entries.stream()
                .map(entry -> entry.isEmpty() ? "" : "  " + entry)
                .toArray(String[]::new));
    }

    /**
     * Shows the whole task list, numbered from one.
     *
     * @param tasks the tasks to list
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            addLines("Your list is empty.");
            return;
        }
        addLines(countOf(tasks.size()) + " in your list.");
        addNumbered(tasks);
    }

    /**
     * Confirms that a task was added.
     *
     * @param task  the task just added
     * @param total the number of tasks now in the list
     */
    public void showAdded(Task task, int total) {
        addLines("Added. Now " + ordinal(total) + " in your list.",
                "  " + task);
    }

    /**
     * Confirms that a task was deleted.
     *
     * @param task      the task just removed
     * @param remaining the number of tasks left in the list
     */
    public void showRemoved(Task task, int remaining) {
        addLines("Removed. " + countOf(remaining) + " left.",
                "  " + task);
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task just marked
     */
    public void showMarked(Task task) {
        addLines("Marked done.", "  " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task just unmarked
     */
    public void showUnmarked(Task task) {
        addLines("Marked not done.", "  " + task);
    }

    /**
     * Shows the tasks falling on a particular date.
     *
     * @param dateText the date, already formatted for display
     * @param matches  the tasks that fall on that date
     */
    public void showTasksOn(String dateText, List<Task> matches) {
        if (matches.isEmpty()) {
            addLines("Nothing on " + dateText + ".");
            return;
        }
        addLines(countOf(matches.size()) + " on " + dateText + ".");
        addNumbered(matches);
    }

    /**
     * Shows the tasks matching a search.
     *
     * @param keyword the text that was searched for
     * @param matches the tasks whose description contains the keyword
     */
    public void showMatchingTasks(String keyword, List<Task> matches) {
        if (matches.isEmpty()) {
            addLines("No matches for \"" + keyword + "\".");
            return;
        }
        addLines(countOf(matches.size()) + " matching \"" + keyword + "\".");
        addNumbered(matches);
    }

    /**
     * Confirms that the list was reordered, and shows the new order.
     *
     * @param tasks the tasks in their new order
     */
    public void showSorted(List<Task> tasks) {
        if (tasks.isEmpty()) {
            addLines("Nothing to sort.");
            return;
        }
        addLines("Sorted by date, soonest first.");
        addNumbered(tasks);
    }

    /**
     * Warns that the save file could not be read in full.
     *
     * @param skipped how many lines had to be discarded
     */
    public void showLoadingError(int skipped) {
        addLines("Skipped " + skipped + " unreadable line(s) in the save file. The rest loaded.");
    }

    /** Returns "1 task" or "N tasks", so counts never read as "1 tasks". */
    private static String countOf(int count) {
        return count == 1 ? "1 task" : count + " tasks";
    }

    /** Returns the position as an ordinal, e.g. 1st, 2nd, 3rd, 11th. */
    private static String ordinal(int position) {
        boolean isTeen = position % 100 >= 11 && position % 100 <= 13;
        String suffix = switch (isTeen ? 0 : position % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
        return position + suffix;
    }

    /** Appends the given tasks, numbered from one. */
    private void addNumbered(List<Task> tasks) {
        addLines(IntStream.range(0, tasks.size())
                .mapToObj(i -> "  " + (i + 1) + ". " + tasks.get(i))
                .toArray(String[]::new));
    }

    /**
     * Appends lines to the message being built.
     *
     * @param lines the lines to add, in order
     */
    private void addLines(String... lines) {
        for (String line : lines) {
            buffer.append(line).append(System.lineSeparator());
        }
    }
}
