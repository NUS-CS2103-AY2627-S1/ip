package arthur.messages;

import java.util.List;
import java.util.stream.IntStream;

import arthur.tasks.Task;

/**
 * Displays the tasks currently stored in the command context.
 */
public class ListMessage extends Message {
    // AI-assisted: Named list formatting and user-facing numbering literals.
    private static final String NO_TASKS = "No tasks to display.";
    private static final String RESPONSE_FORMAT = "%s\n\n%s";
    private static final String EMPTY_LIST = "";
    private static final String TASK_NUMBER_SUFFIX = ". ";
    private static final int DISPLAY_INDEX_OFFSET = 1;
    // AI-assisted: Added a message that formats stored tasks as numbered entries.
    private final List<Task> tasks;

    /**
     * Creates a message that displays the supplied tasks.
     *
     * @param tasks the tasks to display.
     */
    public ListMessage(List<Task> tasks) {
        this.tasks = tasks;
        assert tasks != null : "Tasks should not be null";
    }

    @Override
    public String toString() {
        String listStr;
        if (tasks.isEmpty()) {
            listStr = NO_TASKS;
        } else {
            listStr = IntStream.range(0, tasks.size())
                    .mapToObj(index -> (index + DISPLAY_INDEX_OFFSET) + TASK_NUMBER_SUFFIX + tasks.get(index))
                    .reduce(EMPTY_LIST, (list, task) -> list.isEmpty() ? task : list + System.lineSeparator() + task);
        }
        return String.format(RESPONSE_FORMAT, getRandomQuote(), listStr);
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
