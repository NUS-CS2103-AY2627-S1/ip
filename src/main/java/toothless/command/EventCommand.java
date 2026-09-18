package toothless.command;

import toothless.task.Event;
import toothless.task.Task;

/**
 * Coordinates creating an event and persisting the updated task list.
 */
public class EventCommand extends AddCommand {
    private final String description;
    private final String from;
    private final String to;

    /**
     * Creates an event command from validated task details.
     *
     * @param description event description obtained from the parser.
     * @param from event starting time obtained from the parser.
     * @param to event ending time obtained from the parser.
     */
    public EventCommand(String description, String from, String to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /**
     * Creates the event handled by the shared add workflow.
     *
     * @return event created from the parsed description and timing values
     */
    @Override
    protected Task createTask() {
        return new Event(description, from, to);
    }
}
