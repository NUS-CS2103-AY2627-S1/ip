package bingbong.command;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import bingbong.exception.BingBongException;
import bingbong.storage.Storage;
import bingbong.task.Task;
import bingbong.task.TaskList;
import bingbong.ui.Ui;

/**
 * Command to search for tasks within the tracker using a text keyword. Filters the active task collection and displays
 * matching entries to the user.
 */
public class FindCommand extends Command {

    private final String in;

    /**
     * Constructs an operational finding command instance.
     *
     * @param in The command line user input.
     */
    public FindCommand(String in) {
        this.in = in;
    }

    /**
     * Loops through the active task collection, checks description boundaries, and prints matching results to the
     * terminal interface.
     *
     * @param tasks   The active TaskList tracking current items.
     * @param ui      The user interface formatting output blocks.
     * @param storage The storage file handler managing disk reads and writes.
     * @throws BingBongException If the search keyword is blank or missing.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingBongException {
        if (in.trim().length() <= 4 || in.substring(4).trim().isEmpty()) {
            throw new BingBongException("Please specify a keyword to find! :(");
        }

        String keyword = in.substring(5).trim();

        List<Task> matchingTasks = IntStream.range(0, tasks.size())
                .mapToObj(tasks::get)
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toList());

        if (matchingTasks.isEmpty()) {
            ui.print("BingBong found no matching tasks with that keyword!");
            return;
        }

        ui.print("BingBong shows the matching tasks in your list:");
        IntStream.range(0, matchingTasks.size())
                .forEach(i -> ui.print((i + 1) + ". " + matchingTasks.get(i)));
    }
}
