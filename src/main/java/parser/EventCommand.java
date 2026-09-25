package parser;

import exception.FriedbergException;
import task.Event;

/**
 * Handles the 'event' command for Friedberg.
 * Adds an event to the list of tasks.
 */
public class EventCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "event");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        Event event = parseEvent(userInput);
        commandContext.addTask(event);
        return TaskResponseFormatter.formatAddition(event, commandContext.getTasksSize());
    }

    /**
     * Parses an event without changing tasks or storage.
     *
     * @param userInput event command containing a description and start/end dates
     * @return event represented by the command
     * @throws FriedbergException if the command word, delimiters, or dates are invalid
     */
    private Event parseEvent(String userInput) throws FriedbergException {
        ParserUtil.ParsedEvent event = ParserUtil.parseEventCommand(userInput);
        return new Event(event.description(), event.fromDate(), event.toDate());
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
