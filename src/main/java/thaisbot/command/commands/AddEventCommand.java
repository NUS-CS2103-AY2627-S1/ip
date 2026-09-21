package thaisbot.command.commands;

import java.util.List;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.command.Command;
import thaisbot.command.Parser;
import thaisbot.task.Event;
import thaisbot.task.Task;
import thaisbot.task.TaskList;

/**
 * Command to add an event task.
 */
public class AddEventCommand extends Command {
    private final String description;
    private final Parser.ParsedDateTime from;
    private final Parser.ParsedDateTime to;
    private final List<String> tags;

    /**
     * Construct an AddEventCommand.
     * @param description description text
     * @param from parsed start date/time
     * @param to parsed end date/time
     * @param tags tags to attach to the event
     */
    public AddEventCommand(String description, Parser.ParsedDateTime from,
                           Parser.ParsedDateTime to, List<String> tags) {
        this.description = description;
        this.from = from;
        this.to = to;
        this.tags = List.copyOf(tags);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException {
        Task task = new Event(description, from.getValue(), from.hasTime(), to.getValue(), to.hasTime());
        task.addTags(tags);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
