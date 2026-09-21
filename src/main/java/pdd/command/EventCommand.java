package pdd.command;

import pdd.task.Event;

/** Adds an already-parsed {@link Event} to the task list. */
public class EventCommand extends AddTaskCommand {
    /** Creates a command that will add the given already-parsed event to the task list. */
    public EventCommand(Event event) {
        super(event);
    }
}
