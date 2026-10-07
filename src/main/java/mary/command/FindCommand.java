package mary.command;

import java.util.List;

import mary.exception.MaryException;
import mary.storage.Storage;
import mary.task.Task;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Searches task descriptions without changing tasks or saved data.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a search command from the text following the command word.
     *
     * @param keyword non-null search text; outer whitespace is ignored.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword.strip();
    }

    /**
     * Displays matching tasks numbered from one in result order.
     *
     * @param tasks task collection to search.
     * @param ui handler for invalid-input messages.
     * @param storage unused because searching does not save data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            if (keyword.isBlank()) {
                throw new MaryException("use 'find keyword', for example 'find book'.");
            }
            List<Task> matches = tasks.find(keyword);
            if (matches.isEmpty()) {
                ui.showMessage(" No matching tasks in sight. Try another keyword.");
                return;
            }
            ui.showMessage(" Look what I sniffed out:");
            for (int i = 0; i < matches.size(); i++) {
                ui.showMessage(" " + (i + 1) + "." + matches.get(i));
            }
        } catch (MaryException exception) {
            ui.showError(exception.getMessage());
        }
    }
}
