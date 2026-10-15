package hse.command;

import java.io.IOException;

import hse.storage.Storage;
import hse.task.TaskList;
import hse.ui.Ui;

/** Reports an input that does not match any supported command. */
public class InvalidCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        throw new IllegalArgumentException("Please input a valid command.");
    }
}
