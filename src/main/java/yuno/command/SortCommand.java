package yuno.command;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that permanently sorts tasks by their chronological times.
 */
public class SortCommand extends Command {
    /**
     * Creates a command with the requested chronological sort order.
     *
     * @param sortDetails Sort order entered by the user, or blank for ascending order.
     */
    public SortCommand(String sortDetails) {
        super(sortDetails);
    }

    /**
     * Sorts, saves, and displays all tasks in the requested chronological order.
     *
     * @param taskList Task list to sort.
     * @param ui User interface used to display the sorted tasks.
     * @param storage Storage used to save the sorted task list.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If the sort order is invalid or the task list cannot be saved.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        TaskList updatedTaskList = taskList.copy();
        if (getCommandArguments().isBlank()) {
            updatedTaskList.sortChronologically(true);
        } else {
            boolean isAscending = parseIsAscending();
            updatedTaskList.sortChronologically(isAscending);
        }
        storage.save(updatedTaskList);
        taskList.replaceWith(updatedTaskList);
        ui.printList(taskList);
        return CommandResult.CONTINUE;
    }

    private boolean parseIsAscending() throws InvalidCommandFormatException {
        String[] sortParts = getCommandArguments().strip().split("\\s+");

        if (sortParts.length != 2 || !sortParts[0].equals("/order")) {
            throw new InvalidCommandFormatException(
                    "'asc' or 'desc'? If you are unsure, go back and think before even calling me.");
        }

        return switch (sortParts[1]) {
            case "desc" -> false;
            case "asc" -> true;
            default -> throw new InvalidCommandFormatException(
                    "'asc' or 'desc'? If you are unsure, go back and think before even calling me.");
        };
    }
}
