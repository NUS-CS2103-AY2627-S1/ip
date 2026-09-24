package hampster.command;

import hampster.exception.HampsterException;
import hampster.task.TaskList;
import hampster.ui.Ui;

/** Command that finds tasks containing a keyword. */
public class FindCommand extends Command {

    private final String keyword;

    /** Creates a find command for the requested keyword. */
    public FindCommand(String[] parts) throws HampsterException {
        if (parts.length != 2) {
            throw new HampsterException(
                    "Find requires exactly one keyword for my secret search.");
        }

        keyword = parts[1];
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        TaskList matchingTasks = tasks.find(keyword);

        ui.showMessage("\tMy spies found these tasks in the dossier:");

        if (matchingTasks.size() == 0) {
            ui.showMessage("\tNothing escaped into the dossier. Try another keyword.");
        }

        for (int i = 0; i < matchingTasks.size(); i++) {
            ui.showMessage("\t" + (i + 1) + ". " + matchingTasks.get(i));
        }
    }
}
