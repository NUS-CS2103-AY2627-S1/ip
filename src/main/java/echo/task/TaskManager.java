package echo.task;

import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import echo.EchoException;
import echo.storage.Storage;

/**
 * Provides the task operations used by E.C.H.O.
 */
public class TaskManager {
    /** Comparator that sorts tasks chronologically with undated tasks placed at the end. */
    private static final Comparator<Task> CHRONOLOGICAL_COMPARATOR = (first, second) -> {
        Optional<LocalDateTime> firstTime = first.getDateTime();
        Optional<LocalDateTime> secondTime = second.getDateTime();

        if (firstTime.isPresent() && secondTime.isPresent()) {
            int comparison = firstTime.get().compareTo(secondTime.get());
            if (comparison != 0) {
                return comparison;
            }
        } else if (firstTime.isPresent()) {
            return -1;
        } else if (secondTime.isPresent()) {
            return 1;
        }
        return first.getDescription().compareToIgnoreCase(second.getDescription());
    };

    /** Comparator that sorts tasks alphabetically by description (case-insensitive). */
    private static final Comparator<Task> NAME_COMPARATOR = (first, second) -> {
        int comparison = first.getDescription().compareToIgnoreCase(second.getDescription());
        if (comparison != 0) {
            return comparison;
        }
        Optional<LocalDateTime> firstTime = first.getDateTime();
        Optional<LocalDateTime> secondTime = second.getDateTime();
        if (firstTime.isPresent() && secondTime.isPresent()) {
            return firstTime.get().compareTo(secondTime.get());
        } else if (firstTime.isPresent()) {
            return -1;
        } else if (secondTime.isPresent()) {
            return 1;
        }
        return 0;
    };

    /** The task list used to store tasks in memory. */
    private final TaskList taskList;
    /** The storage used to persist task changes. */
    private final Storage storage;

    /**
     * Creates a task manager with an empty list and default storage.
     */
    public TaskManager() {
        this(new TaskList(), new Storage());
    }

    /**
     * Creates a task manager using the supplied task list and default storage.
     *
     * @param taskList task list used by this manager.
     */
    public TaskManager(TaskList taskList) {
        this(taskList, new Storage());
    }

    /**
     * Creates a task manager using the supplied task list and storage.
     *
     * @param taskList task list used by this manager.
     * @param storage storage used to save changes.
     */
    public TaskManager(TaskList taskList, Storage storage) {
        this.taskList = taskList;
        this.storage = storage;
    }

    /**
     * Creates and stores a todo task.
     *
     * @param description task description.
     * @return the new task.
     * @throws EchoException if adding or saving the task fails or if it is a duplicate.
     */
    public Task addTodo(String description) throws EchoException {
        return addTask(new TodoTask(description));
    }

    /**
     * Creates and stores a deadline task.
     *
     * @param description task description.
     * @param by due date with an optional time.
     * @return the new task.
     * @throws EchoException if adding or saving the task fails or if it is a duplicate.
     */
    public Task addDeadline(String description, String by) throws EchoException {
        return addTask(new DeadlineTask(description, by));
    }

    /**
     * Creates and stores an event task.
     *
     * @param description event description.
     * @param from event start date with an optional time.
     * @param to event end date with an optional time.
     * @return the new task.
     * @throws EchoException if adding or saving the task fails, if dates are invalid, or if duplicate.
     */
    public Task addEvent(String description, String from, String to) throws EchoException {
        return addTask(new EventTask(description, from, to));
    }

    /**
     * Returns a task at a one-based task-list position.
     *
     * @param taskNumber one-based task number.
     * @return task at that position.
     */
    public Task getTask(int taskNumber) {
        return taskList.getTask(taskNumber);
    }

    /**
     * Returns the number of tasks currently stored.
     */
    public int size() {
        return taskList.size();
    }

    /**
     * Checks whether the task list is empty.
     */
    public boolean isEmpty() {
        return taskList.isEmpty();
    }

    /**
     * Returns a read-only snapshot of the tasks in insertion order.
     *
     * @return immutable task snapshot.
     */
    public List<Task> getTasks() {
        return taskList.asList();
    }

    /**
     * Returns a list of tasks whose description contains the search keyword.
     *
     * @param keyword the keyword to search for.
     */
    public List<Task> findByKeyword(String keyword) {
        return taskList.findByKeyword(keyword);
    }

    /**
     * Marks a task as done.
     *
     * @param taskNumber one-based task number.
     * @return the marked task.
     * @throws EchoException if the task number is unavailable.
     */
    public Task markTask(int taskNumber) throws EchoException {
        Task task = requireTask(taskNumber);
        if (task.isDone()) {
            throw new EchoException("Directive #" + taskNumber + " is already marked as completed:\n  " + task);
        }
        task.mark();
        save();
        return task;
    }

    /**
     * Marks a task as not done.
     *
     * @param taskNumber one-based task number.
     * @return the unmarked task.
     * @throws EchoException if the task number is unavailable, task is already in-progress, or save fails.
     */
    public Task unmarkTask(int taskNumber) throws EchoException {
        Task task = requireTask(taskNumber);
        if (!task.isDone()) {
            throw new EchoException("Directive #" + taskNumber + " is already marked as in-progress:\n  " + task);
        }
        task.unmark();
        save();
        return task;
    }

    /**
     * Removes a task from the task list.
     *
     * @param taskNumber one-based task number.
     * @return the removed task.
     * @throws EchoException if the task number is unavailable or saving fails.
     */
    public Task deleteTask(int taskNumber) throws EchoException {
        Task task = requireTask(taskNumber);
        Task removedTask = taskList.removeTask(taskNumber);
        assert removedTask == task : "The task removed must be the task that was validated";
        save();
        return removedTask;
    }

    /**
     * Sorts the tasks using the supplied criteria and persists the new order.
     *
     * @param criteria the criteria to sort tasks by.
     * @return the sorted list of tasks.
     * @throws EchoException if saving the sorted tasks fails.
     */
    public List<Task> sortTasks(SortCriteria criteria) throws EchoException {
        Comparator<Task> comparator = switch (criteria) {
            case DATE -> CHRONOLOGICAL_COMPARATOR;
            case NAME -> NAME_COMPARATOR;
        };
        taskList.sort(comparator);
        save();
        return taskList.asList();
    }

    /** Adds a task and persists the updated list. */
    private Task addTask(Task task) throws EchoException {
        if (taskList.hasDuplicate(task)) {
            throw new EchoException("This directive already exists in the manifest:\n  " + task);
        }
        taskList.addTask(task);
        assert taskList.getTask(taskList.size()) == task : "A newly added task must be appended";
        save();
        return task;
    }

    /** Retrieves a task after checking that its one-based number exists. */
    private Task requireTask(int taskNumber) throws EchoException {
        if (taskList.isEmpty()) {
            throw new EchoException("Directive backlog is empty. "
                    + "Use 'todo', 'deadline', or 'event' to log tasks first.");
        }
        if (!taskList.hasTask(taskNumber)) {
            throw new EchoException("There is no task numbered " + taskNumber
                    + ". Use 'list' to see the available task numbers.");
        }
        return taskList.getTask(taskNumber);
    }

    /** Persists the current in-memory task list. */
    private void save() throws EchoException {
        try {
            storage.save(taskList.asList());
        } catch (UncheckedIOException exception) {
            throw new EchoException("Storage failure: Unable to save changes to '"
                    + storage.getFilePath() + "'. Please check file permissions.");
        }
    }
}
