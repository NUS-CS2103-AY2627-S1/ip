package milo.command;

import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/** Represents the "bye" command, which ends the session. */
public class ExitCommand extends Command {
    /**
     * Does nothing: {@code Milo.run()} prints the goodbye message itself,
     * once, after its command loop ends — whether that is because of "bye"
     * or because the input simply ran out. Printing it here too would show
     * it twice when the user actually types "bye".
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Intentionally empty; see class Javadoc.
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
