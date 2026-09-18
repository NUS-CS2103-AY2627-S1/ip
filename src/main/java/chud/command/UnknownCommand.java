package chud.command;

import chud.exception.ChudException;
import chud.storage.Storage;
import chud.task.TaskList;
import chud.ui.Ui;

/** Reports that the typed command word wasn't recognized. */
public class UnknownCommand extends Command {
    private final String commandWord;

    /** Creates a command that will report the given unrecognized command word as an error. */
    public UnknownCommand(String commandWord) {
        this.commandWord = commandWord;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        throw new ChudException("I don't know what '" + commandWord + "' means. "
                + "Try list, todo, deadline, event, on, find, mark, unmark, delete, or bye.");
    }
}
