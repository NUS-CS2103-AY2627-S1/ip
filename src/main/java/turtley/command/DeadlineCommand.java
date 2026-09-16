package turtley.command;

import turtley.exception.TurtleyException;
import turtley.model.Deadline;
import turtley.model.Task;
import turtley.util.DateTimeParser;

/**
 * Adds a deadline task.
 */
public class DeadlineCommand extends AddCommand {

    private final String input;

    /**
     * Creates a deadline command.
     *
     * @param input the description and deadline text.
     */
    public DeadlineCommand(String input) {
        this.input = input;
    }

    /**
     * Parses the command input into a deadline task.
     *
     * @return the parsed deadline task.
     * @throws TurtleyException if the input does not contain a description and deadline.
     */
    @Override
    protected Task createTask() {
        TagParser.ParsedTags parsedInput = TagParser.parseOptionalModifier(
                input, "Invalid format. Use: deadline <description> /by <date> [/tag #tag1 #tag2 ...]");
        String taskInput = parsedInput.content();
        if (TagParser.countModifier(taskInput, "/by") != 1) {
            throw invalidFormat();
        }
        int byIndex = taskInput.indexOf(" /by ");
        if (byIndex <= 0 || byIndex + 5 >= taskInput.length()) {
            throw invalidFormat();
        }

        String description = taskInput.substring(0, byIndex).trim();
        String by = taskInput.substring(byIndex + 5).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw invalidFormat();
        }
        return new Deadline(description, DateTimeParser.parse(by), parsedInput.tags());
    }

    /**
     * Creates the standard error for malformed deadline commands.
     *
     * @return the deadline format error.
     */
    private TurtleyException invalidFormat() {
        return new TurtleyException(
                "Invalid format. Use: deadline <description> /by <date> [/tag #tag1 #tag2 ...]");
    }
}
