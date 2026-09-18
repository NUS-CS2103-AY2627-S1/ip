package oreo.command;

import oreo.core.AppContext;
import oreo.core.Parser;

/** Command that displays the current task list. */
public class ListCommand extends Command {
    /** Creates a command that displays the task list. */
    public ListCommand(Parser parser) {
        super(parser);
    }

    /** Displays all tasks currently in the list. */
    @Override
    public void execute(AppContext context) {
        context.getUi().showTaskList(context.getTasks());
    }
}
