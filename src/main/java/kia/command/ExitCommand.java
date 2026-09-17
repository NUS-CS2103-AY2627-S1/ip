package kia.command;

import java.util.ArrayList;

import kia.task.Task;
import kia.ui.Ui;

/** A command that says goodbye and terminates Kia's command loop. */
public class ExitCommand extends Command {
    /** Creates an exit command. */
    public ExitCommand() {
    }

    /**
     * Displays the farewell message.
     *
     * @param tasks the current tasks, which are unchanged by this command
     * @param ui the user-interface boundary used for output
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui) {
        ui.showBye();
    }

    /**
     * Indicates that the command loop should terminate.
     *
     * @return always {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
