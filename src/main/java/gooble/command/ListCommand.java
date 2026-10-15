package gooble.command;

import gooble.GoobleException;

/**
 * Handles the basic list command.
 */
public class ListCommand extends Command {
    /** Creates a list command from complete user input. */
    public ListCommand(String input) {
        super(input);
    }

    @Override
    public void execute(CommandContext context) throws GoobleException {
        if (!input.equals("list")) {
            throw new GoobleException("Please use: list from yyyy-MM-dd HHmm to yyyy-MM-dd HHmm");
        }
        context.ui().showTasks(context.tasks());
    }
}
