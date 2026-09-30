package marcus.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores Marcus's tasks and provides task-list operations.
 */
public class TaskList {
    /** Maximum number of tasks held by the current in-memory task list. */
    public static final int CAPACITY = 100;
    private static final String INDENT = "     ";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    private final Task[] tasks;
    private int size;

    /** Creates an empty task list. */
    public TaskList() {
        this(new Task[CAPACITY]);
    }

    /**
     * Creates a task list from an array whose populated entries appear first.
     *
     * @param tasks tasks to store.
     */
    public TaskList(Task[] tasks) {
        assert tasks != null : "Task storage must not be null";
        this.tasks = tasks;
        while (size < tasks.length && tasks[size] != null) {
            size++;
        }
        for (int index = size; index < tasks.length; index++) {
            assert tasks[index] == null : "Task storage must be packed from the beginning";
        }
    }

    /**
     * Reports whether this task list has reached its capacity.
     *
     * @return whether this task list is full.
     */
    public boolean isFull() {
        return size == tasks.length;
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return number of tasks in this list.
     */
    public int size() {
        return size;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert !isFull() : "Cannot add a task to a full task list";
        assert task != null : "A task list must not store a null task";
        tasks[size] = task;
        size++;
    }

    /**
     * Reports whether a task with the same type and details is already present.
     *
     * @param candidate task to compare with existing tasks.
     * @return whether an equivalent task is already stored.
     */
    public boolean containsEquivalentTask(Task candidate) {
        assert candidate != null : "The candidate task must not be null";
        return IntStream.range(0, size)
                .anyMatch(index -> tasks[index].toFileString().equals(candidate.toFileString()));
    }

    /**
     * Returns the task at a one-based user-facing task number.
     *
     * @param taskNumber one-based task number.
     * @return the matching task, or {@code null} when the number is invalid.
     */
    public Task get(int taskNumber) {
        if (taskNumber < 1 || taskNumber > size) {
            return null;
        }
        return tasks[taskNumber - 1];
    }

    /**
     * Removes the task at a one-based user-facing task number.
     *
     * @param taskNumber one-based task number.
     * @return removed task, or {@code null} when the number is invalid.
     */
    public Task remove(int taskNumber) {
        Task removedTask = get(taskNumber);
        if (removedTask == null) {
            return null;
        }
        for (int index = taskNumber - 1; index < size - 1; index++) {
            tasks[index] = tasks[index + 1];
        }
        tasks[size - 1] = null;
        size--;
        return removedTask;
    }

    /**
     * Formats the current task list with its completion markers.
     *
     * @return formatted task-list text.
     */
    public String toDisplayString() {
        String numberedTasks = IntStream.range(0, size)
                .mapToObj(index -> INDENT + (index + 1) + "." + tasks[index])
                .collect(Collectors.joining("\n"));
        return INDENT + "Here are the tasks in your list:\n"
                + (numberedTasks.isEmpty() ? "" : numberedTasks + "\n");
    }

    /**
     * Formats tasks that occur on a specified date, retaining their task-list numbers.
     *
     * @param date date to match.
     * @return formatted matching tasks or a message when none match.
     */
    public String tasksOnDateToString(LocalDate date) {
        String formattedDate = date.format(DISPLAY_DATE_FORMAT);
        return formatNumberedTasks("Here are the tasks occurring on " + formattedDate + ":",
                task -> task.occursOn(date), "There are no tasks occurring on " + formattedDate + ".", false);
    }

    /**
     * Formats tasks whose descriptions contain a keyword, retaining their task-list numbers.
     *
     * @param keyword keyword to match.
     * @return formatted matching tasks or a message when none match.
     */
    public String tasksMatchingKeywordToString(String keyword) {
        return formatNumberedTasks("Here are the matching tasks in your list:",
                task -> task.matchesKeyword(keyword), "There are no matching tasks in your list.", false);
    }

    /**
     * Formats tasks that have a specified tag, retaining their task-list numbers.
     *
     * @param tag tag to match.
     * @return formatted matching tasks or a message when none match.
     */
    public String tasksMatchingTagToString(String tag) {
        return formatNumberedTasks("Here are the tasks tagged " + tag + ":",
                task -> task.hasTag(tag), "There are no tasks tagged " + tag + ".", true);
    }

    /**
     * Returns all tasks in file format, one task per line.
     *
     * @return serialized task list.
     */
    public String toFileString() {
        String savedTasks = IntStream.range(0, size)
                .mapToObj(index -> tasks[index].toFileString())
                .collect(Collectors.joining(System.lineSeparator()));
        return savedTasks.isEmpty() ? "" : savedTasks + System.lineSeparator();
    }

    /**
     * Formats numbered tasks selected by the supplied condition.
     *
     * @param heading heading shown before the matching tasks.
     * @param condition condition used to select tasks.
     * @param emptyMessage message shown when no tasks match.
     * @return formatted task-list text.
     */
    private String formatNumberedTasks(String heading, Predicate<Task> condition, String emptyMessage,
            boolean shouldShowTags) {
        String matchingTasks = IntStream.range(0, size)
                .filter(index -> condition.test(tasks[index]))
                .mapToObj(index -> INDENT + (index + 1) + "."
                        + (shouldShowTags ? tasks[index] : tasks[index].toStringWithoutTags()))
                .collect(Collectors.joining("\n"));
        String taskContent = matchingTasks.isEmpty() ? INDENT + emptyMessage : matchingTasks;
        return INDENT + heading + "\n" + taskContent + "\n";
    }
}
