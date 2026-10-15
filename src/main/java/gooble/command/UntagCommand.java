package gooble.command;

import gooble.GoobleException;

/** Handles removing all tags from a task. */
public class UntagCommand extends Command {
    /** Creates an untag command from complete user input. */
    public UntagCommand(String input) {
        super(input);
    }

    /** Removes all tags from the selected task. */
    @Override
    public void execute(CommandContext context) throws GoobleException {
        String number = context.parser().argumentAfter(input, "untag");
        int index = parseTaskNumber(number);
        if (!context.tasks().isValidIndex(index)) {
            context.ui().showMessage("That task number does not exist. Gooble searched everywhere, "
                    + "even under the digital couch.");
            return;
        }
        if (!context.tasks().get(index).hasTags()) {
            context.ui().showMessage("This task has no tags to remove. Gooble found only tag-shaped air.");
            return;
        }
        context.tasks().removeTags(index);
        context.ui().showUntagged(context.tasks().get(index));
    }

    /** Converts a one-based task number into a zero-based index. */
    private int parseTaskNumber(String number) throws GoobleException {
        try {
            return Integer.parseInt(number) - 1;
        } catch (NumberFormatException e) {
            throw new GoobleException("Please provide a valid task number. Gooble cannot untag task-shaped air.");
        }
    }
}
