package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;
import oreo.model.Task;

/** Replaces merchandise details attached to a task. */
public class EditMerchandiseCommand extends Command {
    private final String command;

    /** Creates an edit-merchandise command. */
    public EditMerchandiseCommand(String command, Parser parser) {
        super(parser);
        this.command = command;
    }

    /** Replaces and persists merchandise details for the selected task. */
    @Override
    public void execute(AppContext context) throws OreoException {
        String[] parts = parser.merchandiseParts(command, "Use: edit-merchandise TASK_NUMBER DETAILS");
        Task task = context.getTasks().get(parser.taskIndex(parts[0], context.getTasks().size()));
        if (!task.hasMerchandise()) {
            throw new OreoException("This task has no merchandise to edit.");
        }
        task.setMerchandise(parts[1]);
        context.getStorage().save(context.getTasks());
        context.getUi().showMerchandise("Noted. I've updated merchandise for this task:", task);
    }
}
