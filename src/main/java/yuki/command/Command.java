package yuki.command;

import yuki.storage.Storage;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Represents an instruction that can be executed by Yuki.
 */
public abstract class Command {
    /**
     * Performs this command using Yuki's task list and supporting components.
     *
     * @param tasks Tasks managed by Yuki.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist changes.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /**
     * Returns whether Yuki should stop after executing this command.
     *
     * @return The value {@code true} only for a command that exits Yuki.
     */
    public boolean isExit() {
        return false;
    }
}
