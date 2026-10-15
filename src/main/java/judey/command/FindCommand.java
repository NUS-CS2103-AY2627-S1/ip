package judey.command;

import judey.exception.JudeyException;
import judey.storage.Storage;
import judey.tasklist.TaskList;
import judey.ui.Ui;

/**
 * Represents a command to search for tasks containing a specific keyword in their description.
 */
public class FindCommand extends Command{
    private final String keyword;

    /**
     * Constructs a {@code FindCommand} with the specific search keyword
     * @param keyword text phrase to search for within task descriptions
     */
    public FindCommand(String keyword){
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws JudeyException {
        tasks.findTasks(keyword);
    }
}
