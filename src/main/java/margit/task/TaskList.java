package margit.task;

import java.util.Arrays;
import java.util.Comparator;

/** Stores the application's tasks and performs basic list operations. */
public class TaskList {
    private static final int CAPACITY = 100;
    private static final Comparator<Task> SCHEDULED_TASK_COMPARATOR = Comparator
            .comparing(Task::getScheduledDateTime)
            .thenComparingInt(TaskList::getScheduledTaskTypeOrder);
    private final Task[] tasks = new Task[CAPACITY];
    private int size;

    /** Returns the number of tasks currently in the list. */
    public int size() {
        return size;
    }

    /** Returns whether no further tasks can be added to the list. */
    public boolean isFull() {
        return size == CAPACITY;
    }

    /** Returns the task at the specified zero-based index. */
    public Task get(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        return tasks[index];
    }

    /**
     * Appends one or more tasks to the end of the list.
     *
     * @param tasksToAdd tasks to append, in order
     */
    public void add(Task... tasksToAdd) {
        assert size + tasksToAdd.length <= CAPACITY : "Task additions must not exceed list capacity";
        for (Task task : tasksToAdd) {
            assert task != null : "A task list cannot contain null tasks";
            tasks[size] = task;
            size++;
        }
    }

    /** Removes and returns the task at the specified zero-based index. */
    public Task remove(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        Task removed = tasks[index];
        for (int i = index; i < size - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[size - 1] = null;
        size--;
        return removed;
    }

    /** Returns the tasks whose descriptions contain {@code keyword}, in list order. */
    public TaskList findByKeyword(String keyword) {
        TaskList matchingTasks = new TaskList();
        Arrays.stream(tasks, 0, size)
                .filter(task -> task.hasKeyword(keyword))
                .forEach(matchingTasks::add);
        return matchingTasks;
    }

    /** Sorts this list permanently and returns the groups used to display the sorted tasks. */
    public SortedTaskLists sort(SortOrder sortOrder) {
        if (sortOrder != SortOrder.ASCENDING) {
            throw new IllegalArgumentException("Unsupported sort order: " + sortOrder);
        }

        TaskList todoTasks = new TaskList();
        TaskList unscheduledTasks = new TaskList();
        TaskList scheduledTasks = new TaskList();
        classifyTasks(todoTasks, unscheduledTasks, scheduledTasks);
        sortScheduledTasks(scheduledTasks);
        replaceTasks(todoTasks, unscheduledTasks, scheduledTasks);

        return new SortedTaskLists(todoTasks, unscheduledTasks, scheduledTasks);
    }

    /** Classifies tasks into todo, unscheduled, and scheduled task lists. */
    private void classifyTasks(TaskList todoTasks, TaskList unscheduledTasks, TaskList scheduledTasks) {
        for (int i = 0; i < size; i++) {
            Task task = tasks[i];
            if (task instanceof TodoTask) {
                todoTasks.add(task);
            } else if (task.getScheduledDateTime() == null) {
                unscheduledTasks.add(task);
            } else {
                scheduledTasks.add(task);
            }
        }
    }

    /** Sorts scheduled tasks chronologically, using task type to break equal-time ties. */
    private void sortScheduledTasks(TaskList scheduledTasks) {
        Arrays.sort(scheduledTasks.tasks, 0, scheduledTasks.size, SCHEDULED_TASK_COMPARATOR);
    }

    /** Replaces this list's order with the displayed task groups. */
    private void replaceTasks(TaskList... taskLists) {
        size = 0;
        for (TaskList taskList : taskLists) {
            for (int i = 0; i < taskList.size; i++) {
                add(taskList.tasks[i]);
            }
        }
    }

    /** Returns the order used to place equal-time task types. */
    private static int getScheduledTaskTypeOrder(Task task) {
        return task instanceof DeadlineTask ? 0 : 1;
    }

    /** Returns whether an index refers to a task currently held by the list. */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < size;
    }
}
