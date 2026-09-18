package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;
import oreo.model.Task;

/** Command that removes one task from the list and persists the result. */
public class DeleteCommand extends Command {
    private final String taskNumber;

    /** Creates a delete command for the supplied one-based task number. */
    public DeleteCommand(String taskNumber, Parser parser) {
        super(parser);
        this.taskNumber = taskNumber;
    }

    /** Deletes the selected task, persists the list, and displays confirmation. */
    @Override
    public void execute(AppContext context) throws OreoException {
        Task task = context.getTasks().get(parser.taskIndex(taskNumber, context.getTasks().size()));
        context.getTasks().remove(task);
        context.getStorage().save(context.getTasks());
        context.getUi().showDeleted(task, context.getTasks().size());
    }
}
