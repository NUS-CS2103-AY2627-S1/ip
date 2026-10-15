package command;

import java.time.LocalDateTime;

import exception.CbtException;
import parser.Parser;
import task.Deadline;
import task.TaskList;

/** Adds a deadline task with a parsed date and time. */
public class DeadlineCommand extends AddTaskCommand {
    private final String details;

    /**
     * Creates a command from a deadline description and its {@code /by} argument.
     *
     * @param details user-entered deadline details.
     */
    public DeadlineCommand(String details) {
        this.details = details;
    }

    /**
     * Parses and adds a deadline to the task list.
     *
     * @param tasks task list to update.
     * @return result describing the added deadline.
     * @throws CbtException if the details or deadline date are invalid.
     */
    @Override
    public CommandResult execute(TaskList tasks) throws CbtException {
        int marker = details.indexOf(" /by ");
        int byLength = " /by ".length();
        if (marker <= 0 || marker != details.lastIndexOf(" /by ")
                || details.substring(marker + byLength).isBlank()) {
            throw new CbtException("Use: deadline DESCRIPTION /by DATE_OR_TIME");
        }
        String description = details.substring(0, marker).trim();
        String byString = details.substring(marker + byLength).trim();

        LocalDateTime byDate = Parser.parseLineToDate(byString);
        Deadline task = new Deadline(description, byDate);
        return addTask(tasks, task);
    }
}
