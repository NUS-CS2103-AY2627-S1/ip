package oreo.command;

import oreo.core.OreoException;
import oreo.core.Parser;
import oreo.model.Event;
import oreo.model.Task;

/** Command that adds an event task. */
public class EventCommand extends AddTaskCommand {
    private final String command;

    /** Creates an event command. */
    public EventCommand(String command, Parser parser) {
        super(parser);
        this.command = command;
    }

    /** Parses the command argument into an event task. */
    @Override
    protected Task createTask() throws OreoException {
        String[] parts = parser.eventParts(command);
        try {
            return new Event(parts[0], parts[1], parts[2]);
        } catch (IllegalArgumentException e) {
            throw new OreoException(e.getMessage());
        }
    }
}
