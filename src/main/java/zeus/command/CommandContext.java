package zeus.command;

import zeus.storage.Storage;
import zeus.task.TaskList;
import zeus.ui.Ui;

/** Provides application components to a command during execution. */
public final class CommandContext {
    /** Task collection available to commands. */
    private final TaskList tasks;

    /** UI used to display command results. */
    private final Ui ui;

    /** Persistent storage used after task changes. */
    private final Storage storage;

    /**
     * Creates a context containing the components commands may use.
     *
     * @param tasks Task collection available to commands.
     * @param ui UI used to display command results.
     * @param storage Persistent storage used after task changes.
     */
    public CommandContext(TaskList tasks, Ui ui, Storage storage) {
        this.tasks = tasks;
        this.ui = ui;
        this.storage = storage;
    }

    /**
     * Returns the task collection.
     *
     * @return Task collection available to commands.
     */
    public TaskList getTasks() {
        return tasks;
    }

    /**
     * Returns the user interface.
     *
     * @return UI used to display command results.
     */
    public Ui getUi() {
        return ui;
    }

    /**
     * Returns persistent storage.
     *
     * @return Storage used after task changes.
     */
    public Storage getStorage() {
        return storage;
    }
}
