package luna.command;

import java.time.LocalDate;

import luna.exception.LunaException;
import luna.parser.Parser;
import luna.storage.Storage;
import luna.task.Event;
import luna.task.Task;
import luna.task.TaskDateFormatter;
import luna.task.TaskList;

/**
 * Adds an event task.
 */
public class AddEventCommand extends AddCommand {
    private final String commandArgs;

    /**
     * Creates a command that adds an event.
     *
     * @param commandArgs User input after the command word.
     * @param tasks Task list to update.
     * @param storage Storage to update.
     */
    public AddEventCommand(String commandArgs, TaskList tasks, Storage storage) {
        super(tasks, storage);
        this.commandArgs = commandArgs;
    }

    @Override
    protected Task createTask() throws LunaException {
        String[] parts = Parser.parseEvent(commandArgs); // [desc, from, to]
        String desc = parts[0];
        String from = parts[1];
        String to = parts[2];

        LocalDate startDate = TaskDateFormatter.parse(from);
        LocalDate endDate = TaskDateFormatter.parse(to);
        if (startDate.isAfter(endDate)) {
            throw new LunaException("The start date must be before the end date");
        }

        return new Event(desc, startDate, endDate);
    }
}
