package wobble.tasks;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/** Stores the tasks created during one Wobble session. */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /** Adds a task to the list. */
    public void add(Task task) {
        validateNewTask(task);
        tasks.add(task);
    }

    /** Inserts a task at a one-based position, primarily for restoring a failed deletion. */
    public void addAt(int oneBasedIndex, Task task) {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size() + 1) {
            throw new IllegalArgumentException("The insertion position is outside the task list");
        }
        validateNewTask(task);
        tasks.add(oneBasedIndex - 1, task);
    }

    /** Returns whether the list already contains a task with the same details. */
    public boolean containsEquivalent(Task candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("The candidate task must not be null");
        }
        assert candidate != null : "The candidate task must not be null";
        return tasks.stream().anyMatch(task -> task.hasSameDetailsAs(candidate));
    }

    /** Returns the number of stored tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns the task at a one-based position, or null if the position is invalid. */
    public Task get(int oneBasedIndex) {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            return null;
        }
        return tasks.get(oneBasedIndex - 1);
    }

    /** Removes and returns the task at a one-based position, or null if invalid. */
    public Task delete(int oneBasedIndex) {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            return null;
        }
        return tasks.remove(oneBasedIndex - 1);
    }

    /** Returns the one-based numbers of tasks whose descriptions contain a keyword. */
    public List<Integer> find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("The search keyword must not be blank");
        }
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .map(index -> index + 1)
                .boxed()
                .toList();
    }

    /**
     * Returns unfinished deadlines and events occurring within a day range.
     * The window starts at the beginning of today so date-only tasks remain visible throughout today.
     */
    public List<Integer> findUpcoming(LocalDateTime now, int days) {
        if (now == null) {
            throw new IllegalArgumentException("The reminder search must have a reference time");
        }
        if (days < 0) {
            throw new IllegalArgumentException("The reminder range must not be negative");
        }
        assert now != null : "The reminder search must have a reference time";
        assert days >= 0 : "The reminder range must not be negative";
        LocalDateTime start = now.toLocalDate().atStartOfDay();
        LocalDateTime end;
        try {
            end = now.plusDays(days);
        } catch (DateTimeException | ArithmeticException exception) {
            throw new IllegalArgumentException("The reminder range is too large", exception);
        }
        return IntStream.range(0, tasks.size())
                .filter(index -> !tasks.get(index).isDone())
                .filter(index -> getScheduledTime(tasks.get(index)) != null)
                .filter(index -> !getScheduledTime(tasks.get(index)).isBefore(start)
                        && !getScheduledTime(tasks.get(index)).isAfter(end))
                .boxed()
                .sorted(Comparator.comparing(index -> getScheduledTime(tasks.get(index))))
                .map(index -> index + 1)
                .toList();
    }

    /** Returns the relevant due or start time for a scheduled task. */
    private static LocalDateTime getScheduledTime(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        if (task instanceof Event event) {
            return event.getFrom();
        }
        return null;
    }

    /** Validates a task before it is inserted into the list. */
    private void validateNewTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("The task list must not contain null tasks");
        }
        assert task != null : "The task list must not contain null tasks";
        if (containsEquivalent(task)) {
            throw new IllegalArgumentException("The task list must not contain duplicate tasks");
        }
    }
}
