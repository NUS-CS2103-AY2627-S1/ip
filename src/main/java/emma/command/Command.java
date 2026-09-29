package emma.command;

import emma.EmmaException;
import emma.Storage;
import emma.TaskList;

/**
 * One thing the user asked Emma to do, ready to be carried out.
 */
public interface Command {

    /**
     * Carries out the command.
     *
     * @param tasks the tasks Emma is tracking.
     * @param storage where any change is saved.
     * @return Emma's response.
     * @throws EmmaException if the command cannot be carried out
     */
    String execute(TaskList tasks, Storage storage) throws EmmaException;

    /**
     * Tells whether Emma should stop after this command.
     *
     * @return true only for the command that ends the conversation.
     */
    default boolean isExit() {
        return false;
    }
}
