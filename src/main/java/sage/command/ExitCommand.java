package sage.command;

import java.io.IOException;

import sage.model.TaskList;
import sage.storage.Storage;
import sage.ui.Ui;

/** Command that ends the Sage session. */
public class ExitCommand extends Command {
    /** Creates an exit command. */
    public ExitCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        ui.show("Bye. Hope to see you again soon!");
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
