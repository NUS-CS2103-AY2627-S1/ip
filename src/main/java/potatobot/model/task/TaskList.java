package potatobot.model.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import potatobot.exception.PotatoBotException;

/**
 * Stores and manages a bounded list of tasks.
 */
public class TaskList {
    /** Maximum number of tasks that the list can store. */
    public static final int MAX_SIZE = 100;

    private final List<Task> tasks = new ArrayList<>(MAX_SIZE);

    /**
     * Creates an empty task list.
     */
    public TaskList() {
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task.
     * @return Task at the specified index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds one or more items to the task list.
     *
     * @param items Tasks to add.
     * @throws PotatoBotException If there is insufficient room for all the tasks.
     */
    public void add(Task... items) throws PotatoBotException {
        if (items.length > MAX_SIZE - tasks.size()) {
            throw new PotatoBotException("Your potato sack is full! It can only hold 100 items.");
        }

        for (Task item : items) {
            tasks.add(item);
        }

        // The capacity check must preserve the bounded-list invariant after every
        // addition.
        assert tasks.size() <= MAX_SIZE : "Adding tasks must not exceed the list capacity";
    }

    /**
     * Adds a task at the specified zero-based index.
     *
     * @param index Zero-based position at which to restore the task.
     * @param task  Task to add.
     * @throws PotatoBotException If the task list is already full.
     */
    public void add(int index, Task task) throws PotatoBotException {
        if (tasks.size() == MAX_SIZE) {
            throw new PotatoBotException("Your potato sack is full! It can only hold 100 items.");
        }
        tasks.add(index, task);

        assert tasks.size() <= MAX_SIZE : "Adding a task must not exceed the list capacity";
    }

    /**
     * Returns {@code true} if this task list contains no tasks.
     *
     * @return {@code true} if this list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Marks the task at the specified index as completed.
     *
     * @param index Zero-based index of the task to mark.
     */
    public void markDone(int index) {
        tasks.get(index).markDone();
    }

    /**
     * Marks the task at the specified index as incomplete.
     *
     * @param index Zero-based index of the task to reset.
     */
    public void markReset(int index) {
        tasks.get(index).markReset();
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index Zero-based index of the task to remove.
     * @return Removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Formats all tasks as a numbered list for display to the user.
     *
     * @return Formatted task list, or an empty-list message if no tasks exist.
     */
    public String printList() {
        if (isEmpty()) {
            return "Nothing to see here...";
        }

        String formattedTasks = IntStream.range(0, tasks.size())
                .mapToObj(index -> formatTask(tasks.get(index), index + 1))
                .collect(Collectors.joining());
        return "Here are the tasks in your potato sack:" + formattedTasks;
    }

    /**
     * Formats tasks whose descriptions contain a keyword as a numbered list.
     *
     * @param keyword Keyword to find in task descriptions.
     * @return Formatted matching tasks, or an empty-list message if none match.
     */
    public String find(String keyword) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.matchesKeyword(keyword))
                .toList();
        if (matchingTasks.isEmpty()) {
            return "Nothing to see here...";
        }

        String formattedTasks = IntStream.range(0, matchingTasks.size())
                .mapToObj(index -> formatTask(matchingTasks.get(index), index + 1))
                .collect(Collectors.joining());
        return "Here are the tasks in your potato sack:" + formattedTasks;
    }

    /**
     * Converts the task list to the text stored in the save file.
     * Each task occupies one line and includes its completion status.
     *
     * @return Task list in its storage format.
     */
    public String toFileContents() {
        return tasks.stream()
                .map(task -> "[" + task.getStatusIcon() + "] " + task)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Formats a task with its displayed number and completion status.
     *
     * @param task       Task to format.
     * @param taskNumber One-based number displayed to the user.
     * @return Task formatted as one line in a displayed task list.
     */
    private static String formatTask(Task task, int taskNumber) {
        return "\n  " + taskNumber + ".[" + task.getStatusIcon() + "] " + task;
    }
}
