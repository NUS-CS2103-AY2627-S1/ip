package sage.command;

import java.io.IOException;

import sage.exception.SageException;
import sage.model.Task;
import sage.model.TaskList;
import sage.parser.Parser;
import sage.storage.Storage;
import sage.ui.Ui;

/** Command that removes and saves a task-list update. */
public class DeleteCommand extends Command {
    private final String input;

    /**
     * Creates a delete command from the raw command input.
     *
     * @param input raw delete command input
     */
    public DeleteCommand(String input) {
        this.input = input;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SageException {
        int number;
        try {
            number = new Parser().parseTaskNumber(input);
        } catch (NumberFormatException e) {
            throw new SageException("The task number should be a number, like `delete 2`.");
        }
        if (number < 1 || number > tasks.size()) {
            throw new SageException("I couldn't find task " + number
                    + ". Please choose a task number from your list.");
        }
        Task deleted = tasks.remove(number - 1);
        storage.save(tasks);
        ui.show("Noted. I've removed this task:");
        ui.show("  " + deleted);
        ui.show("Now you have " + tasks.size() + " tasks in the list.");
    }
}
