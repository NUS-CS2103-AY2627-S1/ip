package jeff.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;
/**
 * Stores and manages the user's tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;
    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task list must not be null";
        this.tasks = tasks;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Retrieves the task at the specified index.
     *
     * @param index zero-based index of the task
     * @return task at the specified index
     */
    public Task getTask(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task index must be within the task list";
        return tasks.get(index);
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add
     */
    public void addTask(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Deletes and returns the task at the specified index.
     *
     * @param index zero-based index of the task
     * @return deleted task
     */
    public Task deleteTask(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task index must be within the task list";
        return tasks.remove(index);
    }

    /**
     * Updates one detail of the task at the specified index.
     *
     * @param index Zero-based index of the task.
     * @param field Task detail to update.
     * @param value Replacement value.
     * @return Updated task.
     * @throws IllegalArgumentException If the field is incompatible with the task.
     */
    public Task updateTask(int index, UpdateField field, String value) {
        assert index >= 0 && index < tasks.size()
                : "Task index must be within the task list";
        assert field != null : "Update field must not be null";
        assert value != null && !value.isBlank()
                : "Update value must not be blank";

        Task originalTask = tasks.get(index);
        Task updatedTask = createUpdatedTask(
                originalTask, field, value);

        if (originalTask.isDone()) {
            updatedTask.markAsDone();
        }

        tasks.set(index, updatedTask);
        return updatedTask;
    }

    private Task createUpdatedTask(
            Task task, UpdateField field, String value) {
        if (task instanceof Todo) {
            return updateTodo(field, value);
        }

        if (task instanceof Deadline) {
            return updateDeadline((Deadline) task, field, value);
        }

        if (task instanceof Event) {
            return updateEvent((Event) task, field, value);
        }

        throw new IllegalArgumentException(
                "That task type cannot be updated.");
    }

    private Todo updateTodo(UpdateField field, String value) {
        if (field != UpdateField.DESCRIPTION) {
            throw new IllegalArgumentException(
                    "A todo can only update /description.");
        }

        return new Todo(value);
    }

    private Deadline updateDeadline(
            Deadline deadline, UpdateField field, String value) {
        switch (field) {
            case DESCRIPTION:
                return new Deadline(value, deadline.getDueDate());
            case DUE_DATE:
                return new Deadline(
                        deadline.getDescription(), parseDate(value));
            default:
                throw new IllegalArgumentException(
                        "A deadline can only update /description or /by.");
        }
    }

    private Event updateEvent(
            Event event, UpdateField field, String value) {
        switch (field) {
            case DESCRIPTION:
                return new Event(
                        value,
                        event.getStartTime(),
                        event.getEndTime());
            case START_TIME:
                return new Event(
                        event.getDescription(),
                        value,
                        event.getEndTime());
            case END_TIME:
                return new Event(
                        event.getDescription(),
                        event.getStartTime(),
                        value);
            default:
                throw new IllegalArgumentException(
                        "An event can only update "
                                + "/description, /from, or /to.");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Please enter the date in YYYY-MM-DD format.");
        }
    }

    /**
     * Returns all tasks in the list.
     *
     * @return underlying collection of tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Finds tasks whose descriptions contain the specified keyword.
     * The search is case-insensitive.
     *
     * @param keyword keyword to search for
     * @return tasks with descriptions containing the keyword
     */
    public ArrayList<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);

        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
