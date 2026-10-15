package nutrisoy.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Deadline;
import nutrisoy.task.Task;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/**
 * Adds a deadline task from user-provided command arguments.
 */
public class DeadlineCommand extends Command {
    private final String arguments;

    /**
     * Creates a command with the arguments describing the deadline.
     *
     * @param arguments deadline description and due-date information
     */
    public DeadlineCommand(String arguments) {
        assert arguments != null : "Deadline arguments must not be null";
        this.arguments = arguments;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        assert tasks != null && ui != null : "Command collaborators must not be null";
        if (arguments.isEmpty()) {
            throw new DukeException("The description of a deadline cannot be empty. "
                    + "Use: deadline [description] /by [yyyy-MM-dd]");
        }
        String usage = "deadline [description] /by [yyyy-MM-dd]";
        int byIndex = findSingleParameter(arguments, "/by", usage);
        String description = validateDescription(arguments.substring(0, byIndex), "deadline");
        String byString = arguments.substring(byIndex + 3).trim();
        if (byString.isEmpty()) {
            throw new DukeException("The deadline date cannot be empty.");
        }

        try {
            LocalDate byDate = LocalDate.parse(byString);
            Task newDeadline = new Deadline(description, byDate);
            addUniqueTask(newDeadline, tasks, ui);
        } catch (DateTimeParseException e) {
            throw new DukeException("Please provide the deadline date in yyyy-MM-dd format (e.g., 2019-12-02).");
        }
    }
}
