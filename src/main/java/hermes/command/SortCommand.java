package hermes.command;

import hermes.HermesException;
import hermes.task.LogBook;

/** Reorders tasks by their due date and stored the updated order. */
public class SortCommand extends Command {

    @Override
    public String execute(LogBook logBook) throws HermesException {
        return logBook.sort();
    }
}
