package arrodes.command;

import arrodes.storage.Storage;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/** Command that displays the syntax and purpose of every available command. */
public class HelpCommand extends Command {
    /** Complete command guide shown to the user. */
    private static final String HELP_MESSAGE = """
            Arrodes reveals the available commands:

            todo <description>
                Adds a todo task.

            deadline <description> /by <yyyy-MM-dd>
            deadline <description> /by <yyyy-MM-ddTHH:mm>
                Adds a task with a deadline.

            event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>
            event <description> /from <yyyy-MM-ddTHH:mm> /to <yyyy-MM-ddTHH:mm>
                Adds an event between two dates or date-times.

            mark <task number>
                Marks a task as complete.

            unmark <task number>
                Marks a task as incomplete.

            delete <task number>
                Deletes a task.

            list
                Displays all tasks.

            find <keyword>
                Finds tasks containing the keyword.

            upcoming /on <yyyy-MM-dd>
            upcoming /on <yyyy-MM-ddTHH:mm>
                Displays deadlines and events relevant to a date or date-time.

            tag <task number> /with <tag>
                Adds a tag to a task.

            help
                Displays this command guide.

            bye
                Exits Arrodes.
            """;

    /** Creates a help command. */
    public HelpCommand() {
    }

    /** Displays the command guide without changing tasks or storage. */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        ui.showMessage(HELP_MESSAGE);
    }
}
