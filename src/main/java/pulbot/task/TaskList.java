package pulbot.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Represents the ordered collection of tasks managed by Pulbot. */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates an independent task list containing the supplied tasks in order. */
    public TaskList(Collection<Task> tasks) {
        assert tasks != null : "A task list cannot be created from a null collection";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a validated task to the end of the list. */
    public void add(Task task) {
        assert task != null : "A task list cannot contain null tasks";
        tasks.add(task);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Restores a removed task at its original position when saving fails. */
    public void insert(int index, Task task) {
        assert task != null : "A task list cannot contain null tasks";
        tasks.add(index, task);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Returns whether the list already contains a task with the same identifying details. */
    public boolean containsDuplicate(Task task) {
        assert task != null : "Only a validated task can be checked for duplication";
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
