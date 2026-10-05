package gatsby.command;

import java.util.stream.IntStream;

import gatsby.model.TaskList;
import gatsby.ui.Ui;

/**
 * Displays every task currently held by Gatsby.
 */
public class ListCommand extends Command {
    /** Creates the command that displays the current task list. */
    public ListCommand() {
    }

    /**
     * Prints the task list, including the empty-list message when appropriate.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.printLine(" Here are the tasks in your list:");
        if (tasks.isEmpty()) {
            ui.printLine(" There's nothing here yet! Go ahead and add any tasks you'd like! :)");
            return;
        }
        IntStream.range(0, tasks.size())
                .mapToObj(index -> " " + (index + 1) + ". " + tasks.get(index))
                .forEach(ui::printLine);
    }
}
