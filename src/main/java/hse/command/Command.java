package hse.command;

import java.io.IOException;

import hse.storage.Storage;
import hse.task.TaskList;
import hse.ui.Ui;

/** Represents one parsed instruction that can be performed by the application. */
public abstract class Command {
    /** Performs this instruction using the application's collaborating objects. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws IOException;

    /** Returns whether executing this instruction ends the application. */
    public boolean isExit() {
        return false;
    }
}
