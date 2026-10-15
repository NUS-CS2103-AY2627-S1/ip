package elsa.command;

import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.ui.Ui;

/** Shows the tasks whose description contains any of one or more keywords. */
public class FindCommand extends Command {
    private final String[] keywords;

    /**
     * Creates a command that will show the tasks matching any of the given
     * keywords.
     * Written as varargs so that the parser can hand over however many words the
     * user typed without wrapping them up first, and so that a single keyword,
     * which is the usual case, still reads as one argument at the call site.
     *
     * @param keywords the texts the user is looking for, one or more
     */
    public FindCommand(String... keywords) {
        assert keywords.length > 0 : "find needs a keyword to look for";
        this.keywords = keywords;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing changes, so the list is not saved.
        return ui.getMatchingTasksMessage(tasks, keywords);
    }
}
