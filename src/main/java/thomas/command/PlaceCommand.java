package thomas.command;

import thomas.ThomasException;
import thomas.place.PlaceList;
import thomas.storage.PlaceStorage;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/** Represents a command that operates on the separate place collection. */
public abstract class PlaceCommand extends Command {
    /** Executes this command against places. */
    public abstract void execute(PlaceList places, Ui ui, PlaceStorage storage) throws ThomasException;

    /** Prevents a place command from being run as a task command. */
    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage) {
        throw new UnsupportedOperationException("Place commands require place storage.");
    }
}
