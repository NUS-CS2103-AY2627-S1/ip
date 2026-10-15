package gooble.command;

import gooble.GoobleException;

/** Handles finding tasks whose descriptions contain a keyword. */
public class FindCommand extends Command {
    public FindCommand(String input) {
        super(input);
    }

    @Override
    public void execute(CommandContext context) throws GoobleException {
        String keyword = context.parser().argumentAfter(input, "find");
        context.parser().validateDescription(keyword);
        context.ui().showMatchingTasks(context.tasks().findByDescription(keyword));
    }
}
