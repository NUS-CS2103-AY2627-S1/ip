package herta.command;

import java.util.Locale;

import herta.storage.Storage;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that searches task descriptions for a keyword.
 */
public class FindCommand extends QueryCommand {
    private static final String FIND_HEADING = "Found them. Here are the matches:";
    private static final String FIND_EMPTY_MESSAGE = "Nothing matched. Try a more useful keyword.";
    private final String normalizedKeyword;

    /**
     * Creates a command that searches task descriptions for the given keyword.
     *
     * @param keyword the text to find in task descriptions.
     */
    public FindCommand(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "A find command must contain a non-blank keyword.");
        }
        normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
    }

    /**
     * Displays tasks whose descriptions contain the search keyword.
     *
     * @param tasks the task list to search.
     * @param ui the output interface used to display responses.
     * @param storage unused because searching does not change stored data.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) {
        showMatchingTasksWithoutEmptyHeading(tasks,
                task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword),
                FIND_HEADING,
                FIND_EMPTY_MESSAGE,
                ui);
    }
}
