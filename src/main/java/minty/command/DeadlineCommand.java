package minty.command;

import minty.exception.MintyException;
import minty.task.Task;

/**
 * Adds a deadline to Minty's task list.
 */
public class DeadlineCommand extends AddCommand {

    /**
     * Creates a deadline command from the user's complete input.
     *
     * @param fullCommand complete deadline command.
     */
    public DeadlineCommand(String fullCommand) {
        super(fullCommand);
    }

    /**
     * Parses the deadline represented by the complete command.
     *
     * @param fullCommand complete deadline command.
     * @return validated deadline.
     * @throws MintyException if the deadline details are invalid.
     */
    @Override
    protected Task createTask(String fullCommand) throws MintyException {
        return Parser.parseDeadline(fullCommand);
    }
}
