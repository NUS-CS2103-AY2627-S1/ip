package oreo.command;

import oreo.core.AppContext;
import oreo.core.Parser;

/** Displays all tasks with any attached merchandise details. */
public class ListAllCommand extends Command {
    /** Creates a list-all command. */
    public ListAllCommand(Parser parser) {
        super(parser);
    }

    /** Displays all tasks and their merchandise. */
    @Override
    public void execute(AppContext context) {
        context.getUi().showAllTasks(context.getTasks());
    }
}
