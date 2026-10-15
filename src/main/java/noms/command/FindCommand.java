package noms.command;

import noms.parser.Parser;
import noms.storage.Storage;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Finds the tasks whose description contains a given keyword. The keyword is
 * extracted by the {@link Parser} before this command is created.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * @param keyword the text to search for within task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(keyword, tasks.find(keyword));
    }
}
