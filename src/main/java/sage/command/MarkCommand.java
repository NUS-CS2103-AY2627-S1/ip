package sage.command;

import java.io.IOException;

import sage.exception.SageException;
import sage.model.Task;
import sage.model.TaskList;
import sage.parser.Parser;
import sage.storage.Storage;
import sage.ui.Ui;

/** Command that marks a task as done. */
public class MarkCommand extends Command {
    private final String input;

    /**
     * Creates a mark command from the raw command input.
     *
     * @param input raw mark command input
     */
    public MarkCommand(String input) {
        this.input = input;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SageException {
        int number;
        try {
            number = new Parser().parseTaskNumber(input);
        } catch (NumberFormatException e) {
            throw new SageException("The task number should be a number, like `mark 2`.");
        }
        if (number < 1 || number > tasks.size()) {
            throw new SageException("I couldn't find task " + number
                    + ". Please choose a task number from your list.");
        }
        Task task = tasks.get(number - 1);
        if (task.isDone()) {
            throw new SageException("Task " + number + " is already marked as done.");
        }
        task.markAsDone();
        storage.save(tasks);
        ui.show("Nice! I've marked this task as done:");
        ui.show("  " + task);
    }
}
