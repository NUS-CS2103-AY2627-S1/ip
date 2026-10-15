package et.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Stores and provides controlled access to ET's tasks.
 */
public class TaskList {
    /** The tasks currently managed by ET. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the tasks to manage
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task collection must be provided";
        assert tasks.stream().noneMatch(task -> task == null) : "Initial task collection must not contain null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void addTask(Task task) {
        assert task != null : "Task to add must be provided";
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based position.
     *
     * @param taskIndex the zero-based task position
     * @return the task at the given position
     */
    public Task getTask(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Removes and returns the task at a zero-based position.
     *
     * @param taskIndex the zero-based task position
     * @return the task that was removed
     */
    public Task removeTask(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword, ignoring letter case.
     *
     * @param keyword the text to search for
     * @return matching tasks in their current list order
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns canonical task numbers in chronological display order without rearranging the task list.
     *
     * <p>Incomplete tasks precede completed tasks. Within each status group, deadlines use their due time,
     * events use their start time, and undated to-dos follow dated tasks. Canonical task number resolves ties.</p>
     *
     * @return one-based canonical task numbers in chronological display order
     */
    public List<Integer> getChronologicallySortedTaskNumbers() {
        List<Integer> taskNumbers = new ArrayList<>();
        for (int taskNumber = 1; taskNumber <= tasks.size(); taskNumber++) {
            taskNumbers.add(taskNumber);
        }

        taskNumbers.sort(Comparator
                .comparing((Integer taskNumber) -> getTask(taskNumber - 1).isDone())
                .thenComparing(taskNumber -> getTask(taskNumber - 1).getTaskType() == TaskType.TODO)
                .thenComparing(taskNumber -> getChronologicalDateTime(getTask(taskNumber - 1)))
                .thenComparingInt(Integer::intValue));
        return List.copyOf(taskNumbers);
    }

    /**
     * Returns the date and time used to position a task chronologically.
     *
     * @param task the task whose chronological value is needed
     * @return the deadline due time, event start time, or the minimum date-time for an undated to-do
     */
    private LocalDateTime getChronologicalDateTime(Task task) {
        return switch (task.getTaskType()) {
        case TODO -> LocalDateTime.MIN;
        case DEADLINE -> {
            assert task instanceof Deadline : "Deadline task type must use the Deadline class";
            yield ((Deadline) task).getBy();
        }
        case EVENT -> {
            assert task instanceof Event : "Event task type must use the Event class";
            yield ((Event) task).getFrom();
        }
        };
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable snapshot of the tasks for display or persistence.
     *
     * @return the current tasks in list order
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
