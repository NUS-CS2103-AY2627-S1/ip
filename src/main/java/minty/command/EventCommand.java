package minty.command;

import minty.exception.MintyException;
import minty.task.Task;

/**
 * Adds an event to Minty's task list.
 */
public class EventCommand extends AddCommand {

    /**
     * Creates an event command from the user's complete input.
     *
     * @param fullCommand complete event command.
     */
    public EventCommand(String fullCommand) {
        super(fullCommand);
    }

    /**
     * Parses the event represented by the complete command.
     *
     * @param fullCommand complete event command.
     * @return validated event.
     * @throws MintyException if the event details are invalid.
     */
    @Override
    protected Task createTask(String fullCommand) throws MintyException {
        return Parser.parseEvent(fullCommand);
    }
}
