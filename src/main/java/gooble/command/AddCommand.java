package gooble.command;

import gooble.GoobleException;
import gooble.task.Task;

/** Handles adding a general task. */
public class AddCommand extends Command {
    /** Creates an add command from complete user input. */
    public AddCommand(String input) {
        super(input);
    }

    /** Adds a general task described by the command input. */
    public void execute(CommandContext context) throws GoobleException {
        String description = context.parser().argumentAfter(input, "add");
        context.parser().validateDescription(description);
        try {
            context.tasks().add(new Task(description));
            context.ui().showAddedGeneral(description);
        } catch (IllegalArgumentException e) {
            throw new GoobleException(e.getMessage());
        }
    }
}
