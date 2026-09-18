package chud.command;

import chud.exception.ChudException;
import chud.parser.Parser;
import chud.storage.Storage;
import chud.task.Deadline;
import chud.task.TaskList;
import chud.ui.Ui;

/** Adds a Deadline task with the given (not yet validated) "description /by ..." text. */
public class AddDeadlineCommand extends Command {
    private final String arguments;

    /** Creates a command that will parse and add a deadline from the given raw argument text. */
    public AddDeadlineCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Parser.DeadlineArgs deadlineArgs = Parser.parseDeadlineArgs(arguments);
        addTask(tasks, ui, storage, new Deadline(deadlineArgs.description, deadlineArgs.by));
    }
}
