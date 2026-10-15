package serina.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import serina.exception.SerinaError;
import serina.exception.SerinaException;

/**
 * Stores Serina's tasks and provides operations that act on the task collection.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks, typically loaded from storage.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "TaskList should be created from a task collection.";

        this.tasks = new ArrayList<>(tasks);
        assert this.tasks.size() <= MAX_TASKS : "Initial task lists should be capped before TaskList is created.";
        assert !this.tasks.contains(null) : "TaskList should store only task objects.";
    }

    /**
     * Creates a task list containing the supplied individual tasks.
     *
     * @param tasks Initial tasks in their desired list order.
     */
    public TaskList(Task... tasks) {
        this(List.of(tasks));
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     * @throws SerinaException If the task list has reached its maximum size.
     */
    public void add(Task task) throws SerinaException {
        if (tasks.size() >= MAX_TASKS) {
            throw new SerinaException(SerinaError.MAX_TASKS);
        }
        int duplicateIndex = indexOfDuplicate(task);
        if (duplicateIndex >= 0) {
            throw new SerinaException(SerinaError.DUPLICATE_TASK,
                    "It matches task " + (duplicateIndex + 1) + ".");
        }

        int taskCountBeforeAdd = tasks.size();
        assert task != null : "Only successfully created tasks should be added.";
        assert taskCountBeforeAdd < MAX_TASKS : "Maximum-size validation should reject full task lists.";

        tasks.add(task);
        assert tasks.size() == taskCountBeforeAdd + 1 : "Adding one task should increase the task count by one.";
        assert tasks.size() <= MAX_TASKS : "TaskList should never grow beyond its maximum size.";
    }

    /**
     * Returns the task matching a one-based task number entered by the user.
     *
     * @param taskNumberText One-based task number entered by the user.
     * @return The matching task.
     * @throws SerinaException If the given text is not a valid stored task number.
     */
    public Task getTask(String taskNumberText) throws SerinaException {
        int taskIndex = getTaskIndex(taskNumberText);
        Task task = tasks.get(taskIndex);
        assert task != null : "A validated task number should refer to an existing task.";
        return task;
    }

    /**
     * Removes and returns the task matching a one-based task number.
     *
     * @param taskNumberText One-based task number entered by the user.
     * @return The removed task.
     * @throws SerinaException If the given text is not a valid stored task number.
     */
    public Task delete(String taskNumberText) throws SerinaException {
        int taskCountBeforeDelete = tasks.size();
        Task deletedTask = tasks.remove(getTaskIndex(taskNumberText));
        assert deletedTask != null : "A validated task number should remove an existing task.";
        assert tasks.size() == taskCountBeforeDelete - 1 : "Deleting one task should decrease the task count by one.";
        return deletedTask;
    }

    /**
     * Returns tasks whose descriptions contain every keyword in the query, ignoring case.
     *
     * @param query Whitespace-delimited keywords to match against task descriptions.
     * @return Matching tasks in their original list order.
     */
    public List<Task> find(String query) {
        assert query != null : "Find should receive a query parsed from a command.";

        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
        assert !normalizedQuery.isEmpty() : "Find should be called only after empty queries are rejected.";

        List<String> normalizedKeywords = List.of(normalizedQuery.split("\\s+"));
        return tasks.stream()
                .filter(task -> {
                    String normalizedDescription = task.getDescription().toLowerCase(Locale.ROOT);
                    return normalizedKeywords.stream().allMatch(normalizedDescription::contains);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the current number of tasks.
     *
     * @return The task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view of the tasks for display and persistence.
     *
     * @return An unmodifiable view of the tasks.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Replaces all tasks with independent copies of a validated candidate list.
     *
     * @param replacement Validated replacement list.
     */
    public void replaceWith(TaskList replacement) {
        tasks.clear();
        replacement.tasks.stream().map(Task::copy).forEach(tasks::add);
    }

    /**
     * Returns an independent copy that can be changed before saving.
     *
     * @return Deep copy of this task list.
     */
    public TaskList copy() {
        List<Task> copiedTasks = tasks.stream()
                .map(Task::copy)
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(copiedTasks);
    }

    /**
     * Converts a one-based user task number into a valid zero-based list index.
     *
     * @param taskNumberText Task number entered by the user.
     * @return The corresponding zero-based index.
     * @throws SerinaException If the text is not an integer or refers to a missing task.
     */
    private int getTaskIndex(String taskNumberText) throws SerinaException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText.trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new SerinaException(SerinaError.INVALID_TASK_NUMBER);
            }

            int taskIndex = taskNumber - 1;
            assert taskIndex >= 0 && taskIndex < tasks.size()
                    : "Validated task numbers should map to valid zero-based indexes.";
            return taskIndex;
        } catch (NumberFormatException e) {
            throw new SerinaException(SerinaError.INVALID_TASK_NUMBER);
        }
    }

    private int indexOfDuplicate(Task candidate) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).hasSameIdentity(candidate)) {
                return i;
            }
        }
        return -1;
    }
}
