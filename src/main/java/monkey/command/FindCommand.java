package monkey.command;

import java.util.Locale;

import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** Command object for displaying tasks whose descriptions contain a keyword. */
public class FindCommand extends CommandAction {
    private final String keyword;

    /** Creates a find command for the supplied keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword.trim();
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (keyword.isEmpty()) {
            ui.showMessage("OOPS! Monkey says: A find command needs a keyword.");
            return;
        }
        ui.showMessage("Here are the matching tasks in your list:");
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        int matchNumber = 1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase(Locale.ROOT).contains(lowerCaseKeyword)) {
                ui.showMessage(matchNumber + "." + tasks.get(i));
                matchNumber++;
            }
        }
    }
}
