package sage.command;

import java.io.IOException;

import sage.model.TaskList;
import sage.storage.Storage;
import sage.ui.Ui;

/** Command that displays all tasks in insertion order. */
public class ListCommand extends Command {
    /** Creates a list command. */
    public ListCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        ui.show("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            ui.show((index + 1) + "." + tasks.get(index));
        }
    }
}
