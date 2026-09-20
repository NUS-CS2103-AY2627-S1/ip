package fifi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fifi.exception.DuplicateTaskException;
import fifi.exception.ExcessiveTaskException;
import fifi.task.Task;

/**
 * Stores the chatbot's tasks and provides operations on the task list.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list with tasks loaded from storage.
     *
     * @param tasks the tasks to keep in this list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
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
     * Adds a task to the list if the list is not full.
     *
     * @param task the task to add
     * @throws ExcessiveTaskException if the task list already has too many tasks
     * @throws DuplicateTaskException if another task has the same details
     */
    public void add(Task task) throws ExcessiveTaskException, DuplicateTaskException {
        if (tasks.size() >= MAX_TASKS) {
            throw new ExcessiveTaskException(
                    """
                    You have exceeded the cap of 100 tasks! Delete old tasks in order to make space for new tasks.\
                    """);
        }
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
            throw new DuplicateTaskException("Oops! A task with the same details already exists.");
        }
        tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param taskIndex the zero-based task index
     * @return the task at that index
     */
    public Task get(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Deletes and returns the task at the given zero-based index.
     *
     * @param taskIndex the zero-based task index
     * @return the deleted task
     */
    public Task delete(int taskIndex) {
        int previousSize = tasks.size();
        Task removedTask = tasks.remove(taskIndex);
        // Deletion must remove exactly one entry so subsequent task numbers stay consistent.
        assert tasks.size() == previousSize - 1 : "Deleting a task must reduce the task count by one";
        return removedTask;
    }

    /**
     * Returns a task list containing tasks that occur on the given date.
     *
     * @param showDate the date to match against
     * @return a task list containing matching deadlines and events
     */
    public TaskList getTasksOccurringOn(LocalDate showDate) {
        ArrayList<Task> occurringTasks = tasks.stream()
                .filter(task -> task.occursOn(showDate))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(occurringTasks);
    }

    /**
     * Returns a task list containing tasks with descriptions that contain the keyword.
     *
     * @param keyword the keyword to search for
     * @return a task list containing matching tasks
     */
    public TaskList findTasksByKeyword(String keyword) {
        ArrayList<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matchingTasks);
    }

    /**
     * Calculates a current status breakdown for all tasks in the list.
     *
     * @param now date and time at which the statistics are requested
     * @return statistics for every task in the list
     */
    public TaskStatistics getStatistics(LocalDateTime now) {
        return TaskStatistics.calculate(tasks, now.toLocalDate(), 0);
    }

    /**
     * Calculates a current status breakdown for tasks created during the requested period.
     *
     * @param startDate first creation date included in the period
     * @param now end of the period and time at which statuses are classified
     * @return statistics for tasks created from the start date through now
     */
    public TaskStatistics getStatisticsSince(LocalDate startDate, LocalDateTime now) {
        LocalDateTime periodStart = startDate.atStartOfDay();
        List<Task> tasksInPeriod = tasks.stream()
                .filter(task -> task.getCreatedAt()
                        .map(createdAt -> !createdAt.isBefore(periodStart) && !createdAt.isAfter(now))
                        .orElse(false))
                .toList();
        int unknownCreationTimes = (int) tasks.stream()
                .filter(task -> task.getCreatedAt().isEmpty())
                .count();
        return TaskStatistics.calculate(tasksInPeriod, now.toLocalDate(), unknownCreationTimes);
    }

    /**
     * Returns the numbered text shown by the list and show commands.
     *
     * @return the display text for this task list
     */
    public String toDisplayString() {
        StringBuilder taskString = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            taskString.append("\n");
            taskString.append(i + 1)
                    .append(". ")
                    .append(tasks.get(i).toString());
        }
        return taskString.toString();
    }

    /**
     * Returns the underlying tasks as a read-only list for saving.
     *
     * @return the tasks in this list
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Creates an independent task list for staging changes before saving.
     *
     * @return copies of every task, including their completion timestamps
     */
    public TaskList copy() {
        return new TaskList(tasks.stream().map(Task::copy).toList());
    }

    /**
     * Replaces this list with a successfully saved candidate list.
     *
     * @param candidate the task list whose save succeeded
     */
    public void replaceWith(TaskList candidate) {
        List<Task> savedTasks = candidate.asList();
        tasks.clear();
        tasks.addAll(savedTasks);
    }
}
