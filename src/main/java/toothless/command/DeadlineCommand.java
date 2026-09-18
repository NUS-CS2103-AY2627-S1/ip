package toothless.command;

import java.time.LocalDate;

import toothless.task.Deadline;
import toothless.task.Task;

/**
 * Coordinates creating a deadline and persisting the updated task list.
 */
public class DeadlineCommand extends AddCommand {
    private final String description;
    private final LocalDate by;

    /**
     * Creates a deadline command from validated task details.
     *
     * @param description deadline description obtained from the parser.
     * @param by date obtained from the parser.
     */
    public DeadlineCommand(String description, LocalDate by) {
        this.description = description;
        this.by = by;
    }

    /**
     * Creates the deadline handled by the shared add workflow.
     *
     * @return deadline created from the parsed description and date
     */
    @Override
    protected Task createTask() {
        return new Deadline(description, by);
    }
}
