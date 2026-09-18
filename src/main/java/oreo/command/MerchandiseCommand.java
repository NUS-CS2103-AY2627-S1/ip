package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;
import oreo.model.Task;

/** Adds merchandise details to a task. */
public class MerchandiseCommand extends Command {
    private final String command;

    /** Creates an add-merchandise command. */
    public MerchandiseCommand(String command, Parser parser) {
        super(parser);
        this.command = command;
    }

    /** Adds and persists merchandise details for the selected task. */
    @Override
    public void execute(AppContext context) throws OreoException {
        String[] parts = parser.merchandiseParts(command, "Use: merchandise TASK_NUMBER DETAILS");
        Task task = context.getTasks().get(parser.taskIndex(parts[0], context.getTasks().size()));
        if (task.hasMerchandise()) {
            throw new OreoException("This task already has merchandise. Use edit-merchandise instead.");
        }
        task.setMerchandise(parts[1]);
        context.getStorage().save(context.getTasks());
        context.getUi().showMerchandise("Got it. I've added merchandise to this task:", task);
    }
}
