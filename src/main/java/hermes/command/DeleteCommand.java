package hermes.command;

import hermes.HermesException;
import hermes.task.LogBook;

/** Removes one or more tasks from the list. */
public class DeleteCommand extends Command {

    private final int[] indexes;

    /**
     * @param indexes the tasks' positions in the list, counting from zero.
     */
    public DeleteCommand(int... indexes) {
        this.indexes = indexes;
    }

    @Override
    public String execute(LogBook logBook) throws HermesException {
        return logBook.delete(this.indexes);
    }
}
