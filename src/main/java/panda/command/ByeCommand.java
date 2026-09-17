package panda.command;

import panda.lifecycle.TerminationManager;
import panda.task.TaskList;

/**
 * Terminates Panda after confirmation from the graphical interface.
 */
public class ByeCommand implements Command {

    /**
     * Terminates the program after the graphical interface confirms the exit.
     *
     * @param taskList Active task list.
     */
    @Override
    public void execute(TaskList taskList) {
        TerminationManager.terminate();
    }
}
