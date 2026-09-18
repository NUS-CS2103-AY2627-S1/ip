package hermes.command;

import hermes.HermesException;
import hermes.task.LogBook;

/** Resets the list to the previous stage by one step */
public class UndoCommand extends Command {
    @Override
    public String execute(LogBook logBook) throws HermesException {
        return logBook.undo();
    }
}
