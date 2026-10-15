package gooble.command;

import gooble.task.TaskList;
import gooble.ui.Ui;

/**
 * Shared application services available to command handlers.
 */
public class CommandContext {
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;

    /** Creates a context containing services shared by command handlers. */
    public CommandContext(TaskList tasks, Ui ui, Parser parser) {
        this.tasks = tasks;
        this.ui = ui;
        this.parser = parser;
    }

    /** Returns the task list service. */
    public TaskList tasks() {
        return tasks;
    }

    /** Returns the user-interface service. */
    public Ui ui() {
        return ui;
    }

    /** Returns the command parser service. */
    public Parser parser() {
        return parser;
    }
}
