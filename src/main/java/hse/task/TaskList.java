package hse.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Stores tasks and provides operations that change or query the task collection. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "A task list cannot contain null tasks.";
        tasks.add(task);
    }

    /** Returns the task at a zero-based index. */
    public Task get(int index) {
        assertValidIndex(index);
        return tasks.get(index);
    }

    /** Removes and returns the task at a zero-based index. */
    public Task delete(int index) {
        assertValidIndex(index);
        return tasks.remove(index);
    }

    /** Marks and returns the task at a zero-based index. */
    public Task mark(int index) {
        Task task = get(index);
        task.markAsDone();
        return task;
    }

    /** Unmarks and returns the task at a zero-based index. */
    public Task unmark(int index) {
        Task task = get(index);
        task.markAsNotDone();
        return task;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns a copy of all tasks, preserving their display order. */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /** Returns tasks whose descriptions contain the supplied keyword. */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toList());
    }

    /** Sorts dated tasks chronologically while leaving undated tasks at the end. */
    public void sortByDate() {
        tasks.sort(Comparator.comparing(Task::getSortDateTime));
    }

    /** Returns tasks that are deadlines or events occurring on a supplied date. */
    public List<Task> getTasksOn(LocalDate date) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (occursOn(task, date)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /** Returns the one-based display positions of tasks that occur on a supplied date. */
    public List<Integer> getTaskPositionsOn(LocalDate date) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (occursOn(task, date)) {
                positions.add(i + 1);
            }
        }
        return positions;
    }

    /** Returns whether a deadline or event occurs on a supplied date. */
    private boolean occursOn(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            return ((Deadline) task).isOn(date);
        }
        if (task instanceof Event) {
            return ((Event) task).occursOn(date);
        }
        return false;
    }

    /** Asserts that an index refers to an existing task. */
    private void assertValidIndex(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must identify an existing task.";
    }
}
