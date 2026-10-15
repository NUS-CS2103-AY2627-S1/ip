package hse.command;

import java.io.IOException;

import hse.parser.Parser;
import hse.storage.Storage;
import hse.task.Deadline;
import hse.task.Task;
import hse.task.TaskList;
import hse.ui.Ui;

/** Adds a deadline task described by the user. */
public class DeadlineCommand extends Command {
    private final String details;

    /** Creates a deadline command from the text after its keyword. */
    public DeadlineCommand(String details) {
        this.details = details;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        String[] parts = details.split(" /by ", 2);
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new IllegalArgumentException("Please use: deadline DESCRIPTION /by DATE.");
        }
        Task task = new Deadline(parts[0], Parser.parseDateTime(parts[1]));
        tasks.add(task);
        storage.save(tasks);
        ui.showAddedTask(task, tasks.size());
    }
}
