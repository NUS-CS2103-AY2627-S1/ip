package turtley.command;

import turtley.exception.TurtleyException;
import turtley.model.Task;
import turtley.model.ToDo;

/**
 * Adds a to-do task.
 */
public class TodoCommand extends AddCommand {

    private final String description;

    /**
     * Creates a to-do command.
     *
     * @param description the to-do description.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /**
     * Creates a to-do task from the supplied description.
     *
     * @return the new to-do task.
     */
    @Override
    protected Task createTask() {
        TagParser.ParsedTags parsedInput = TagParser.parseOptionalModifier(
                description, "Invalid format. Use: todo <description> [/tag #tag1 #tag2 ...]");
        if (parsedInput.content().isBlank()) {
            throw new TurtleyException("Invalid format. Use: todo <description> [/tag #tag1 #tag2 ...]");
        }
        return new ToDo(parsedInput.content(), parsedInput.tags());
    }
}
