package alfred.command;

import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Lists tasks whose description contains a given keyword.
 */
public class FindCommand extends Command {
    /** Substring to match against task descriptions. */
    private final String keyword;

    /**
     * Creates a command that searches descriptions for {@code keyword}.
     *
     * @param keyword Text typed after the {@code find} command.
     */
    public FindCommand(String keyword) {
        // Parser rejects a missing or blank keyword before constructing this command.
        assert keyword != null && !keyword.isBlank() : "Find keyword should already be validated";
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showFoundTasks(tasks.find(keyword));
    }
}
