package fifi;

import java.time.LocalDate;
import java.util.List;

import fifi.task.Deadline;
import fifi.task.Event;
import fifi.task.Task;
import fifi.task.ToDo;

/**
 * Stores a mutually exclusive status breakdown of a group of tasks.
 */
public class TaskStatistics {
    private final int totalTasks;
    private final int completedTasks;
    private final int todoCompleted;
    private final int todoIncomplete;
    private final int deadlineCompleted;
    private final int deadlinePending;
    private final int deadlineOverdue;
    private final int eventCompleted;
    private final int eventUpcoming;
    private final int eventOngoing;
    private final int eventPast;
    private final int excludedUnknownCreationTimes;

    private TaskStatistics(List<Task> tasks, LocalDate today, int excludedUnknownCreationTimes) {
        this.totalTasks = tasks.size();
        this.completedTasks = countTasks(tasks, Task::isMarked);
        this.todoCompleted = countTasks(tasks, task -> task instanceof ToDo && task.isMarked());
        this.todoIncomplete = countTasks(tasks, task -> task instanceof ToDo && !task.isMarked());
        this.deadlineCompleted = countTasks(tasks, task -> task instanceof Deadline && task.isMarked());
        this.deadlinePending = countTasks(tasks, task -> task instanceof Deadline deadline
                && !task.isMarked() && !deadline.getDueDate().isBefore(today));
        this.deadlineOverdue = countTasks(tasks, task -> task instanceof Deadline deadline
                && !task.isMarked() && deadline.getDueDate().isBefore(today));
        this.eventCompleted = countTasks(tasks, task -> task instanceof Event && task.isMarked());
        this.eventUpcoming = countTasks(tasks, task -> task instanceof Event event
                && !task.isMarked() && today.isBefore(event.getStart()));
        this.eventOngoing = countTasks(tasks, task -> task instanceof Event event
                && !task.isMarked() && !today.isBefore(event.getStart()) && !today.isAfter(event.getEnd()));
        this.eventPast = countTasks(tasks, task -> task instanceof Event event
                && !task.isMarked() && today.isAfter(event.getEnd()));
        this.excludedUnknownCreationTimes = excludedUnknownCreationTimes;
    }

    /**
     * Calculates statistics for the given tasks as of the supplied date.
     *
     * @param tasks tasks included in the requested period
     * @param today date used to classify deadlines and events
     * @param excludedUnknownCreationTimes number of legacy tasks omitted because their creation time is unknown
     * @return the calculated task statistics
     */
    public static TaskStatistics calculate(List<Task> tasks, LocalDate today, int excludedUnknownCreationTimes) {
        return new TaskStatistics(tasks, today, excludedUnknownCreationTimes);
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getIncompleteTasks() {
        return totalTasks - completedTasks;
    }

    public int getCompletionRate() {
        if (totalTasks == 0) {
            return 0;
        }
        return (int) Math.round(completedTasks * 100.0 / totalTasks);
    }

    public int getTodoCompleted() {
        return todoCompleted;
    }

    public int getTodoIncomplete() {
        return todoIncomplete;
    }

    public int getDeadlineCompleted() {
        return deadlineCompleted;
    }

    public int getDeadlinePending() {
        return deadlinePending;
    }

    public int getDeadlineOverdue() {
        return deadlineOverdue;
    }

    public int getEventCompleted() {
        return eventCompleted;
    }

    public int getEventUpcoming() {
        return eventUpcoming;
    }

    public int getEventOngoing() {
        return eventOngoing;
    }

    public int getEventPast() {
        return eventPast;
    }

    public int getExcludedUnknownCreationTimes() {
        return excludedUnknownCreationTimes;
    }

    /**
     * Returns a multiline summary suitable for both console and graphical interfaces.
     *
     * @param heading description of the tasks included in these statistics
     * @return formatted statistics with an optional legacy-data note
     */
    public String toDisplayString(String heading) {
        String summary = String.format("""
                %s
                Total tasks: %d
                Completed tasks: %d
                Incomplete tasks: %d
                Completion rate: %d%%

                Todos:
                Completed: %d
                Incomplete: %d

                Deadlines:
                Completed: %d
                Pending: %d
                Overdue: %d

                Events:
                Completed: %d
                Upcoming: %d
                Ongoing: %d
                Past: %d""",
                heading, totalTasks, completedTasks, getIncompleteTasks(), getCompletionRate(),
                todoCompleted, todoIncomplete, deadlineCompleted, deadlinePending, deadlineOverdue,
                eventCompleted, eventUpcoming, eventOngoing, eventPast);
        if (excludedUnknownCreationTimes == 0) {
            return summary;
        }
        return summary + String.format("%n%nExcluded legacy tasks with unknown creation times: %d",
                excludedUnknownCreationTimes);
    }

    private static int countTasks(List<Task> tasks, java.util.function.Predicate<Task> condition) {
        return (int) tasks.stream().filter(condition).count();
    }
}
