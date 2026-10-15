package bingbong.command;

import bingbong.exception.BingBongException;
import bingbong.storage.Storage;
import bingbong.task.TaskList;
import bingbong.ui.Ui;

/**
 * Represents an executable command in the chatbot application. All specific commands must extend this class and
 * implement execute.
 */

public abstract class Command {

    /**
     * Executes the specific behavior defined by the underlying command implementation.
     *
     * @param tasks   The active TaskList tracking current items.
     * @param ui      The user interface formatting output blocks.
     * @param storage The storage file handler managing disk reads and writes.
     * @throws BingBongException If processing limits or validation boundaries are breached.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BingBongException;

    /**
     * Determines whether the current command signals the application runtime loop to exit.
     *
     * @return True if execution loop must terminate, false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
