package emma.command;

import emma.Storage;
import emma.TaskList;

/**
 * Shows the tasks whose description contains a piece of text.
 */
public class FindCommand implements Command {

    private final String keyword;

    /**
     * Creates the command.
     *
     * @param keyword the text to look for in each description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Lists the matching tasks, keeping their numbers from the full list. Saves nothing. */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        String matched = tasks.format(task -> task.hasDescriptionContaining(keyword));
        if (matched.isEmpty()) {
            return "Nothing has \"" + keyword + "\" in its description.";
        }
        return "Here's what I found:\n" + matched;
    }
}
