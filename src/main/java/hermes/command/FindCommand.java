package hermes.command;

import hermes.task.LogBook;

/** Finds tasks based on keyword */
public class FindCommand extends Command {

    private final String keyword;

    /**
     * @param keyword used to find tasks whose taskDescription contains the keyword.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(LogBook logBook) {
        return logBook.findTasks(keyword);
    }
}
