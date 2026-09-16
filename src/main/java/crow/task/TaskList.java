package crow.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Stores tasks and provides operations for managing them.
 */
public class TaskList {
    private static final Comparator<Task> DESCRIPTION_COMPARATOR =
            Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER);
    private static final Comparator<Deadline> DEADLINE_COMPARATOR =
            Comparator.comparing(Deadline::getDeadlineDateTime)
                    .thenComparing(Deadline::getDescription, String.CASE_INSENSITIVE_ORDER);
    private static final Comparator<Event> EVENT_COMPARATOR =
            Comparator.comparing(Event::getStartDateTime)
                    .thenComparing(Event::getDescription, String.CASE_INSENSITIVE_ORDER);

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        assert containsNoNullTasks(tasks) : "Initial task list must not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
        sortByTypeAndDate();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
        sortByTypeAndDate();
    }

    /**
     * Removes and returns a task.
     *
     * @param index Zero-based index of the task.
     * @return Removed task.
     */
    public Task delete(int index) {
        assert isValidIndex(index) : "Task index must be within the list";
        return tasks.remove(index);
    }

    /**
     * Marks a task as done.
     *
     * @param index Zero-based index of the task.
     */
    public void mark(int index) {
        assert isValidIndex(index) : "Task index must be within the list";
        tasks.get(index).markAsDone();
    }

    /**
     * Marks a task as not done.
     *
     * @param index Zero-based index of the task.
     */
    public void unmark(int index) {
        assert isValidIndex(index) : "Task index must be within the list";
        tasks.get(index).markAsNotDone();
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to find in task descriptions.
     * @return Matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Search keyword must not be blank";
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Checks whether a task with the same type and identifying details exists.
     */
    public boolean containsTaskWithSameDetails(Task task) {
        assert task != null : "Task to compare must not be null";
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetailsAs(task));
    }

    /**
     * Groups tasks by type and sorts each group into a predictable order.
     * ToDos are ordered alphabetically, while dated tasks are ordered by their
     * date and time followed by their description.
     */
    private void sortByTypeAndDate() {
        Stream<Task> sortedTodos = tasks.stream()
                .filter(Todo.class::isInstance)
                .sorted(DESCRIPTION_COMPARATOR);
        Stream<Task> sortedDeadlines = tasks.stream()
                .filter(Deadline.class::isInstance)
                .map(Deadline.class::cast)
                .sorted(DEADLINE_COMPARATOR)
                .map(Task.class::cast);
        Stream<Task> sortedEvents = tasks.stream()
                .filter(Event.class::isInstance)
                .map(Event.class::cast)
                .sorted(EVENT_COMPARATOR)
                .map(Task.class::cast);

        List<Task> sortedTasks = Stream.of(sortedTodos, sortedDeadlines, sortedEvents)
                .flatMap(taskStream -> taskStream)
                .toList();
        assert sortedTasks.size() == tasks.size() : "Every task must have a supported type";

        tasks.clear();
        tasks.addAll(sortedTasks);
    }

    /**
     * Returns a task at the given index.
     *
     * @param index Zero-based index of the task.
     * @return Task at the index.
     */
    public Task get(int index) {
        assert isValidIndex(index) : "Task index must be within the list";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks stored.
     *
     * @return Number of tasks.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Returns a read-only view of all tasks.
     *
     * @return Unmodifiable task list.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Checks whether an index identifies a task in the list.
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Checks that every element in an initial task collection is present.
     */
    private static boolean containsNoNullTasks(List<Task> tasks) {
        for (Task task : tasks) {
            if (task == null) {
                return false;
            }
        }
        return true;
    }
}
