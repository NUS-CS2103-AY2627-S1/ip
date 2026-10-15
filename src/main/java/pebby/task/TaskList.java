package pebby.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Owns Pebby's tasks and provides the operations used to manage them.
 *
 * <p>The underlying list is private so other classes cannot accidentally
 * replace it or change it without using a task-list operation.</p>
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to include in the new list
     */
    public TaskList(Task... tasks) {
        this(List.of(tasks));
    }

    /**
     * Creates a task list containing the supplied saved tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the task at the specified zero-based index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Creates and adds a todo task.
     */
    public Todo addTodo(String description) {
        Todo todo = new Todo(description);
        addUniqueTask(todo);
        return todo;
    }

    /**
     * Creates and adds a deadline task.
     */
    public Deadline addDeadline(String description, String by) {
        Deadline deadline = new Deadline(description, by);
        addUniqueTask(deadline);
        return deadline;
    }

    /**
     * Creates and adds an event task.
     */
    public Event addEvent(String description, String from, String to) {
        Event event = new Event(description, from, to);
        addUniqueTask(event);
        return event;
    }

    /**
     * Marks the task at the specified zero-based index as complete.
     */
    public Task markAsDone(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the specified zero-based index as incomplete.
     */
    public Task markAsNotDone(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Deletes and returns the task at the specified zero-based index.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword, in task-list order.
     */
    public List<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Returns dated tasks that occur on the specified date, with incomplete tasks first.
     */
    public List<Task> getScheduleFor(LocalDate date) {
        return tasks.stream()
                .filter(task -> isScheduledOn(task, date))
                .sorted(Comparator.comparing(Task::isDone))
                .toList();
    }

    /** Returns whether a dated task occurs on the specified date. */
    private boolean isScheduledOn(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return deadline.isOn(date);
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return event.isOn(date);
        }
        return false;
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a copy of the tasks for storage without exposing the internal list.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /** Adds a task after ensuring an identical task has not already been recorded. */
    private void addUniqueTask(Task task) {
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
            throw new IllegalArgumentException("An identical task is already in the list.");
        }
        tasks.add(task);
    }
}
