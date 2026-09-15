package duke.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Adds a deadline task.
 *
 * <p>Expected user input format: {@code deadline <description> /by <YYYY-MM-DD>}</p>
 */
public class DeadlineCommand extends Command {
    /** Raw description string provided by the user. */
    private final String description;
    /** Raw due date string provided by the user. */
    private final String dueDate;

    /**
     * Creates a deadline command with the given description and due date strings.
     *
     * @param description Raw description.
     * @param dueDate Raw due date string (expected to be {@code YYYY-MM-DD}).
     */
    public DeadlineCommand(String description, String dueDate) {
        this.description = description;
        this.dueDate = dueDate;
    }

    /**
     * Adds a deadline task to the task list if the description and due date are valid.
     *
     * @param tasks Task list to add into.
     * @param ui UI used to show success/error messages.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        if (description.trim().isEmpty() || dueDate.trim().isEmpty()) {
            ui.showError("""
                    The Deadline description and due date cannot be empty!
                    Use ' /by ' to indicate a deadline!
                    """);
            return;
        }

        try {
            LocalDate deadlineDate = LocalDate.parse(dueDate.trim());
            tasks.addDeadline(description.trim(), deadlineDate);
            ui.showTaskAdded(tasks.getLast(), tasks.size());
        } catch (DateTimeParseException e) {
            ui.showError("Please provide a valid deadline in the format YYYY-MM-DD.");
        }
    }
}
