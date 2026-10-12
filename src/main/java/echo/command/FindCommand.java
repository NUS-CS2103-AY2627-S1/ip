package echo.command;

import java.util.List;

import echo.task.TaskManager;
import echo.ui.Ui;

/** Finds tasks whose descriptions contain a keyword. */
public class FindCommand extends Command {
    /**
     * Creates a find command.
     *
     * @param keyword keyword to search for.
     */
    public FindCommand(String keyword) {
        super(Type.FIND, List.of(keyword));
    }

    /**
     * Displays tasks whose descriptions contain the supplied keyword.
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) {
        String keyword = getArgument(0);
        return ui.showFindResults(keyword, taskManager.findByKeyword(keyword));
    }
}
