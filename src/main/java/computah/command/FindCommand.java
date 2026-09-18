package computah.command;

import java.util.ArrayList;
import java.util.stream.Collectors;

import computah.model.Model;
import computah.storage.Storage;
import computah.task.Task;
import computah.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks containing the given keyword.
     *
     * @param keyword keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shows tasks whose descriptions contain the keyword.
     *
     * @param model current application data.
     * @param ui user interface used to display matching tasks.
     * @param storage storage component; unused for this command.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) {
        ArrayList<Task> matchingTasks = model.getTasks().stream()
                .filter(task -> task.hasDescriptionContaining(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        ui.showMatchingTasks(matchingTasks);
    }
}
