package brucli.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import brucli.ui.Messages;

/**
 * Owns the collection of tasks and operations that change it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Pairs a task with its one-based number shown to the user.
     */
    public record IndexedTask(int number, Task task) {}

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
        reindexTasks();
    }

    /**
     * Adds a todo and assigns it the next available ID.
     */
    public void addTodo(String description) {
        tasks.add(new Todo(tasks.size(), description));
    }

    /**
     * Adds a deadline and assigns it the next available ID.
     */
    public void addDeadline(String description, LocalDateTime due) {
        tasks.add(new Deadline(tasks.size(), description, due));
    }

    /**
     * Adds an event and assigns it the next available ID.
     */
    public void addEvent(
            String description,
            LocalDateTime start,
            LocalDateTime end
    ) {
        tasks.add(new Event(
                tasks.size(),
                description,
                start,
                end
        ));
    }

    /**
     * Marks the task at the zero-based ID as isDone.
     */
    public void markDone(int taskId) {
        getTask(taskId).markDone();
    }

    /**
     * Marks the task at the zero-based ID as not isDone.
     */
    public void unmarkDone(int taskId) {
        getTask(taskId).unmarkDone();
    }

    /**
     * Deletes the task at the zero-based ID and restores consecutive IDs.
     */
    public void delete(int taskId) {
        getTask(taskId);
        tasks.remove(taskId);
        reindexTasks();
    }

    /**
     * Returns tasks matching the filter with their original user-facing numbers.
     */
    public List<IndexedTask> matching(ListFilter filter) {
        ArrayList<IndexedTask> matchingTasks = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.matches(filter)) {
                matchingTasks.add(new IndexedTask(i + 1, task));
            }
        }

        return matchingTasks;
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword Keyword to find in task descriptions.
     * @return Matching tasks with their original user-facing numbers.
     */
    public List<IndexedTask> containing(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("A search keyword is required.");
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        ArrayList<IndexedTask> matchingTasks = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String normalizedDescription = task.description.toLowerCase(Locale.ENGLISH);
            if (normalizedDescription.contains(normalizedKeyword)) {
                matchingTasks.add(new IndexedTask(i + 1, task));
            }
        }

        return matchingTasks;
    }

    /** Returns an immutable snapshot for persistence. */
    public List<Task> snapshot() {
        return List.copyOf(tasks);
    }

    /**
     * Finds a task by its zero-based ID and validates that it exists.
     */

    private Task getTask(int taskId) {
        if (taskId < 0 || taskId >= tasks.size()) {
            throw new IllegalArgumentException(Messages.taskNotFound());
        }

        return tasks.get(taskId);
    }

    /**
     * Updates internal IDs after loading or deleting tasks.
     */
    private void reindexTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).id = i;
        }
    }

    /**
     * Returns a sorted view of the tasks while preserving their original numbers.
     *
     * @param sortField Field used to order the tasks.
     * @return Sorted tasks with their original user-facing numbers.
     */
    public List<IndexedTask> sortedBy(SortField sortField) {
        Comparator<Task> taskComparator = switch (sortField) {
            case DESCRIPTION -> Comparator.comparing(
                    task -> task.description,
                    String.CASE_INSENSITIVE_ORDER
            );
            case DATE -> Comparator.comparing(
                    Task::getDateTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case STATUS -> Comparator.comparing(task -> task.isDone);
        };

        return matching(null).stream()
                .sorted(Comparator.comparing(
                        IndexedTask::task,
                        taskComparator
                ))
                .toList();
    }
}
