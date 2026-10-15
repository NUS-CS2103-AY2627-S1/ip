package luna.command;

import java.time.LocalDate;

import luna.exception.LunaException;
import luna.parser.Parser;
import luna.storage.Storage;
import luna.task.Deadline;
import luna.task.Task;
import luna.task.TaskDateFormatter;
import luna.task.TaskList;

/**
 * Adds a deadline task.
 */
public class AddDeadlineCommand extends AddCommand {
    private final String commandArgs;

    /**
     * Creates a command that adds a deadline.
     *
     * @param commandArgs User input after the command word.
     * @param tasks Task list to update.
     * @param storage Storage to update.
     */
    public AddDeadlineCommand(String commandArgs, TaskList tasks, Storage storage) {
        super(tasks, storage);
        this.commandArgs = commandArgs;
    }

    @Override
    protected Task createTask() throws LunaException {
        String[] parts = Parser.parseDeadline(commandArgs); // [desc, by]
        String desc = parts[0];
        String by = parts[1];

        LocalDate deadlineDate = TaskDateFormatter.parse(by);
        return new Deadline(desc, deadlineDate);
    }
}
