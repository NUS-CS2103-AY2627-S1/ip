package bibi.command;

import bibi.task.Task;

/**
 * Shows the tasks whose description contains a given keyword.
 */
public class FindCommand extends FilterCommand {
    private final String keyword;

    /**
     * Creates a command that will search for the given keyword.
     *
     * @param keyword the text to look for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    protected boolean matches(Task task) {
        return task.hasKeyword(keyword);
    }

    @Override
    protected String getHeader() {
        return "Here is what matches:";
    }

    @Override
    protected String getNoMatchMessage() {
        return "Nothing matches '" + keyword + "'.";
    }
}
