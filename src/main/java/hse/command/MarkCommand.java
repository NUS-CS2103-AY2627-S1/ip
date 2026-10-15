package hse.command;

import java.io.IOException;

import hse.parser.Parser;
import hse.storage.Storage;
import hse.task.Task;
import hse.task.TaskList;
import hse.ui.Ui;

/** Marks one task as complete and saves the updated list. */
public class MarkCommand extends Command {
    private final String commandText;

    /** Creates a mark command from its full input text. */
    public MarkCommand(String commandText) {
        this.commandText = commandText;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        Task task = tasks.mark(Parser.getIndex(commandText, tasks.size()));
        storage.save(tasks);
        ui.showMarkedTask(task);
    }
}
