package edith.command;

import edith.task.TaskList;
import edith.ui.Ui;

/** Ends the current Edith session. */
public class ExitCommand extends Command {
    /**
     * Displays Edith's farewell message.
     *
     * @param tasks the current task list, which is not modified
     * @param ui the user interface used to display the farewell message
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showGoodbye();
    }

    /**
     * Indicates that Edith should stop after this command executes.
     *
     * @return {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
