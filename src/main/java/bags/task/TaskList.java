package bags.task;

import java.util.ArrayList;
import java.util.List;

import bags.exception.BagsException;

/**
 * Stores tasks and provides operations that change the task collection.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied loaded tasks.
     *
     * @param tasks the list of tasks to initialise the task list with
     */
    public TaskList(List<Task> tasks) {
        if (tasks == null) {
            this.tasks = new ArrayList<>();
        } else {
            this.tasks = new ArrayList<>(tasks);
        }
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task to TaskList";
        int previousSize = tasks.size();

        tasks.add(task);

        assert tasks.size() == previousSize + 1 : "Task list size should increase by 1 after addition";
    }

    public int getSize() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public List<Task> getTasks() {
        return this.tasks;
    }

    /**
     * Marks the selected task as done.
     *
     * @param command the user's mark command containing the task number
     * @return the task that was marked as done
     * @throws BagsException if the task number is missing, invalid,
     *                       or does not correspond to an existing task
     */
    public Task markDone(String command) throws BagsException {
        int index = parseTaskIndex(command);
        assert index >= 0 && index < tasks.size() : "Parsed index must be within valid bounds";

        Task task = tasks.get(index);
        assert task != null : "Task retrieved at valid index should not be null";

        task.markDone();
        assert task.isDone() : "Task should be marked as done";
        return task;
    }

    /**
     * Marks the selected task as undone.
     *
     * @param command the user's unmark command containing the task number
     * @return the task that was marked as undone
     * @throws BagsException if the task number is missing, invalid,
     *                       or does not correspond to an existing task
     */
    public Task markUndone(String command) throws BagsException {
        int index = parseTaskIndex(command);
        assert index >= 0 && index < tasks.size() : "Parsed index must be within valid bounds";

        Task task = tasks.get(index);
        assert task != null : "Task retrieved at valid index should not be null";

        task.markUndone();
        assert !task.isDone() : "Task should be marked as undone";
        return task;
    }

    /**
     * Deletes the task selected.
     *
     * @param command command string containing the task number
     * @return the deleted task
     * @throws BagsException if the task number is missing or invalid
     */
    public Task delete(String command) throws BagsException {
        int index = parseTaskIndex(command);
        assert index >= 0 && index < tasks.size() : "Parsed index must be within valid bounds";

        int previousSize = tasks.size();
        Task removedTask = tasks.remove(index);

        assert removedTask != null : "Removed task should not be null";
        assert tasks.size() == previousSize - 1 : "Task list size should decrease by 1 after deletion";
        return removedTask;
    }

    /**
     * Returns the task selected by the task number.
     *
     * @param command command containing the task number
     * @return the selected task
     * @throws BagsException if the task number is missing, invalid,
     *                       or does not correspond to an existing task
     */
    public Task getTask(String command) throws BagsException {
        int index = parseTaskIndex(command);
        assert index >= 0 && index < tasks.size() : "Parsed index must be within valid bounds";

        Task task = tasks.get(index);
        assert task != null : "Task retrieved at valid index should not be null";
        return task;
    }

    /**
     * Parses the command string to extract and validate a task index.
     *
     * @param command raw command input containing the task number
     * @return 0-based index of the selected task
     * @throws BagsException if command format or task index is invalid
     */
    private int parseTaskIndex(String command) throws BagsException {
        assert command != null : "Command string passed to parseTaskIndex should not be null";

        String[] words = command.split(" ");
        if (words.length < 2) {
            throw new BagsException(
                    "Hmm, did you forget a task number? Please enter a value from 1 to "
                            + tasks.size());
        }

        try {
            int taskNumber = Integer.parseInt(words[1]);
            if (taskNumber <= 0 || taskNumber > tasks.size()) {
                throw new BagsException(
                    "Oops! Task does not exist in your bag. Please enter a number from 1 to " + tasks.size());
            }

            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new BagsException(
                    "Hmm, Invalid task number. Please enter a number from 1 to " + tasks.size());
        }
    }

    /**
     * Converts all tasks into formatted records suitable for Storage.
     *
     * @return list of storage-formatted task strings
     */
    public List<String> saveRecords() {
        List<String> records = new ArrayList<>();
        for (Task task : tasks) {
            assert task != null : "Task list should not contain null task elements";
            records.add(task.parseEvent());
        }
        assert records.size() == tasks.size() : "Saved records count must match task list size";
        return records;
    }

    /**
     * Searches for tasks containing the keyword specified in the command string.
     *
     * @param command the user's search command containing the search keyword
     * @return formatted string of matching tasks
     * @throws BagsException if the keyword is missing or blank
     */
    public String search(String command) throws BagsException {
        if (command == null) {
            throw new BagsException("Oops! I can't search an empty command.");
        }

        String[] parts = command.split(" ", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new BagsException("Hmm, did you forget a keyword? Try searching your bag again.");
        }

        String searchKeyword = parts[1].trim().toLowerCase();
        assert !searchKeyword.isEmpty() : "Search keyword should not be empty after validation";

        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(searchKeyword))
                .toList();

        if (matchingTasks.isEmpty()) {
            return "🔎 Oops! I can't find any matching tasks in the bag. Wanna add a task?";
        }

        StringBuilder output = new StringBuilder("🔎 I rummaged through your bag and found these:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            Task task = matchingTasks.get(i);
            output.append(System.lineSeparator())
                    .append(i + 1)
                    .append(".")
                    .append(task);
        }

        return output.toString();
    }

    /**
     * Replaces the selected task with a task of the same type.
     *
     * @param command the user's edit command containing the task number
     * @param replacementTask the task that replaces the selected task
     * @return the replacement task
     * @throws BagsException if the task number is missing, invalid,
     *                       does not correspond to an existing task, or the task types differ
     */
    public Task edit(String command, Task replacementTask) throws BagsException {
        assert replacementTask != null : "Replacement task must not be null";

        int index = parseTaskIndex(command);
        assert index >= 0 && index < tasks.size() : "Parsed index must be within valid bounds";

        Task originalTask = tasks.get(index);
        assert originalTask != null : "Task retrieved at valid index should not be null";

        if (originalTask.getType() != replacementTask.getType()) {
            throw new BagsException("Oops! The replacement task must have the same type as the original task.");
        }

        if (originalTask.isDone()) {
            replacementTask.markDone();
        }

        tasks.set(index, replacementTask);
        assert tasks.get(index) == replacementTask : "Replacement task should be stored at selected index";
        return replacementTask;
    }

    /**
     * Returns the task list in a numbered format.
     *
     * @return formatted task list string
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("🎒 Let me unpack the bag's contents... here you go!");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            output.append(System.lineSeparator())
                    .append(i + 1)
                    .append(".")
                    .append(task);
        }
        output.append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("You have ")
                .append(tasks.size())
                .append(tasks.size() == 1 ? " item" : " items")
                .append(" in your bag.");

        return output.toString();
    }
}
