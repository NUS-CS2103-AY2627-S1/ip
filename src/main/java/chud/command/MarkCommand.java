package chud.command;

import chud.exception.ChudException;
import chud.parser.Parser;
import chud.storage.Storage;
import chud.task.TaskList;
import chud.ui.Ui;

/** Marks the task at a given 1-based list number as done. */
public class MarkCommand extends Command {
    private final String arguments;

    /** Creates a command that will mark the task at the raw (not yet validated) index text. */
    public MarkCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "mark");
        tasks.get(taskIndex).markAsDone();
        storage.save(tasks);
        ui.showMessage("W rizz, secured the bag 🏆:");
        ui.showIndentedMessage(tasks.get(taskIndex));
    }
}
