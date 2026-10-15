package prism.tasklist;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import prism.PrismException;
import prism.task.Deadline;
import prism.task.Event;
import prism.task.Task;

/** Maintains the ordered collection of tasks and task-list operations. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list initialized with a copy of the supplied tasks. */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /** Returns the mutable list of tasks managed by this object. */
    public List<Task> getTasks() {
        return this.tasks;
    }

    /** Returns the number of tasks in the list. */
    public int getSize() {
        return this.tasks.size();
    }

    /** Returns the task at a zero-based index. */
    public Task getTask(int index) throws PrismException {
        validateIndex(index);
        assert index >= 0 && index < this.tasks.size() : "Task index must be valid after validation";
        return this.tasks.get(index);
    }

    /** Adds a task to the end of the list and returns it. */
    public Task addTask(Task task) {
        this.tasks.add(task);
        return task;
    }

    /** Removes and returns the task at a zero-based index. */
    public Task deleteTask(int index) throws PrismException {
        validateIndex(index);
        assert index >= 0 && index < this.tasks.size() : "Task index must be valid after validation";
        return this.tasks.remove(index);
    }

    /** Marks the task at a zero-based index as done. */
    public Task markTask(int index) throws PrismException {
        validateIndex(index);
        assert index >= 0 && index < this.tasks.size() : "Task index must be valid after validation";
        Task task = this.tasks.get(index);
        task.markAsDone();
        return task;
    }

    /** Marks the task at a zero-based index as not done. */
    public Task unmarkTask(int index) throws PrismException {
        validateIndex(index);
        assert index >= 0 && index < this.tasks.size() : "Task index must be valid after validation";
        Task task = this.tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /** Reschedules the task at the supplied index. */
    public Task snoozeTask(int index, String newDateTime) throws PrismException {
        validateIndex(index);
        Task task = this.tasks.get(index);
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            deadline.reschedule(newDateTime);
        } else if (task instanceof Event) {
            Event event = (Event) task;
            event.reschedule(newDateTime);
        } else {
            throw new PrismException("!!! Only deadlines and events can be snoozed.");
        }
        return task;
    }

    /** Returns deadlines and events occurring on the supplied date. */
    public List<Task> getTasksOnDate(LocalDate queryDate) {
        return this.tasks.stream()
                .filter(task -> isTaskOnDate(task, queryDate))
                .collect(Collectors.toList());
    }

    /** Returns tasks whose descriptions contain the supplied keyword, ignoring case. */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return this.tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toList());
    }

    /** Returns whether a task occurs on the supplied date. */
    private boolean isTaskOnDate(Task task, LocalDate queryDate) {
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return deadline.getBy().toLocalDate().equals(queryDate);
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            LocalDate startDate = event.getFrom().toLocalDate();
            LocalDate endDate = event.getTo().toLocalDate();
            return (queryDate.isEqual(startDate) || queryDate.isAfter(startDate))
                    && (queryDate.isEqual(endDate) || queryDate.isBefore(endDate));
        }
        return false;
    }

    /** Throws an exception when an index is outside the current list. */
    private void validateIndex(int index) throws PrismException {
        if (index < 0 || index >= this.tasks.size()) {
            throw new PrismException("!!! That task number doesn't exist. You currently have "
                    + this.tasks.size() + " task(s).");
        }
    }
}
