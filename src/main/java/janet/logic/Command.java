package janet.logic;

import java.util.List;

import janet.exception.StorageException;
import janet.storage.Storage;
import janet.task.Task;
import janet.task.TaskList;
import janet.ui.Ui;

/**
 * Represents an operation entered by the user.
 */
public abstract class Command {
    /**
     * Executes this command using Janet's collaborating components.
     *
     * @param tasks Janet's task list.
     * @param ui Janet's user interface.
     * @param storage Janet's task storage.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /**
     * Returns whether executing this command ends Janet.
     *
     * @return whether Janet should exit
     */
    public boolean isExit() {
        return false;
    }
}

/** Adds a new task and persists the changed list. */
class AddCommand extends Command {
    private static final String PAST_DATE_TIME_WARNING =
            "Warning: this task contains a date or time that has already passed.";

    private final boolean hasPastDateTime;
    private final Task task;

    /**
     * Creates a command that adds one task.
     *
     * @param task task to add.
     */
    AddCommand(Task task) {
        this(task, false);
    }

    /**
     * Creates a command that adds one task and optionally warns about a past date or time.
     *
     * @param task task to add.
     * @param hasPastDateTime whether the task contains a recognized past date or time.
     */
    AddCommand(Task task, boolean hasPastDateTime) {
        this.task = task;
        this.hasPastDateTime = hasPastDateTime;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> previousTasks = tasks.getTasks();
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            tasks.restore(previousTasks);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
        if (hasPastDateTime) {
            ui.showWarning(PAST_DATE_TIME_WARNING);
        }
    }
}

/** Displays every stored task. */
class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}

/** Removes one or more tasks and persists the changed list. */
class DeleteCommand extends Command {
    private final List<Integer> taskNumbers;

    /**
     * Creates a command that deletes one or more tasks.
     *
     * @param taskNumbers one-based numbers of the tasks to delete.
     */
    DeleteCommand(List<Integer> taskNumbers) {
        this.taskNumbers = List.copyOf(taskNumbers);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> previousTasks = tasks.getTasks();
        List<Task> deletedTasks = tasks.delete(taskNumbers);
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            tasks.restore(previousTasks);
            throw exception;
        }
        if (deletedTasks.size() == 1) {
            ui.showTaskDeleted(deletedTasks.get(0), tasks.size());
        } else {
            ui.showTasksDeleted(deletedTasks, tasks.size());
        }
    }
}

/** Marks a task as complete and persists the changed list. */
class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks one task as complete.
     *
     * @param taskNumber one-based number of the task to mark.
     */
    MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            restoreCompletionStatus(task, wasDone);
            throw exception;
        }
        ui.showTaskMarked(task);
    }

    /** Restores a task's completion status after a failed save. */
    private void restoreCompletionStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
    }
}

/** Marks a task as incomplete and persists the changed list. */
class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks one task as incomplete.
     *
     * @param taskNumber one-based number of the task to unmark.
     */
    UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        task.markAsUndone();
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            restoreCompletionStatus(task, wasDone);
            throw exception;
        }
        ui.showTaskUnmarked(task);
    }

    /** Restores a task's completion status after a failed save. */
    private void restoreCompletionStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
    }
}

/** Displays tasks whose descriptions contain a keyword. */
class FindCommand extends Command {
    private final String keyword;

    FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}

/** Ends Janet after showing the goodbye message. */
class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
