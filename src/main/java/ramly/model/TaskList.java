package ramly.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;

import ramly.exception.TaskValidationException;

/** Owns the collection of tasks and its basic operations. */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;
    private UndoOperation undoOperation;

    /** Captures task membership, completion states, and undo history for rollback. */
    public static final class State {
        private final ArrayList<Task> tasks;
        private final ArrayList<Boolean> completionStates;
        private final UndoOperation undoOperation;

        private State(ArrayList<Task> tasks, UndoOperation undoOperation) {
            this.tasks = new ArrayList<>(tasks);
            this.completionStates = tasks.stream()
                    .map(task -> task.isDone)
                    .collect(Collectors.toCollection(ArrayList::new));
            this.undoOperation = undoOperation;
        }
    }

    /** Reverses one task-list mutation and describes its result. */
    @FunctionalInterface
    private interface UndoOperation {
        UndoResult undo();
    }

    /** Creates a task list backed by the supplied collection. */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Backing task collection must not be null";
        assert tasks.stream().noneMatch(task -> task == null) : "Task list must not contain null tasks";
        this.tasks = tasks;
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns the task at the specified zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Adds a task to the collection. */
    public void add(Task task) {
        assert task != null : "Added task must not be null";
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameIdentity(task))) {
            throw new TaskValidationException("That exact task is already on your trail.");
        }
        int addedIndex = tasks.size();
        tasks.add(task);
        undoOperation = () -> {
            Task removedTask = tasks.remove(addedIndex);
            return new UndoResult(UndoResult.Action.ADD, removedTask, tasks.size());
        };
    }

    /** Removes and returns the task at the specified index. */
    public Task remove(int index) {
        Task removedTask = tasks.remove(index);
        undoOperation = () -> {
            tasks.add(index, removedTask);
            return new UndoResult(UndoResult.Action.DELETE, removedTask, tasks.size());
        };
        return removedTask;
    }

    /** Marks the task at the specified index and records its previous status. */
    public Task mark(int index) {
        Task task = tasks.get(index);
        boolean wasDone = task.isDone;
        task.mark();
        recordStatusUndo(task, wasDone);
        return task;
    }

    /** Unmarks the task at the specified index and records its previous status. */
    public Task unmark(int index) {
        Task task = tasks.get(index);
        boolean wasDone = task.isDone;
        task.unmark();
        recordStatusUndo(task, wasDone);
        return task;
    }

    /** Records how to restore a task's completion status. */
    private void recordStatusUndo(Task task, boolean wasDone) {
        undoOperation = () -> {
            if (wasDone) {
                task.mark();
            } else {
                task.unmark();
            }
            return new UndoResult(UndoResult.Action.STATUS_CHANGE, task, tasks.size());
        };
    }

    /** Reverses and consumes the latest mutation, or returns null when none exists. */
    public UndoResult undo() {
        if (undoOperation == null) {
            return null;
        }
        UndoOperation operationToExecute = undoOperation;
        undoOperation = null;
        return operationToExecute.undo();
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring case. */
    public ArrayList<Task> find(String keyword) {
        assert keyword != null : "Search keyword must not be null";
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.description.toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** Returns the zero-based position of a task, or -1 if it is absent. */
    public int indexOf(Task task) {
        return tasks.indexOf(task);
    }

    /** Captures the current state so a failed persistence operation can be rolled back. */
    public State createSnapshot() {
        return new State(tasks, undoOperation);
    }

    /** Restores a previously captured state after a failed persistence operation. */
    public void restoreSnapshot(State state) {
        assert state != null : "Restored task-list state must not be null";
        assert state.tasks.size() == state.completionStates.size()
                : "Every restored task must have a completion state";
        tasks.clear();
        tasks.addAll(state.tasks);
        for (int i = 0; i < tasks.size(); i++) {
            if (state.completionStates.get(i)) {
                tasks.get(i).mark();
            } else {
                tasks.get(i).unmark();
            }
        }
        undoOperation = state.undoOperation;
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
