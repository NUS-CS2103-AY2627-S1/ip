package duke.task;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Locale;
import java.util.stream.Collectors;

import duke.exception.GaryException;
import duke.storage.Storage;

/**
 * Represents the in-memory list of tasks for the application.
 *
 * <p>This class provides operations to add, remove, and update tasks. If a {@link Storage} is configured,
 * mutating operations will automatically trigger {@link #save()}.</p>
 */
public class TaskList {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final int MAX_UNDO_HISTORY_SIZE = 100;
    /** Underlying list of tasks. Indices are zero-based. */
    private final ArrayList<Task> tasks;
    /** Changes that can be undone, with the most recent change first. */
    private final Deque<UndoAction> undoHistory;
    /** Storage used for persistence; may be {@code null} if persistence is not configured. */
    private Storage storage;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
        this.undoHistory = new ArrayDeque<>();
    }

    /**
     * Creates a task list backed by the given existing list.
     *
     * @param tasks Existing tasks to use as the initial contents.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        assert tasks.stream().noneMatch(task -> task == null) : "Initial task list must not contain null tasks";

        this.tasks = tasks;
        this.undoHistory = new ArrayDeque<>();
    }

    /**
     * Sets the storage used by this task list for persistence.
     *
     * @param storage Storage instance to use.
     */
    public void setStorage(Storage storage) {
        assert storage != null : "Configured storage must not be null";

        this.storage = storage;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero-based index.
     * @return The task at {@code index}.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the most recently added task.
     *
     * @return Last task in the list.
     */
    public Task getLast() {
        assert !tasks.isEmpty() : "Cannot get the last task from an empty list";

        return tasks.getLast();
    }

    /**
     * Adds a task to the list without auto-saving.
     *
     * <p>This is mainly useful for non-user-triggered additions (e.g., loading from disk). For user commands,
     * prefer the typed add methods that auto-save.</p>
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";

        tasks.add(task);
    }

    /**
     * Adds a todo task and saves the updated list (if storage is configured).
     *
     * @param description Description of the todo.
     * @throws GaryException If the description is invalid, the task is duplicated, or saving fails.
     */
    public void addTodo(String description) {
        assert description != null && !description.isBlank() : "Todo description must be non-blank";

        String normalizedDescription = normalizeDescription(description);
        Task addedTask = new ToDo(normalizedDescription);
        ensureUnique(addedTask);
        int addedIndex = tasks.size();
        tasks.add(addedTask);
        saveOrRollback(() -> tasks.remove(addedTask));
        recordUndo(() -> tasks.remove(addedTask), () -> tasks.add(addedIndex, addedTask));
    }

    /**
     * Adds a deadline task and saves the updated list (if storage is configured).
     *
     * @param description Description of the deadline.
     * @param deadline Due date.
     * @throws GaryException If the description is invalid, the task is duplicated, or saving fails.
     */
    public void addDeadline(String description, java.time.LocalDate deadline) {
        assert description != null && !description.isBlank() : "Deadline description must be non-blank";
        assert deadline != null : "Deadline date must not be null";

        String normalizedDescription = normalizeDescription(description);
        Task addedTask = new Deadline(normalizedDescription, deadline);
        ensureUnique(addedTask);
        int addedIndex = tasks.size();
        tasks.add(addedTask);
        saveOrRollback(() -> tasks.remove(addedTask));
        recordUndo(() -> tasks.remove(addedTask), () -> tasks.add(addedIndex, addedTask));
    }

    /**
     * Adds an event task and saves the updated list (if storage is configured).
     *
     * @param description Description of the event.
     * @param start Start date.
     * @param end End date.
     * @throws GaryException If the event data is invalid, the task is duplicated, or saving fails.
     */
    public void addEvent(String description, java.time.LocalDate start, java.time.LocalDate end) {
        assert description != null && !description.isBlank() : "Event description must be non-blank";
        assert start != null : "Event start date must not be null";
        assert end != null : "Event end date must not be null";

        if (!start.isBefore(end)) {
            throw new GaryException("The event start date must be before the end date.");
        }

        String normalizedDescription = normalizeDescription(description);
        Task addedTask = new Event(normalizedDescription, start, end);
        ensureUnique(addedTask);
        int addedIndex = tasks.size();
        tasks.add(addedTask);
        saveOrRollback(() -> tasks.remove(addedTask));
        recordUndo(() -> tasks.remove(addedTask), () -> tasks.add(addedIndex, addedTask));
    }

    /**
     * Removes and returns the task at the given index, then saves the updated list (if storage is configured).
     *
     * @param index Zero-based index.
     * @return The removed task.
     * @throws GaryException If saving fails; the removal is rolled back before the exception is thrown.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Removal index must have been validated";

        Task removedTask = tasks.remove(index);
        saveOrRollback(() -> tasks.add(index, removedTask));
        recordUndo(() -> tasks.add(index, removedTask), () -> tasks.remove(removedTask));
        return removedTask;
    }

    /**
     * Marks the task at the given index as done/undone, then saves the updated list (if storage is configured).
     *
     * @param index Zero-based index.
     * @param isDone {@code true} to mark as done, {@code false} to mark as not done.
     * @throws GaryException If saving fails; the status change is rolled back before the exception is thrown.
     */
    public void mark(int index, boolean isDone) {
        assert index >= 0 && index < tasks.size() : "Mark index must have been validated";

        Task task = tasks.get(index);
        boolean wasDone = task.isDone;
        if (wasDone == isDone) {
            save();
            return;
        }

        if (isDone) {
            task.markAsDone();
        } else {
            task.markUndone();
        }
        saveOrRollback(() -> setDone(task, wasDone));
        recordUndo(() -> setDone(task, wasDone), () -> setDone(task, isDone));
    }

    /**
     * Reverses the most recent task-list change and saves the restored list.
     *
     * @return {@code true} if a change was undone, or {@code false} if no undoable change exists.
     * @throws GaryException If saving fails; the undo is rolled back and remains available to retry.
     */
    public boolean undo() {
        if (undoHistory.isEmpty()) {
            return false;
        }

        UndoAction action = undoHistory.removeFirst();
        action.undo();
        try {
            save();
        } catch (GaryException e) {
            action.redo();
            undoHistory.addFirst(action);
            throw e;
        }
        return true;
    }

    /**
     * Validates that the index is within the current task list bounds.
     *
     * @param index Zero-based index.
     * @throws GaryException If {@code index} is out of range.
     */
    public void validateIndex(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new GaryException("I can't find a task with that number!");
        }
    }

    /**
     * Returns the underlying list of tasks.
     *
     * <p>Callers should treat the returned list as read-only to avoid bypassing validation and persistence.</p>
     *
     * @return The underlying task list.
     */
    public ArrayList<Task> asList() {
        return tasks;
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * <p>Matching is case-insensitive. The returned list preserves the order of tasks in the task list.</p>
     *
     * @param keyword Keyword to search for.
     * @return List of matching tasks.
     */
    public ArrayList<Task> findByKeyword(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Search keyword must be non-blank";

        String keywordInLowerCase = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.description.toLowerCase(Locale.ROOT).contains(keywordInLowerCase))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Saves the current tasks to disk if storage is configured.
     */
    public void save() {
        if (storage != null) {
            storage.save(tasks);
        }
    }

    private String normalizeDescription(String description) {
        String normalizedDescription = description.strip().replaceAll("\\s+", " ");
        if (normalizedDescription.contains("|")) {
            throw new GaryException("Task descriptions cannot contain the '|' character.");
        }
        if (normalizedDescription.length() > MAX_DESCRIPTION_LENGTH) {
            throw new GaryException("Task descriptions cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters.");
        }
        return normalizedDescription;
    }

    private void ensureUnique(Task newTask) {
        boolean isDuplicate = tasks.stream().anyMatch(task -> task.hasSameDetails(newTask));
        if (isDuplicate) {
            throw new GaryException("That task already exists in your list.");
        }
    }

    private void saveOrRollback(Runnable rollbackOperation) {
        try {
            save();
        } catch (GaryException e) {
            rollbackOperation.run();
            throw e;
        }
    }

    private void recordUndo(Runnable undoOperation, Runnable redoOperation) {
        assert undoOperation != null : "Undo operation must not be null";
        assert redoOperation != null : "Redo operation must not be null";

        if (undoHistory.size() == MAX_UNDO_HISTORY_SIZE) {
            undoHistory.removeLast();
        }
        undoHistory.addFirst(new UndoAction(undoOperation, redoOperation));
    }

    private void setDone(Task task, boolean isDone) {
        if (isDone) {
            task.markAsDone();
        } else {
            task.markUndone();
        }
    }

    /** Stores the inverse operations needed to safely undo or restore one task-list change. */
    private static class UndoAction {
        private final Runnable undoOperation;
        private final Runnable redoOperation;

        UndoAction(Runnable undoOperation, Runnable redoOperation) {
            this.undoOperation = undoOperation;
            this.redoOperation = redoOperation;
        }

        void undo() {
            undoOperation.run();
        }

        void redo() {
            redoOperation.run();
        }
    }
}
