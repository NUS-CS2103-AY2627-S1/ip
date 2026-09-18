package herta.command;

import herta.task.Event;

/**
 * Represents the command that adds an event task.
 */
public class EventCommand extends AddCommand {

    /**
     * Creates a command that adds the given event task.
     *
     * @param event the event task to add.
     */
    public EventCommand(Event event) {
        super(event);
    }
}
