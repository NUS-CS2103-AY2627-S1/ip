package chattingheads.command;

import chattingheads.exception.ChattingHeadsException;
import chattingheads.task.TaskList;
import chattingheads.ui.Ui;

/**
 * Represents a command that can be executed on the task list.
 */
public abstract class Command {

    /**
     * Executes this command.
     *
     * @param taskList Task list on which the command operates.
     * @param ui       User interface used to display command results.
     * @return Response from execution of command.
     * @throws ChattingHeadsException If an error occurs while executing the command.
     */
    public abstract String execute(TaskList taskList, Ui ui) throws ChattingHeadsException;

    /**
     * Returns whether this command exits the application.
     *
     * @return {@code true} if this command exits the application.
     */
    public boolean shouldExit() {
        return false;
    }

    /**
     * Returns whether this command modifies persistent task data.
     *
     * @return {@code true} if changes made by this command should be saved.
     */
    public boolean shouldSave() {
        return false;
    }
}
