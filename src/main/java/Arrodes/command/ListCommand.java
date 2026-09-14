package arrodes.command;
import arrodes.storage.Storage;
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
        String output = "Arrdoes recalls your request\n";
        for (int i = 0; i < taskList.getSize(); i++) {
            output = output + "    " + (i + 1) + "." + taskList.getTaskByIndex(i).toString() + "\n";
        }
        ui.showMessage(output);
    }
}
