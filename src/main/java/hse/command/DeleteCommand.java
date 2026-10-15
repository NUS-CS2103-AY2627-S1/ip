package hse.command;

import java.io.IOException;

import hse.parser.Parser;
import hse.storage.Storage;
import hse.task.Task;
import hse.task.TaskList;
import hse.ui.Ui;

/** Removes one task from the list and saves the updated list. */
public class DeleteCommand extends Command {
    private final String commandText;

    /** Creates a delete command from its full input text. */
    public DeleteCommand(String commandText) {
        this.commandText = commandText;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        Task task = tasks.delete(Parser.getIndex(commandText, tasks.size()));
        storage.save(tasks);
        ui.showDeletedTask(task, tasks.size());
    }
}
