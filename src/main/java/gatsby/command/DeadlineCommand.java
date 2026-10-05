package gatsby.command;

import java.time.LocalDateTime;

import gatsby.exception.GatsbyException;
import gatsby.model.Deadline;
import gatsby.model.TaskList;
import gatsby.ui.Ui;

/**
 * Creates and saves a deadline task.
 */
public class DeadlineCommand extends Command {
    /** The description and date entered after the {@code deadline} command. */
    private final String payload;

    /**
     * Creates a deadline command for the supplied payload.
     *
     * @param payload the text entered after {@code deadline}
     */
    public DeadlineCommand(String payload) {
        this.payload = payload == null ? "" : payload.strip();
    }

    /**
     * Validates, parses, creates, saves, and reports the new deadline.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     * @throws GatsbyException when the description or deadline is missing or invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws GatsbyException {
        String[] parts = splitOnKeyword(payload, "/by",
                " son there's no name or deadline for this deadline -_-!");
        String description = requireText(parts[0],
                " son this deadline has no description -_-!");
        String deadline = requireText(parts[1],
                " son this deadline has no date after /by -_-!");
        LocalDateTime date = parseDateTime(deadline, "deadline date and time");
        addTask(tasks, ui, new Deadline(description, date));
    }
}
