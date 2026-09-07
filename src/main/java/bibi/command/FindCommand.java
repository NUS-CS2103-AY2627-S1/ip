package bibi.command;

import java.util.List;
import java.util.stream.IntStream;

import bibi.Storage;
import bibi.Ui;
import bibi.task.Task;
import bibi.task.TaskList;

/**
 * Shows the tasks whose description contains a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that will search for the given keyword.
     *
     * @param keyword the text to look for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shows every task whose description contains the keyword, or reports that
     * none does.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> allTasks = tasks.getTasks();

        // Matches keep the number they have in the full list, as the on command
        // does, so a task found this way can be marked or removed straight away.
        // Streaming the numbers rather than the tasks is what keeps that number
        // available after filtering.
        List<Integer> matchNumbers = IntStream.rangeClosed(1, allTasks.size())
                .filter(taskNumber -> allTasks.get(taskNumber - 1).hasKeyword(keyword))
                .boxed()
                .toList();

        if (matchNumbers.isEmpty()) {
            ui.showMessage("No tasks match '" + keyword + "'.");
            return;
        }

        ui.showMessage("Here are the matching tasks in your list:");
        matchNumbers.forEach(taskNumber ->
                ui.showNumberedTask(taskNumber, allTasks.get(taskNumber - 1)));
    }
}
