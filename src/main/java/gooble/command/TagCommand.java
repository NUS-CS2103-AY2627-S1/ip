package gooble.command;

import gooble.GoobleException;

/** Handles adding one tag to a task. */
public class TagCommand extends Command {
    /** Creates a tag command from complete user input. */
    public TagCommand(String input) {
        super(input);
    }

    /** Adds the supplied tag to the selected task. */
    @Override
    public void execute(CommandContext context) throws GoobleException {
        String[] parts = context.parser().parseTag(input);
        int index = parseTaskNumber(parts[0]);
        if (!context.tasks().isValidIndex(index)) {
            context.ui().showMessage("That task number does not exist. Gooble searched everywhere, "
                    + "even under the digital couch.");
            return;
        }
        try {
            context.tasks().addTag(index, parts[1]);
            context.ui().showTagged(context.tasks().get(index));
        } catch (IllegalArgumentException e) {
            context.ui().showMessage(e.getMessage());
        }
    }

    /** Converts a one-based task number into a zero-based index. */
    private int parseTaskNumber(String number) throws GoobleException {
        try {
            return Integer.parseInt(number) - 1;
        } catch (NumberFormatException e) {
            throw new GoobleException("Please provide a valid task number.");
        }
    }
}
