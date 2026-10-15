package hse.command;

import java.io.IOException;

import hse.storage.Storage;
import hse.task.TaskList;
import hse.ui.Ui;

/** Displays every task in the task list. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        ui.showTaskList(tasks);
    }
}
