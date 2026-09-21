package answerme.command;

import java.util.Locale;

import answerme.storage.Storage;
import answerme.task.TaskList;
import answerme.ui.Ui;

/**
 * Represents a command that searches the task list for tasks containing
 * a specified keyword.
 */
public class FindCommand extends Command {
    private final String toFind;

    /**
     * Constructs a new FindCommand using user-supplied input.
     *
     * @param toFind The keyword string that the task should contain.
     */
    public FindCommand(String toFind) {
        this.toFind = toFind;
    }

    /**
     * {@inheritDoc}
     *
     * Finds and displays all tasks that contain the specified search term
     * and ignores case-sensitivity.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        String lowerCaseSearchTerm = toFind.toLowerCase(Locale.ROOT);

        TaskList foundTasks = new TaskList(taskList
                                        .stream()
                                        .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                                                .contains(lowerCaseSearchTerm))
                                        .toList());

        ui.listTasks(foundTasks, "Here are the matching tasks in your list:",
                "No tasks matching \"" + toFind + "\" were found");
    }
}
