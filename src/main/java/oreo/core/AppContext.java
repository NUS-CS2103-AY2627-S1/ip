package oreo.core;

import oreo.model.TaskList;
import oreo.storage.Storage;
import oreo.ui.Ui;

/** The application collaborators available while executing a command. */
public class AppContext {
    /** The current task collection. */
    private final TaskList tasks;
    /** The user interaction handler. */
    private final Ui ui;
    /** The task persistence handler. */
    private final Storage storage;

    /** Creates an execution context from the application collaborators. */
    public AppContext(TaskList tasks, Ui ui, Storage storage) {
        this.tasks = tasks;
        this.ui = ui;
        this.storage = storage;
    }

    /** Returns the current task collection. */
    public TaskList getTasks() {
        return tasks;
    }

    /** Returns the user interaction handler. */
    public Ui getUi() {
        return ui;
    }

    /** Returns the task persistence handler. */
    public Storage getStorage() {
        return storage;
    }
}
