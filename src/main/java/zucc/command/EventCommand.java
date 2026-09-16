package zucc.command;

import zucc.ZuccException;
import zucc.task.Event;
import zucc.task.Priority;
import zucc.task.Task;

/**
 * Adds an event task from a raw description, start value, and end value.
 */
public final class EventCommand extends AddTaskCommand {
    /** Creates an event command awaiting values from Parser. */
    EventCommand() {
        super("event", "/from", "/to", "/priority");
    }

    /**
     * Validates required input and constructs the event task.
     *
     * @return constructed event task.
     * @throws ZuccException if the description, start, or end is missing or malformed.
     */
    @Override
    protected Task createTask() throws ZuccException {
        return new Event(
                requireArgument("a description"),
                require("/from"),
                require("/to"),
                Priority.fromUserInput(getOption("/priority")));
    }
}
