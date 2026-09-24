package hampster.command;

import hampster.exception.HampsterException;
import hampster.parser.TagParser;
import hampster.task.Task;
import hampster.task.TaskList;
import hampster.ui.Ui;

/** Command that assigns one tag to a task. */
public class TagCommand extends Command {

    private final int taskNumber;
    private final String tag;

    /** Creates a tag command from the user's input parts. */
    public TagCommand(String[] parts) throws HampsterException {
        if (parts.length != 3) {
            throw new HampsterException("Tag requires a task number and one brand for the dossier.");
        }
        taskNumber = parseTaskNumber(new String[] {parts[0], parts[1]}, "Tag");
        tag = TagParser.normalize(parts[2]);
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws HampsterException {
        Task task = getTask(tasks);
        task.setTag(tag);
        ui.showMessage("\tBrand applied. This task now belongs to Hampster.", "\t" + task);
    }

    private Task getTask(TaskList tasks) throws HampsterException {
        if (taskNumber > tasks.size()) {
            throw new HampsterException("Task " + taskNumber + " has escaped my lair.");
        }
        return tasks.get(taskNumber - 1);
    }
}
