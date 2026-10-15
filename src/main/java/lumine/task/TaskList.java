package lumine.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lumine.LumineException;
import lumine.storage.Storage;

/**
 * Manages the in-memory list of tasks and keeps storage in sync after every change.
 *
 * <p>All mutating operations ({@link #addTask}, {@link #markAsDone},
 * {@link #markAsUndone}, {@link #deleteTask}, {@link #undoLastChange}) save
 * the list to disk and roll back the in-memory change if saving reports an error.</p>
 */
public class TaskList {
    private static final DateTimeFormatter DATE_COMMAND_FORMAT =
            DateTimeFormatter.ofPattern("uuuu MM dd");
    private final Storage storage;
    private final List<Task> tasks = new ArrayList<>();
    private String currentCommand = "";
    private UndoAction lastUndoAction;

    /**
     * Stores the command text and how to reverse and restore its successful change.
     */
    private record UndoAction(Runnable undoChange, Runnable redoChange, String commandText) {
    }

    /**
     * Constructs a task list containing the tasks saved in the given storage.
     *
     * @param storage the storage to load from and save to.
     */
    public TaskList(Storage storage) {
        this(storage, true);
    }

    /**
     * Constructs a task list, optionally attempting to load saved tasks.
     *
     * @param storage the storage to load from and save to.
     * @param shouldLoadSavedTasks whether to load tasks from storage.
     */
    public TaskList(Storage storage, boolean shouldLoadSavedTasks) {
        assert storage != null : "Task list storage must not be null";
        this.storage = storage;
        if (shouldLoadSavedTasks) {
            tasks.addAll(storage.load());
            assert tasks.stream().noneMatch(task -> task == null) : "Loaded task list must not contain null";
        }
    }

    /**
     * Sets the input text to associate with the next successful task-list change.
     * Setting this context does not replace the previously recorded undo action.
     *
     * @param commandText the command currently being executed.
     */
    public void setCurrentCommand(String commandText) {
        currentCommand = commandText.trim();
    }

    /**
     * Adds the given task to the list and persists the change to storage.
     * If saving fails the task is removed from the list and the exception is re-thrown.
     *
     * @param task the task to add (must not be {@code null}).
     * @throws LumineException if {@code task} is null or storage cannot be written.
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new LumineException("Sorry, task cannot be empty. :C");
        }
        int taskIndex = tasks.size();
        tasks.add(task);
        assert tasks.getLast() == task : "New task must be appended to the task list";
        try {
            saveTasks();
        } catch (LumineException e) {
            Task rolledBackTask = tasks.removeLast();
            assert rolledBackTask == task : "Add rollback must remove the task that was just appended";
            throw e;
        }
        Runnable undoChange = () -> tasks.remove(taskIndex);
        Runnable redoChange = () -> tasks.add(taskIndex, task);
        lastUndoAction = new UndoAction(undoChange, redoChange, currentCommand);
    }

    /**
     * Returns a formatted listing of all tasks or a message when the list is empty.
     */
    public String formatTasks() {
        if (tasks.isEmpty()) {
            return "Nice, there are no task on your list!";
        }

        StringBuilder result = new StringBuilder();
        result.append("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            result.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return result.toString();
    }

    /**
     * Returns a formatted listing of pending deadlines and events due on the given date.
     */
    public String formatTasksDueOn(LocalDate date) {
        assert date != null : "Date filter must not be null";
        String formattedDate = date.format(DATE_COMMAND_FORMAT);
        StringBuilder result = new StringBuilder("Here is your list of pending task due on ")
                .append(formattedDate).append(":");
        int matchCount = 0;
        for (Task task : tasks) {
            if (!task.isDone && task.isDueOn(date)) {
                result.append("\n").append(++matchCount).append(".").append(task);
            }
        }
        if (matchCount == 0) {
            return "Your path is clear on " + formattedDate + " — no tasks due.";
        } else {
            return result.toString();
        }
    }

    /**
     * Returns a formatted listing of tasks that contain the given keyword in their description.
     */
    public String formatMatchingTasks(String keyword) {
        String matchingTasks = IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).description.contains(keyword))
                .mapToObj(i -> (i + 1) + "." + tasks.get(i))
                .collect(Collectors.joining("\n"));

        if (matchingTasks.isEmpty()) {
            return "I couldn't find a task containing '" + keyword + "'.";
        } else {
            return "Here is the list of matching tasks:\n" + matchingTasks;
        }
    }

    /**
     * Marks the task at the given 1-based position as done and saves the list.
     *
     * @param taskNumber 1-based index of the task to mark.
     * @return the task that was marked.
     * @throws LumineException if the task number is out of range or saving fails.
     */
    public Task markAsDone(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LumineException("Task not found :<.\n"
                    + "Please enter a valid task number.");
        }

        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = task.isDone;
        task.markDone();
        assert task.isDone : "Task must be done after it is marked";
        try {
            saveTasks();
        } catch (LumineException e) {
            task.isDone = wasDone;
            assert task.isDone == wasDone : "Failed mark must restore the previous task state";
            throw e;
        }
        Runnable undoChange = () -> {
            if (wasDone) {
                task.markDone();
            } else {
                task.markUndone();
            }
        };
        lastUndoAction = new UndoAction(undoChange, task::markDone, currentCommand);
        return task;
    }

    /**
     * Marks the task at the given 1-based position as not done and saves the list.
     *
     * @param taskNumber 1-based index of the task to unmark.
     * @return the task that was unmarked.
     * @throws LumineException if the task number is out of range or saving fails.
     */
    public Task markAsUndone(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LumineException("Task not found :<.\n"
                    + "Please enter a valid task number.");
        }

        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = task.isDone;
        task.markUndone();
        assert !task.isDone : "Task must be undone after it is unmarked";
        try {
            saveTasks();
        } catch (LumineException e) {
            task.isDone = wasDone;
            assert task.isDone == wasDone : "Failed unmark must restore the previous task state";
            throw e;
        }
        Runnable undoChange = () -> {
            if (wasDone) {
                task.markDone();
            } else {
                task.markUndone();
            }
        };
        lastUndoAction = new UndoAction(undoChange, task::markUndone, currentCommand);
        return task;
    }

    /**
     * Removes the task at the given 1-based position and saves the list.
     *
     * @param taskNumber 1-based index of the task to delete.
     * @return the task that was removed.
     * @throws LumineException if the task number is out of range or saving fails.
     */
    public Task deleteTask(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new LumineException("Task not found :<.\n"
                    + "Please enter a valid task number.");
        }

        int taskIndex = taskNumber - 1;
        Task removedTask = tasks.remove(taskIndex);
        assert removedTask != null : "Deleted task must not be null";
        try {
            saveTasks();
        } catch (LumineException e) {
            tasks.add(taskIndex, removedTask);
            assert tasks.get(taskIndex) == removedTask : "Failed delete must restore the removed task";
            throw e;
        }
        Runnable undoChange = () -> tasks.add(taskIndex, removedTask);
        Runnable redoChange = () -> tasks.remove(taskIndex);
        lastUndoAction = new UndoAction(undoChange, redoChange, currentCommand);
        return removedTask;
    }

    /**
     * Reverses and saves the most recent successful task-list change.
     * If saving fails, the undo is rolled back so memory and disk remain consistent.
     *
     * @return the input text of the command whose change was reversed.
     * @throws LumineException if there is no change to undo or storage cannot be written.
     */
    public String undoLastChange() {
        if (lastUndoAction == null) {
            throw new LumineException("Sorry, there is no command to undo. :C");
        }

        UndoAction undoAction = lastUndoAction;
        undoAction.undoChange().run();
        try {
            saveTasks();
        } catch (LumineException e) {
            undoAction.redoChange().run();
            throw e;
        }
        lastUndoAction = null;
        return undoAction.commandText();
    }

    /**
     * Returns the number of tasks currently in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Persists the current task list to storage; called after every mutating operation.
     */
    private void saveTasks() {
        assert tasks.stream().noneMatch(task -> task == null) : "Task list must not contain null before saving";
        storage.save(tasks);
    }
}
