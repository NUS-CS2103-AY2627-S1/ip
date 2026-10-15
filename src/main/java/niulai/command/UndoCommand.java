package niulai.command;

import niulai.model.TaskList;
import niulai.service.Storage;
import niulai.service.Ui;

/** Represents a request to undo the most recent successful state-changing command. */
public class UndoCommand extends Command {
    /** Creates an undo command. */
    public UndoCommand() {
        super(Type.UNDO);
    }

    /** The chatbot handles the stored inverse action for this command. */
    @Override
    public UndoAction execute(TaskList tasks, Ui ui, Storage storage) {
        return null;
    }

    /** @return true because this command requests the stored inverse action */
    @Override
    public boolean isUndo() {
        return true;
    }
}
