package hse.command;

import java.io.IOException;

import hse.parser.Parser;
import hse.storage.Storage;
import hse.task.Event;
import hse.task.Task;
import hse.task.TaskList;
import hse.ui.Ui;

/** Adds an event task described by the user. */
public class EventCommand extends Command {
    private final String details;

    /** Creates an event command from the text after its keyword. */
    public EventCommand(String details) {
        this.details = details;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        String[] fromParts = details.split(" /from ", 2);
        if (fromParts.length != 2) {
            throw new IllegalArgumentException("Please use: event DESCRIPTION /from START /to END.");
        }
        String[] toParts = fromParts[1].split(" /to ", 2);
        if (fromParts[0].isEmpty() || toParts.length != 2 || toParts[0].isEmpty() || toParts[1].isEmpty()) {
            throw new IllegalArgumentException("Please use: event DESCRIPTION /from START /to END.");
        }
        Task task = new Event(fromParts[0], Parser.parseDateTime(toParts[0]), Parser.parseDateTime(toParts[1]));
        tasks.add(task);
        storage.save(tasks);
        ui.showAddedTask(task, tasks.size());
    }
}
