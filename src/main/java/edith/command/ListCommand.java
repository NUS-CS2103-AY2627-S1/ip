package edith.command;

import edith.Storage;
import edith.Ui;
import edith.task.TaskList;

/** Displays every task. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showList(tasks);
    }
}
