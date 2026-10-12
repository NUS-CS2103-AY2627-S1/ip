package bogos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Stores and manages the tasks currently known to the application.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing copies of the supplied task references.
     *
     * @param tasks Tasks with which to initialise the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
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
     * Adds a task to this list.
     *
     * @param task Task to add.
     * @throws BogosException If a task with the same user-supplied details already exists.
     */
    public void addTask(Task task) throws BogosException {
        if (containsTaskWithSameDetails(task)) {
            throw new BogosException("Bummer, buplicate bullet. :[");
        }
        tasks.add(task);
    }

    /**
     * Returns whether this list contains a task with the same user-supplied details.
     * Completion status is excluded because it does not distinguish two tasks that
     * represent the same work.
     *
     * @param candidate Task to compare with tasks in this list.
     * @return Whether an equivalent task already exists.
     */
    private boolean containsTaskWithSameDetails(Task candidate) {
        for (Task existingTask : tasks) {
            if (hasSameDetails(existingTask, candidate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether two tasks have the same type, description, tags, and dates.
     *
     * @param firstTask First task to compare.
     * @param secondTask Second task to compare.
     * @return Whether both tasks describe the same work.
     */
    private boolean hasSameDetails(Task firstTask, Task secondTask) {
        if (firstTask.getTaskType() != secondTask.getTaskType()
                || !firstTask.getDescription().equals(secondTask.getDescription())
                || !new HashSet<>(firstTask.getTags()).equals(new HashSet<>(secondTask.getTags()))) {
            return false;
        }

        return switch (firstTask.getTaskType()) {
        case TODO -> true;
        case DEADLINE -> ((Deadline) firstTask).getDueDate()
                .equals(((Deadline) secondTask).getDueDate());
        case EVENT -> ((Event) firstTask).getStartDate()
                .equals(((Event) secondTask).getStartDate())
                && ((Event) firstTask).getEndDate().equals(((Event) secondTask).getEndDate());
        };
    }

    /**
     * Returns the task identified by its one-based number.
     *
     * @param taskNumber One-based position of the task.
     * @return The requested task.
     * @throws BogosException If the task number is outside the list.
     */
    public Task getTask(int taskNumber) throws BogosException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new BogosException("Bummer. Bullet beyond bounds. :[");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes and returns the task identified by its one-based number.
     *
     * @param taskNumber One-based position of the task.
     * @return The removed task.
     * @throws BogosException If the task number is outside the list.
     */
    public Task removeTask(int taskNumber) throws BogosException {
        int originalSize = tasks.size();
        Task task = getTask(taskNumber);
        tasks.remove(task);
        assert tasks.size() == originalSize - 1
                : "Removing one task must reduce the task list size by one.";
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword, ignoring case.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their original order.
     */
    public List<Task> findTasks(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(lowerCaseKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns an unmodifiable snapshot of the tasks for display or saving.
     *
     * @return Snapshot of the tasks in this list.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
