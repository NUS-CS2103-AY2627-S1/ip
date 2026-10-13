package arrodes.command;

import arrodes.storage.Storage;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/** Command that displays every task in list order. */
public class ListCommand extends Command {
    /** Creates a list command. */
    public ListCommand() {
    }

    /**
     * Displays all remembered tasks.
     * @param ui interface used for output
     * @param taskList list to display
     * @param storage unused storage
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        StringBuilder output = new StringBuilder("Arrodes reveals the requests entrusted to its memory:\n");
        for (int i = 0; i < taskList.getSize(); i++) {
            Task task = taskList.getTaskByIndex(i);
            output.append("    ").append(i + 1).append('.').append(task).append('\n');
        }
        ui.showMessage(output.toString());
    }
}
