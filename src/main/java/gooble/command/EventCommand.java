package gooble.command;

import gooble.GoobleException;
import gooble.task.Event;

/** Handles adding an event task. */
public class EventCommand extends Command {
    /** Creates an event command from complete user input. */
    public EventCommand(String input) {
        super(input);
    }

    /** Adds an event task described by the command input. */
    public void execute(CommandContext context) throws GoobleException {
        String[] parts = context.parser().parseEvent(input);
        try {
            context.tasks().add(new Event(parts[0], parts[1], parts[2]));
        } catch (IllegalArgumentException e) {
            throw new GoobleException(e.getMessage());
        }
        context.ui().showAdded(context.tasks().get(context.tasks().size() - 1));
        context.ui().showTaskCount(context.tasks().size());
    }
}
