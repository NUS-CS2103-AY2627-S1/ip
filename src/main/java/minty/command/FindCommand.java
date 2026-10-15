package minty.command;

import minty.exception.MintyException;
import minty.storage.Storage;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String fullCommand;

    /**
     * Creates a find command from the user's complete input.
     *
     * @param fullCommand complete find command.
     */
    public FindCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Parses the keyword and displays every matching task.
     *
     * @param tasks task list to search.
     * @param ui command-line interface used for the response.
     * @param storage task storage, which is not used.
     * @throws MintyException if the keyword is missing.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException {
        String keyword = Parser.parseFindKeyword(fullCommand);
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
