package margit.task;

/** Groups the task lists displayed after sorting. */
public class SortedTaskLists {
    private final TaskList todoTasks;
    private final TaskList unscheduledTasks;
    private final TaskList scheduledTasks;

    /** Creates grouped task lists for a sorted task display. */
    SortedTaskLists(TaskList todoTasks, TaskList unscheduledTasks, TaskList scheduledTasks) {
        this.todoTasks = todoTasks;
        this.unscheduledTasks = unscheduledTasks;
        this.scheduledTasks = scheduledTasks;
    }

    /** Returns the todo tasks in their existing order. */
    public TaskList getTodoTasks() {
        return todoTasks;
    }

    /** Returns deadline and event tasks without a parseable date. */
    public TaskList getUnscheduledTasks() {
        return unscheduledTasks;
    }

    /** Returns scheduled deadline and event tasks in chronological order. */
    public TaskList getScheduledTasks() {
        return scheduledTasks;
    }
}
