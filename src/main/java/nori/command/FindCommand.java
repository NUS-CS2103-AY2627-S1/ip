package nori.command;

import nori.storage.Storage;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
class FindCommand extends InputCommand {
    /**
     * Creates a keyword-search command.
     *
     * @param details the keyword after the {@code find} keyword.
     */
    FindCommand(String details) {
        super(details);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (details.isEmpty()) {
            ui.showError("NOOT?! \"find\" needs a keyword."
                    + " Try \"find book\"; even penguins need a clue.");
            return;
        }
        ui.showResponse(tasks.getTasksMatchingKeywordDisplayLines(details));
    }
}
