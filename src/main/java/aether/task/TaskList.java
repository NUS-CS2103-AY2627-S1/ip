package aether.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Owns the ordered collection of tasks and operations performed on that collection. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list containing zero or more supplied tasks.
     *
     * @param tasks tasks to place in the list initially
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks loaded from storage
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task at the end of the list. */
    public void addTask(Task task) {
        assert task != null : "Only a task can be added to the task list.";
        tasks.add(task);
    }

    /**
     * Returns whether the list already contains a task of the same type with the same details.
     * Description matching ignores letter case to avoid near-identical accidental duplicates.
     *
     * @param candidate task whose details should be checked
     * @return true if an equivalent task already exists
     */
    public boolean containsEquivalentTask(Task candidate) {
        return tasks.stream().anyMatch(candidate::hasSameDetails);
    }

    /** Removes and returns the last task, used to undo a failed save after adding a task. */
    public Task removeLastTask() {
        assert !tasks.isEmpty() : "A task must exist before it can be removed.";
        return tasks.remove(tasks.size() - 1);
    }

    /** Marks and returns the task at a 0-based index. */
    public Task markTask(int index) {
        Task task = getTaskAt(index);
        task.markAsDone();
        return task;
    }

    /** Marks as pending and returns the task at a 0-based index. */
    public Task unmarkTask(int index) {
        Task task = getTaskAt(index);
        task.markAsNotDone();
        return task;
    }

    /** Removes and returns the task at a 0-based index. */
    public Task deleteTask(int index) {
        assert hasTaskAt(index) : "The task index must refer to an existing task.";
        return tasks.remove(index);
    }

    /** Restores a task to a 0-based index after a failed save. */
    public void restoreTask(int index, Task task) {
        assert index >= 0 && index <= tasks.size() : "The restore index must be inside the task list.";
        assert task != null : "Only a task can be restored.";
        tasks.add(index, task);
    }

    /** Returns the status of the task at a 0-based index. */
    public TaskStatus getTaskStatus(int index) {
        return getTaskAt(index).getStatus();
    }

    /** Restores the status of the task at a 0-based index after a failed save. */
    public void setTaskStatus(int index, TaskStatus status) {
        getTaskAt(index).setStatus(status);
    }

    /** Returns the number of stored tasks. */
    public int getTaskCount() {
        return tasks.size();
    }

    /** Returns an immutable view of the tasks for storage. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Sorts dated tasks from the earliest relevant date to the latest. Events use their start date;
     * tasks without a date are placed last. Tasks with the same date keep their previous order.
     *
     * @return the order before sorting, so it can be restored if saving fails
     */
    public List<Task> sortByDate() {
        List<Task> previousOrder = List.copyOf(tasks);
        tasks.sort(Comparator.comparing(task -> task.getSortDate().orElse(LocalDate.MAX)));
        return previousOrder;
    }

    /** Restores the entire task list to an earlier order after an unsuccessful save. */
    public void restoreOrder(List<Task> previousOrder) {
        assert previousOrder != null : "A previous task order must be provided.";
        tasks.clear();
        tasks.addAll(previousOrder);
    }

    /** Returns the numbered task list in the form shown to the user. */
    public String formatTaskList() {
        StringBuilder list = new StringBuilder("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            list.append('\n').append(index + 1).append('.').append(tasks.get(index));
        }
        return list.toString();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, using their numbers in the full task list.
     * Matching ignores letter case so that, for example, {@code find BOOK} finds {@code read book}.
     *
     * @param keyword the text to search for in task descriptions
     * @return the numbered matching tasks in the form shown to the user
     */
    public String formatMatchingTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        StringBuilder matches = new StringBuilder("Here are the matching tasks in your list:");
        String matchingTasks = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining("\n"));
        if (!matchingTasks.isEmpty()) {
            matches.append('\n').append(matchingTasks);
        }
        return matches.toString();
    }

    /** Returns the task at a validated 0-based index. */
    private Task getTaskAt(int index) {
        assert hasTaskAt(index) : "The task index must refer to an existing task.";
        return tasks.get(index);
    }

    /** Returns whether a 0-based index identifies one of the stored tasks. */
    private boolean hasTaskAt(int index) {
        return index >= 0 && index < tasks.size();
    }
}
