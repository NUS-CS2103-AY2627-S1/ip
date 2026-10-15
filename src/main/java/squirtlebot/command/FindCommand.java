package squirtlebot.command;

import java.util.stream.IntStream;

import squirtlebot.storage.Storage;
import squirtlebot.task.TaskList;
import squirtlebot.ui.Ui;

/**
 * Represents the find command within {@code SquirtleBot}.
 */
public class FindCommand extends Command {
    private String searchPattern;

    /**
     * Constructs a new FindCommand object using inputs provided by a user.
     *
     * @param inputTokens array containing user inputs required to create a FindCommand object.
     */
    public FindCommand(String[] inputTokens) {
        setAttributes(inputTokens);
    }

    /**
     * Filters the task list for tasks containing the user-supplied string.
     * Displays the filtered tasks to the user.
     *
     * @param tasks list containing tasks created previously by the user.
     * @param ui interface used to display output to the user.
     * @param storage unused storage handler, included to override implementation in {@link Command}.
     */
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        TaskList identifiedTasks = new TaskList(tasks.stream().filter(x -> x.getTaskDescription()
                .contains(searchPattern)).toList());
        if (identifiedTasks.isEmpty()) {
            ui.setSavedMessage("No tasks match your search :(");
            return;
        }
        ui.setSavedMessage(identifiedTasks.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof FindCommand otherFindCommand) {
            return searchPattern.equals(otherFindCommand.searchPattern);
        } else {
            return false;
        }
    }

    /**
     * Extracts string to be searched for within the task list.
     *
     * @param inputTokens array containing user-supplied search string.
     */
    private void setAttributes(String[] inputTokens) {
        this.searchPattern = IntStream.range(1, inputTokens.length).boxed()
                .map(x -> inputTokens[x]).reduce("", (x, y) -> x + y + " ").trim();
    }
}
