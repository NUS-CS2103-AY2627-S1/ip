package sage.command;

import java.io.IOException;

import sage.exception.SageException;
import sage.model.Task;
import sage.model.TaskList;
import sage.storage.Storage;
import sage.ui.Ui;

/** Command that displays tasks whose descriptions contain a keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command for the supplied keyword.
     *
     * @param keyword keyword to search for
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SageException {
        if (keyword.isBlank()) {
            throw new SageException("Please provide a keyword to find, like `find book`.");
        }

        boolean isFound = false;
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (task.getDescription().contains(keyword)) {
                if (!isFound) {
                    ui.show("Here are the matching tasks in your list:");
                }
                ui.show((index + 1) + "." + task);
                isFound = true;
            }
        }

        if (!isFound) {
            ui.show("There are no tasks matching \"" + keyword + "\".");
        }
    }
}
