package harold.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import harold.HaroldException;

/**
 * Owns Harold's task collection and provides operations that modify it.
 */
public class TaskList {
    private static final int MAX_TASK_COUNT = 100;
    private static final double MINIMUM_SIMILARITY = 0.5;

    private final List<Task> tasks;
    private final int discardedTaskCount;

    /**
     * Creates a task list from zero or more tasks.
     *
     * @param initialTasks Tasks with which to initialize the list.
     */
    public TaskList(Task... initialTasks) {
        this(List.of(initialTasks));
    }

    /**
     * Creates a task list containing up to the supported number of initial tasks.
     *
     * @param initialTasks Tasks with which to initialize the list.
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>();
        int discardedCount = 0;
        for (Task task : initialTasks) {
            if (tasks.size() >= MAX_TASK_COUNT || containsTaskWithSameDetails(task)) {
                discardedCount++;
            } else {
                tasks.add(task);
            }
        }
        discardedTaskCount = discardedCount;
        assert tasks.size() <= MAX_TASK_COUNT
                : "Task list must not exceed its maximum capacity after initialization";
        assert discardedTaskCount >= 0
                : "Discarded task count must not be negative";
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     * @throws HaroldException If the task list is full or already contains the task.
     */
    public void add(Task task) throws HaroldException {
        if (tasks.size() >= MAX_TASK_COUNT) {
            throw new HaroldException(
                    "Your task list is full. Complete some tasks before adding more."
            );
        }
        if (containsTaskWithSameDetails(task)) {
            throw new HaroldException("That task is already in your list.");
        }

        int previousTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousTaskCount + 1
                : "Adding one task must increase the task count by one";
        assert tasks.size() <= MAX_TASK_COUNT
                : "Adding a task must preserve the capacity limit";
    }

    /**
     * Deletes and returns the task at the specified zero-based index.
     *
     * @param index Zero-based task index.
     * @return Deleted task.
     * @throws IndexOutOfBoundsException If the index does not identify a task.
     */
    public Task delete(int index) {
        int previousTaskCount = tasks.size();
        Task deletedTask = tasks.remove(index);
        assert tasks.size() == previousTaskCount - 1
                : "Deleting one task must decrease the task count by one";
        return deletedTask;
    }

    /**
     * Restores a task at its previous index after a failed persistence operation.
     *
     * @param index Zero-based index at which the task previously appeared.
     * @param task Task to restore.
     */
    public void restoreDeletedTask(int index, Task task) {
        assert index >= 0 && index <= tasks.size()
                : "A deleted task must be restored at a valid insertion index";
        tasks.add(index, task);
    }

    /**
     * Returns whether the list already contains the task's identifying details.
     */
    private boolean containsTaskWithSameDetails(Task task) {
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Marks and returns the task at the specified zero-based index.
     *
     * @param index Zero-based task index.
     * @return Marked task.
     * @throws IndexOutOfBoundsException If the index does not identify a task.
     */
    public Task mark(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at the specified zero-based index.
     *
     * @param index Zero-based task index.
     * @return Unmarked task.
     * @throws IndexOutOfBoundsException If the index does not identify a task.
     */
    public Task unmark(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword Keyword for which to search.
     * @return Matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the tasks most similar to a keyword based on character edit distance.
     *
     * <p>Each keyword is compared with both the full description and its individual
     * words. Results below the minimum useful similarity are omitted.</p>
     *
     * @param keyword Keyword for which to find similar descriptions.
     * @param limit Maximum number of tasks to return.
     * @return Similar tasks ordered from highest to lowest similarity.
     */
    public List<Task> findSimilar(String keyword, int limit) {
        if (keyword.isBlank() || limit <= 0) {
            return List.of();
        }

        Comparator<Task> bySimilarity = Comparator.comparingDouble(
                task -> calculateSimilarity(keyword, task.getDescription()));
        return tasks.stream()
                .filter(task -> calculateSimilarity(keyword, task.getDescription())
                        >= MINIMUM_SIMILARITY)
                .sorted(bySimilarity.reversed())
                .limit(limit)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the best normalized similarity between a keyword and a description.
     */
    private static double calculateSimilarity(String keyword, String description) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        double bestSimilarity = calculateNormalizedSimilarity(
                normalizedKeyword, normalizedDescription);

        String[] keywordWords = normalizedKeyword.split("[^a-z0-9]+");
        String[] descriptionWords = normalizedDescription.split("[^a-z0-9]+");
        for (String keywordWord : keywordWords) {
            for (String descriptionWord : descriptionWords) {
                if (!keywordWord.isEmpty() && !descriptionWord.isEmpty()) {
                    bestSimilarity = Math.max(
                            bestSimilarity,
                            calculateNormalizedSimilarity(keywordWord, descriptionWord));
                }
            }
        }
        assert bestSimilarity >= 0.0 && bestSimilarity <= 1.0
                : "Normalized similarity must remain between zero and one";
        return bestSimilarity;
    }

    /**
     * Converts Levenshtein edit distance to a similarity from zero to one.
     */
    private static double calculateNormalizedSimilarity(String first, String second) {
        int maximumLength = Math.max(first.length(), second.length());
        if (maximumLength == 0) {
            return 1.0;
        }
        return 1.0 - (double) calculateEditDistance(first, second) / maximumLength;
    }

    /**
     * Calculates the minimum character insertions, deletions, and substitutions
     * required to transform one string into another.
     */
    private static int calculateEditDistance(String first, String second) {
        int[] previousDistances = new int[second.length() + 1];
        for (int j = 0; j <= second.length(); j++) {
            previousDistances[j] = j;
        }

        for (int i = 1; i <= first.length(); i++) {
            int[] currentDistances = new int[second.length() + 1];
            currentDistances[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int substitutionCost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                currentDistances[j] = Math.min(
                        Math.min(currentDistances[j - 1] + 1, previousDistances[j] + 1),
                        previousDistances[j - 1] + substitutionCost);
            }
            previousDistances = currentDistances;
        }
        return previousDistances[second.length()];
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Zero-based task index.
     * @return Task at the specified index.
     * @throws IndexOutOfBoundsException If the index does not identify a task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the number of initial tasks omitted because the list was full.
     *
     * @return Number of discarded initial tasks.
     */
    public int getDiscardedTaskCount() {
        return discardedTaskCount;
    }
}
