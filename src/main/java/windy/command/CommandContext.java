package windy.command;

import java.io.IOException;
import java.util.function.Consumer;

import windy.storage.Storage;
import windy.task.Task;
import windy.task.TaskList;
import windy.ui.Ui;

/**
 * Provides the application state and services needed to execute a command.
 */
public class CommandContext {
    private final TaskList tasks;
    private final Ui ui;
    private final Storage storage;
    private final UndoHistory history;
    private boolean isExitRequested;

    /**
     * Creates a command context with its own undo history and the supplied dependencies.
     *
     * @param tasks the current task list.
     * @param ui the application UI.
     * @param storage the task storage.
     */
    public CommandContext(TaskList tasks, Ui ui, Storage storage) {
        this(tasks, ui, storage, new UndoHistory());
    }

    /**
     * Creates a context sharing the application's undo history.
     *
     * @param tasks the current tasks.
     * @param ui the response UI.
     * @param storage the task storage.
     * @param history the session history.
     */
    public CommandContext(TaskList tasks, Ui ui, Storage storage, UndoHistory history) {
        this.history = history;
        this.tasks = tasks;
        this.ui = ui;
        this.storage = storage;
    }

    public TaskList getTasks() {
        return tasks;
    }

    public Ui getUi() {
        return ui;
    }

    /**
     * Saves a candidate change before publishing it and recording history.
     *
     * @param change the operation to apply to an independent candidate list.
     * @return whether the operation succeeded, including an unchanged result.
     */
    public boolean applyTaskChange(Consumer<TaskList> change) {
        TaskList candidate = tasks.copy();
        change.accept(candidate);
        if (tasks.getTasks().stream().map(Task::toDataString).toList()
                .equals(candidate.getTasks().stream().map(Task::toDataString).toList())) {
            return true;
        }
        if (!saveTasks(candidate)) {
            return false;
        }
        history.record(tasks, candidate);
        tasks.replaceWith(candidate);
        return true;
    }

    /**
     * Restores the available undo or redo snapshot and persists it.
     *
     * @param isUndo whether to undo rather than redo.
     */
    public void restoreTaskChange(boolean isUndo) {
        TaskList target = history.getTarget(isUndo);
        if (target == null) {
            ui.showHistoryResult(isUndo ? "No task change to undo." : "No task change to redo.");
            return;
        }
        if (!saveTasks(target)) {
            return;
        }
        tasks.replaceWith(target);
        history.completeRestore(isUndo);
        ui.showHistoryResult(isUndo ? "Undone the last task change." : "Redone the last task change.");
    }

    private boolean saveTasks(TaskList candidate) {
        try {
            storage.saveTasks(candidate.getTasks());
            return true;
        } catch (IOException exception) {
            ui.showError("Could not save tasks to '" + storage.getFilePath() + "': "
                    + exception.getMessage() + ". The change was not applied.");
            return false;
        }
    }

    /**
     * Records that the application should exit after the current command.
     */
    public void requestExit() {
        isExitRequested = true;
    }

    /**
     * Checks whether the current command requested application exit.
     *
     * @return {@code true} if the application should exit.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }
}
