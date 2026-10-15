package dude.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Encapsulates the collection of tasks managed by the application.
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
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks Tasks to place in the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(Objects.requireNonNull(tasks, "tasks"));
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(Objects.requireNonNull(task, "task"));
    }

    /**
     * Inserts a task at a zero-based index.
     *
     * @param index Zero-based insertion index.
     * @param task  Task to insert.
     */
    public void insert(int index, Task task) {
        checkPositionIndex(index);
        tasks.add(index, Objects.requireNonNull(task, "task"));
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Task at the requested index.
     * @throws IndexOutOfBoundsException If the index is outside this list.
     */
    public Task get(int index) {
        checkElementIndex(index);
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     * @throws IndexOutOfBoundsException If the index is outside this list.
     */
    public Task remove(int index) {
        checkElementIndex(index);
        return tasks.remove(index);
    }

    /**
     * Replaces a task at a zero-based index and returns the previous task.
     *
     * @param index Zero-based replacement index.
     * @param task  Replacement task.
     * @return Task that was replaced.
     */
    public Task replace(int index, Task task) {
        checkElementIndex(index);
        return tasks.set(index, Objects.requireNonNull(task, "task"));
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a sequential stream over the tasks.
     *
     * @return Stream of tasks.
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword.
     *
     * @param keyword Keyword to search for, case-insensitively.
     * @return Matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return findMatches(keyword).stream().map(TaskMatch::task).toList();
    }

    /**
     * Returns matching tasks together with their original list positions.
     *
     * @param keyword Keyword to search for, case-insensitively.
     * @return Matching tasks and their zero-based positions in original order.
     */
    public List<TaskMatch> findMatches(String keyword) {
        Objects.requireNonNull(keyword, "keyword");
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription()
                        .toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .mapToObj(index -> new TaskMatch(index, tasks.get(index)))
                .toList();
    }

    /**
     * Returns indexes of tasks whose normalized descriptions are equal.
     *
     * @param description Description to compare.
     * @return Matching zero-based indexes in their original order.
     */
    public List<Integer> findDuplicateIndexes(String description) {
        String normalizedDescription = normalizeDescription(description);
        return java.util.stream.IntStream.range(0, tasks.size())
                .filter(index -> normalizeDescription(tasks.get(index).getDescription())
                        .equals(normalizedDescription))
                .boxed()
                .toList();
    }

    /**
     * Normalizes description whitespace and case for duplicate comparison.
     *
     * @param description Description to normalize.
     * @return Trimmed, whitespace-collapsed, lower-case description.
     */
    public static String normalizeDescription(String description) {
        Objects.requireNonNull(description, "description");
        return description.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns a read-only snapshot for persistence.
     *
     * @return Unmodifiable snapshot of the tasks.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Checks an index used to access an existing element.
     *
     * @param index Candidate element index.
     */
    private void checkElementIndex(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task index: " + index);
        }
    }

    /**
     * Checks an index used to insert a new element.
     *
     * @param index Candidate insertion index.
     */
    private void checkPositionIndex(int index) {
        if (index < 0 || index > tasks.size()) {
            throw new IndexOutOfBoundsException("Task insertion index: " + index);
        }
    }
}
