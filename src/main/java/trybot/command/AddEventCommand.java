package trybot.command;

import trybot.exception.TryBotException;
import trybot.parser.Parser;
import trybot.task.Event;
import trybot.task.Task;

/**
 * Adds an event task.
 */
public class AddEventCommand extends AddCommand {
    private final String description;
    private final String from;
    private final String to;

    /**
     * Creates an event command.
     *
     * @param event parsed event fields.
     */
    public AddEventCommand(Parser.ParsedEvent event) {
        this.description = event.description();
        this.from = event.from();
        this.to = event.to();
    }

    /**
     * Builds an event from the parsed command fields.
     *
     * @return the event task
     * @throws TryBotException if the event date or time is invalid
     */
    @Override
    protected Task createTask() throws TryBotException {
        try {
            return new Event(description, from, to);
        } catch (IllegalArgumentException exception) {
            throw new TryBotException(exception.getMessage() == null
                    ? "The event date or time is invalid." : exception.getMessage());
        }
    }
}
