package vector.command;

import vector.storage.Storage;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents a command to display the help message.
 */
public class HelpCommand extends Command {

    /**
     * Executes the command, returning a list of available commands and their syntax.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface for displaying messages.
     * @param storage The storage for saving updates.
     * @return The response string containing the help message.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return "Displaying available system commands:\n"
                + "1. todo <description> - Adds a todo task\n"
                + "2. deadline <description> /by <date/time> - Adds a deadline task\n"
                + "3. event <description> /from <start> /to <end> - Adds an event\n"
                + "4. list - Lists all tasks\n"
                + "5. mark <task_number> - Marks a task as done\n"
                + "6. unmark <task_number> - Marks a task as not done\n"
                + "7. delete <task_number> - Deletes a task\n"
                + "8. find <keyword> - Finds tasks by keyword\n"
                + "9. schedule <date> - Finds tasks occurring on a date\n"
                + "10. help - Shows this help message\n"
                + "11. bye - Exits the application";
    }
}
