package taterror.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The in-memory list of tasks, and the operations to add, remove, look up, and
 * search them. Owns no persistence logic itself - see {@link taterror.storage.Storage}
 * for loading/saving.
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
     * Creates a task list pre-populated with {@code initialTasks} (e.g. as loaded
     * by {@link taterror.storage.Storage#load()}).
     */
    public TaskList(List<Task> initialTasks) {
        assert initialTasks != null : "initialTasks should never be null; pass an empty list instead";
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Appends {@code task} to the end of the list.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task";
        tasks.add(task);
    }

    /**
     * Appends zero or more tasks to the end of the list, in the order given.
     */
    public void addAll(Task... tasks) {
        for (Task task : tasks) {
            assert task != null : "Cannot add a null task";
            this.tasks.add(task);
        }
    }

    /**
     * Removes and returns the task at {@code index}.
     */
    public Task remove(int index) {
        assert isValidIndex(index) : "Caller must check isValidIndex before remove; index=" + index;
        return tasks.remove(index);
    }

    public Task get(int index) {
        assert isValidIndex(index) : "Caller must check isValidIndex before get; index=" + index;
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether {@code index} refers to an actual task in this list
     * (i.e. is safe to pass to {@link #get} or {@link #remove}).
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns a live, mutable view of all tasks, in list order. Intended for
     * {@link taterror.storage.Storage#save}; callers elsewhere should prefer
     * {@link #get}.
     */
    public List<Task> asList() {
        return tasks;
    }

    /**
     * Returns every task whose description contains {@code keyword}, in list
     * order.
     */
    public List<Task> findByKeyword(String keyword) {
        assert keyword != null : "keyword should never be null; caller must supply real text";
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Returns whether this list already has a task matching {@code candidate}'s
     * type, description, and type-specific fields (e.g. the same {@code /by}
     * date, or the same {@code /from}/{@code /to}) - i.e. {@code candidate}
     * would be a duplicate if added. Done-status and priority aren't compared,
     * since neither affects what the task actually represents.
     */
    public boolean hasDuplicateOf(Task candidate) {
        assert candidate != null : "candidate should never be null";
        return tasks.stream().anyMatch(task -> task.getTypeCode().equals(candidate.getTypeCode())
                && task.getDescription().equals(candidate.getDescription())
                && task.toSaveDetail().equals(candidate.toSaveDetail()));
    }
}
