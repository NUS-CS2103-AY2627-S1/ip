package nico;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adds a deadline task to the task list.
 */
public class DeadlineCommand extends ParameterCommand {
    /**
     * Creates a command with the deadline details supplied by the user.
     */
    public DeadlineCommand(String details) {
        super("deadline", details);
    }

    /**
     * Validates the supplied details, adds a deadline task, and saves it.
     *
     * @param tasks tasks currently managed by the chatbot.
     * @param ui user interface used to display confirmation.
     * @throws NicoException if the description or due time is invalid
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws NicoException {
        String details = getParameter();
        if (details == null) {
            throw NicoException.invalidInput("A deadline needs a description and due time.",
                    "deadline DESCRIPTION /by dd-MM-yyyy HHmm");
        }
        String[] parts = details.split("\\s*/by\\s+", 2);
        if (parts.length < 2) {
            throw NicoException.invalidInput("I could not find a due time.",
                    "deadline DESCRIPTION /by dd-MM-yyyy HHmm");
        }
        if (parts[0].trim().isEmpty()) {
            throw NicoException.invalidInput("A deadline needs a description.",
                    "deadline DESCRIPTION /by dd-MM-yyyy HHmm");
        }
        if (parts[1].trim().isEmpty()) {
            throw NicoException.invalidInput("A deadline needs a due time.",
                    "deadline DESCRIPTION /by dd-MM-yyyy HHmm");
        }

        LocalDateTime dueTime = Parser.parseDateTime(parts[1].trim(), Nico.DATE_TIME_INPUT_FORMAT);
        Deadline newDeadline = new Deadline(parts[0].trim(), dueTime);
        TaskStorage.writeTask(Nico.FILE_PATH, newDeadline);
        tasks.add(newDeadline);
        ui.showTaskAdded(newDeadline, tasks.size());
    }
}
