package mochi.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import mochi.MochiException;
import mochi.TaskList;
import mochi.task.Deadline;
import mochi.ui.Ui;

/**
 * Adds a deadline task to the task list.
 */
public class AddDeadlineCommand extends Command {
    private final String description;
    private final String byString;

    /**
     * Creates an add-deadline command with the given description and date string.
     *
     * @param description the task description
     * @param byString    the deadline date in yyyy-mm-dd format
     */
    public AddDeadlineCommand(String description, String byString) {
        this.description = description;
        this.byString = byString;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws MochiException {
        if (description.isEmpty()) {
            throw new MochiException("The description of a deadline cannot be empty.");
        }
        LocalDate by;
        try {
            by = LocalDate.parse(byString.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new MochiException("The date must be in yyyy-mm-dd format, e.g., 2019-10-15");
        }
        tasks.add(new Deadline(description, by));
        ui.showAdded(tasks.getTasks().get(tasks.getTasks().size() - 1), tasks.getTasks().size());
    }
}
