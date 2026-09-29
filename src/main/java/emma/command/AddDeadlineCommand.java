package emma.command;

import java.time.LocalDate;

import emma.Deadline;
import emma.Task;

/**
 * Adds a task that must be finished by a given date.
 */
public class AddDeadlineCommand extends AddCommand {

    private final String description;
    private final LocalDate by;

    /**
     * Creates the command.
     *
     * @param description what the task is.
     * @param by the date the task is due.
     */
    public AddDeadlineCommand(String description, LocalDate by) {
        this.description = description;
        this.by = by;
    }

    @Override
    protected Task createTask() {
        return new Deadline(description, by);
    }
}
