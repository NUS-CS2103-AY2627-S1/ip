package turtley.command;

import turtley.exception.TurtleyException;
import turtley.model.Event;
import turtley.model.Task;
import turtley.util.DateTimeParser;

/**
 * Adds an event task.
 */
public class EventCommand extends AddCommand {

    private final String input;

    /**
     * Creates an event command.
     *
     * @param input the description and event time range.
     */
    public EventCommand(String input) {
        this.input = input;
    }

    /**
     * Parses the command input into an event task.
     *
     * @return the parsed event task.
     * @throws TurtleyException if the input does not contain a description and time range.
     */
    @Override
    protected Task createTask() {
        TagParser.ParsedTags parsedInput = TagParser.parseOptionalModifier(
                input, "Invalid format. Use: event <description> /from <start> /to <end> "
                        + "[/tag #tag1 #tag2 ...]");
        String taskInput = parsedInput.content();
        if (TagParser.countModifier(taskInput, "/from") != 1
                || TagParser.countModifier(taskInput, "/to") != 1) {
            throw invalidFormat();
        }
        int fromIndex = taskInput.indexOf(" /from ");
        int toIndex = fromIndex < 0 ? -1 : taskInput.indexOf(" /to ", fromIndex + 6);
        if (fromIndex <= 0 || toIndex <= fromIndex + 6 || toIndex + 5 >= taskInput.length()) {
            throw invalidFormat();
        }

        String description = taskInput.substring(0, fromIndex).trim();
        String from = taskInput.substring(fromIndex + 6, toIndex).trim();
        String to = taskInput.substring(toIndex + 5).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw invalidFormat();
        }
        return new Event(description, DateTimeParser.parse(from), DateTimeParser.parse(to), parsedInput.tags());
    }

    /**
     * Creates the standard error for malformed event commands.
     *
     * @return the event format error.
     */
    private TurtleyException invalidFormat() {
        return new TurtleyException("Invalid format. Use: event <description> /from <start> /to <end> "
                + "[/tag #tag1 #tag2 ...]");
    }
}
