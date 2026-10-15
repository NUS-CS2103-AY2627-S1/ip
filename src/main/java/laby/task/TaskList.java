package laby.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import laby.LabyException;

/** Stores and provides operations for the application's tasks. */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list backed by the supplied mutable list.
     *
     * @param tasks Mutable list of tasks to manage.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "task list must be present";
        for (Task task : tasks) {
            assert task != null : "task list must not contain null tasks";
        }
        this.tasks = tasks;
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Sets the completion status of the task at {@code index} and returns its display text.
     *
     * @param index Zero-based index of the task to modify.
     * @param isDone Whether the task should be marked as done.
     * @return Display text of the modified task.
     * @throws LabyException If the index does not identify a task.
     */
    public String modifyTaskStatus(int index, boolean isDone) throws LabyException {
        try {
            Task task = this.tasks.get(index);
            task.setDone(isDone);
            return task.toString();
        } catch (IndexOutOfBoundsException e) {
            throw new LabyException("please enter a valid task index.");
        }
    }

    /**
     * Removes the task at {@code index} and returns its display text.
     *
     * @param index Zero-based index of the task to remove.
     * @return Display text of the removed task.
     * @throws LabyException If the index does not identify a task.
     */
    public String deleteTask(int index) throws LabyException {
        try {
            Task task = this.tasks.get(index);
            this.tasks.remove(index);
            return task.toString();
        } catch (IndexOutOfBoundsException e) {
            throw new LabyException("please enter a valid task index.");
        }
    }

    /**
     * Adds an incomplete todo task and returns its display text.
     *
     * @param description Description of the task.
     * @return Display text of the added task.
     */
    public String addTodo(String description) {
        Task task = new Todo(description);
        this.tasks.add(task);
        return task.toString();
    }

    /**
     * Adds an incomplete deadline task and returns its display text.
     *
     * @param description Description of the task.
     * @param deadline Time by which the task should be completed.
     * @return Display text of the added task.
     */
    public String addDeadline(String description, LocalDateTime deadline) {
        Task task = new Deadline(description, deadline);
        this.tasks.add(task);
        return task.toString();
    }

    /**
     * Adds an incomplete event task and returns its display text.
     *
     * @param description Description of the task.
     * @param startTime Start of the event.
     * @param endTime End of the event.
     * @return Display text of the added task.
     */
    public String addEvent(String description, LocalDateTime startTime, LocalDateTime endTime) {
        Task task = new Event(description, startTime, endTime);
        this.tasks.add(task);
        return task.toString();
    }

    /**
     * Returns all tasks numbered in the format shown in the console.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.tasks.size(); i++) {
            result.append((i + 1)).append(".").append(this.tasks.get(i)).append("\n");
        }
        return result.toString();
    }

    /**
     * Returns all tasks in the format used for persistence.
     *
     * @return Serialized representation of all tasks.
     */
    public String toFileString() {
        return this.tasks.stream()
                .map(Task::toFileString)
                .collect(Collectors.joining());
    }

    /**
     * Returns a string of tasks that match the input.
     *
     * @param input Input tasks are filtered on.
     * @return String of tasks.
     */
    public String filterTasks(String input) {
        input = input.toLowerCase().trim();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.tasks.size(); i++) {
            Task task = this.tasks.get(i);

            if (task.getDescription().toLowerCase().contains(input)) {
                result.append((i + 1)).append(".").append(task).append("\n");
            }
        }
        return result.toString();
    }
}
