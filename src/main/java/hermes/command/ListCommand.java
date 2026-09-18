package hermes.command;

import hermes.task.LogBook;

/** Shows every task, numbered. */
public class ListCommand extends Command {

    @Override
    public String execute(LogBook logBook) {
        return logBook.listTasks();
    }
}
