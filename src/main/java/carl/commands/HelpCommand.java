package carl.commands;

import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;

/**
 * Displays the commands supported by Carl and their expected formats.
 */
public class HelpCommand implements Command {
    private static final String HELP_MESSAGE = """
            Available controls:
            `todo` <description>
            `deadline` <description> /by yyyy-MM-dd HHmm
            `event` <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm
            `list`
            `mark` <task number>
            `unmark` <task number>
            `delete` <task number>
            `find` <keyword>
            `due` [yyyy-MM-dd]
            `sort`
            `bye`""";

    /**
     * Returns the command reference without changing any task data.
     *
     * @param ui user interface for interacting with the user.
     * @param storage task manager handling task data persistence.
     * @param tasks list of current tasks.
     * @param raw raw input string entered by the user.
     * @return a successful result containing the command reference.
     */
    @Override
    public CommandResult onRun(Ui ui, TaskManager storage, TaskList tasks, String raw) {
        return CommandResult.createSuccess(HELP_MESSAGE);
    }
}
