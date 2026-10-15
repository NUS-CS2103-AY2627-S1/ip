package sine.task;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import sine.exception.SineException;

/**
 * Owns the chatbot's task collection and provides operations on that collection.
 */
public class TaskList {
    private final List<Task> tasks;
    // Only changes made through this task list during this session enter history.
    private final Deque<TaskChange> history = new ArrayDeque<>();

    /** Stores the inverse operation and the task needed for its confirmation. */
    private record TaskChange(ChangeType type, int index, Task task, boolean wasDone) {
    }

    /** Identifies the task change to reverse without recording another change. */
    private enum ChangeType {
        ADD, DELETE, STATUS
    }

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
        history.push(new TaskChange(ChangeType.ADD, tasks.size() - 1, task, task.isDone()));
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task delete(int index) {
        Task task = tasks.remove(index);
        history.push(new TaskChange(ChangeType.DELETE, index, task, task.isDone()));
        return task;
    }

    /**
     * Updates completion status and records history only when the status changes.
     *
     * @param index Zero-based task index.
     * @param isDone Desired completion status.
     */
    public void setDone(int index, boolean isDone) {
        Task task = tasks.get(index);
        if (task.isDone() == isDone) {
            return;
        }
        boolean wasDone = task.isDone();
        restoreStatus(task, isDone);
        history.push(new TaskChange(ChangeType.STATUS, index, task, wasDone));
    }

    /**
     * Reverses the latest task change in memory without recording the undo itself.
     *
     * @return Action details and the affected task in its restored state.
     * @throws SineException If no change remains to undo in this session.
     */
    public String undo() throws SineException {
        if (history.isEmpty()) {
            throw new SineException("There is nothing to undo.");
        }
        TaskChange change = history.peek();
        String action;
        switch (change.type()) {
            case ADD:
                tasks.remove(change.index());
                action = "I removed this task:";
                break;
            case DELETE:
                tasks.add(change.index(), change.task());
                action = "I added back this task:";
                break;
            case STATUS:
                restoreStatus(change.task(), change.wasDone());
                action = change.wasDone() ? "I marked this task as done:" : "I unmarked this task:";
                break;
            default:
                throw new AssertionError("Every task change must have an inverse");
        }
        history.pop();
        return action + "\n  " + change.task();
    }

    /** Restores completion status without creating history. */
    private void restoreStatus(Task task, boolean isDone) {
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Selected task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword Keyword to find in task descriptions.
     * @return Unmodifiable snapshot of matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only snapshot for storage or display code.
     *
     * @return Snapshot of the current tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
