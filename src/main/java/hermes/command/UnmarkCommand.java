package hermes.command;

import hermes.HermesException;
import hermes.task.LogBook;

/** Marks task(s) as no longer completed. */
public class UnmarkCommand extends Command {

    private final int[] indexes;

    /**
     * @param indexes the task's position in the list, counting from zero.
     */
    public UnmarkCommand(int... indexes) {
        this.indexes = indexes;
    }

    @Override
    public String execute(LogBook logBook) throws HermesException {
        return logBook.unmark(this.indexes);
    }
}
