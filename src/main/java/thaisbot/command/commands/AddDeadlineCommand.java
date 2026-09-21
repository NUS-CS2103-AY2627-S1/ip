package thaisbot.command.commands;

import java.util.List;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.command.Command;
import thaisbot.command.Parser;
import thaisbot.task.Deadline;
import thaisbot.task.Task;
import thaisbot.task.TaskList;

/**
 * Command to add a deadline task.
 */
public class AddDeadlineCommand extends Command {
    private final String description;
    private final Parser.ParsedDateTime by;
    private final List<String> tags;

    /**
     * Construct an AddDeadlineCommand.
     * @param description description text
     * @param by parsed deadline date/time
     * @param tags tags to attach to the deadline
     */
    public AddDeadlineCommand(String description, Parser.ParsedDateTime by, List<String> tags) {
        this.description = description;
        this.by = by;
        this.tags = List.copyOf(tags);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException {
        Task task = new Deadline(description, by.getValue(), by.hasTime());
        task.addTags(tags);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
