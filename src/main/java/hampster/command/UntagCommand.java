package hampster.command;

import hampster.exception.HampsterException;
import hampster.parser.TagParser;
import hampster.task.Task;
import hampster.task.TaskList;
import hampster.ui.Ui;

/** Command that removes one tag from a task. */
public class UntagCommand extends Command {

    private final int taskNumber;
    private final String tag;

    /** Creates an untag command from the user's input parts. */
    public UntagCommand(String[] parts) throws HampsterException {
        if (parts.length != 3) {
            throw new HampsterException("Untag requires a task number and one brand to erase.");
        }
        taskNumber = parseTaskNumber(new String[] {parts[0], parts[1]}, "Untag");
        tag = TagParser.normalize(parts[2]);
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws HampsterException {
        if (taskNumber > tasks.size()) {
            throw new HampsterException("Task " + taskNumber + " has escaped my lair.");
        }

        Task task = tasks.get(taskNumber - 1);
        if (task.getTag().equals(tag)) {
            task.removeTag();
        }
        ui.showMessage("\tBrand erased. The dossier is mine again.", "\t" + task);
    }
}
