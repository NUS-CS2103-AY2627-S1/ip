package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;
import oreo.model.Task;

/** Removes merchandise details attached to a task. */
public class DeleteMerchandiseCommand extends Command {
    private final String taskNumber;

    /** Creates a delete-merchandise command. */
    public DeleteMerchandiseCommand(String taskNumber, Parser parser) {
        super(parser);
        this.taskNumber = taskNumber;
    }

    /** Removes and persists merchandise details for the selected task. */
    @Override
    public void execute(AppContext context) throws OreoException {
        if (taskNumber.isEmpty()) {
            throw new OreoException("Use: delete-merchandise TASK_NUMBER");
        }
        Task task = context.getTasks().get(parser.taskIndex(taskNumber, context.getTasks().size()));
        if (!task.hasMerchandise()) {
            throw new OreoException("This task has no merchandise to delete.");
        }
        task.clearMerchandise();
        context.getStorage().save(context.getTasks());
        context.getUi().showMerchandise("Noted. I've removed merchandise from this task:", task);
    }
}
