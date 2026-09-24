package sage.command;

import java.io.IOException;
import java.time.LocalDateTime;

import sage.exception.SageException;
import sage.model.Event;
import sage.model.TaskList;
import sage.parser.Parser;
import sage.storage.Storage;
import sage.ui.Ui;

/** Command that creates and saves an Event task. */
public class AddEventCommand extends Command {
    private final String details;

    /**
     * Creates an Event command for raw event details.
     *
     * @param details the event description and date or date-time range
     */
    public AddEventCommand(String details) {
        this.details = details;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SageException {
        Parser parser = new Parser();
        String[] parts = parser.parseEventDetails(details);
        assert parts.length == 3;
        LocalDateTime from = parser.parseDateTimeOrDate(parts[1], true);
        LocalDateTime to = parser.parseDateTimeOrDate(parts[2], false);
        if (!from.isBefore(to)) {
            throw new SageException("An event's end time must be later than its start time.");
        }
        Event event = new Event(parts[0], from, to);
        if (tasks.containsEquivalent(event)) {
            throw new SageException("This task is already in your list.");
        }
        tasks.add(event);
        storage.save(tasks);
        ui.show("Got it. I've added this task:");
        ui.show("  " + tasks.get(tasks.size() - 1));
        ui.show("Now you have " + tasks.size() + " task"
                + (tasks.size() == 1 ? "" : "s") + " in the list.");
    }
}
