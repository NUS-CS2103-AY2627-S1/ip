package hermes.command;

import hermes.HermesException;
import hermes.task.LogBook;

/** Marks task(s) as completed. */
public class MarkCommand extends Command {

    private final int[] indexes;

    /**
     * @param indexes the task's position in the list, counting from zero.
     */
    public MarkCommand(int... indexes) {
        this.indexes = indexes;
    }

    @Override
    public String execute(LogBook logBook) throws HermesException {
        return logBook.mark(this.indexes);
    }
}
