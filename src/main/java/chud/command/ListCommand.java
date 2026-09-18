package chud.command;

import chud.exception.ChudException;
import chud.parser.Parser;
import chud.storage.Storage;
import chud.task.TaskList;
import chud.ui.Ui;

/**
 * Lists every task currently in the task list. With an optional {@code /sort <key> [asc|desc]}
 * argument, first reorders the task list by that key -- the reordering is permanent (saved to
 * disk), so subsequent mark/unmark/delete task numbers reflect the new order.
 */
public class ListCommand extends Command {
    private final String arguments;

    /** Creates a command that will list tasks, optionally sorting first per the raw arguments. */
    public ListCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Parser.ListSortArgs sortArgs = Parser.parseListSortArgs(arguments);
        if (sortArgs != null) {
            tasks.sort(sortArgs.key.comparator(sortArgs.direction));
            storage.save(tasks);
        }

        ui.showMessage("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showMessage((i + 1) + "." + tasks.get(i));
        }
    }
}
