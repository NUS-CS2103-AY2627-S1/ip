package nico;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adds an event task to the task list.
 */
public class EventCommand extends ParameterCommand {
    /**
     * Creates a command with the event details supplied by the user.
     */
    public EventCommand(String details) {
        super("event", details);
    }

    /**
     * Validates the supplied details, adds an event task, and saves it.
     *
     * @param tasks tasks currently managed by the chatbot.
     * @param ui user interface used to display confirmation.
     * @throws NicoException if the description or event times are invalid
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws NicoException {
        String details = getParameter();
        if (details == null) {
            throw NicoException.invalidInput("An event needs a description, start time, and end time.",
                    "event DESCRIPTION /from dd-MM-yyyy HHmm /to dd-MM-yyyy HHmm");
        }
        String[] parts = details.split("\\s*/from\\s*|\\s*/to\\s*", 3);
        if (parts.length < 3) {
            throw NicoException.invalidInput("I could not find both event times.",
                    "event DESCRIPTION /from dd-MM-yyyy HHmm /to dd-MM-yyyy HHmm");
        }
        if (parts[0].trim().isEmpty()) {
            throw NicoException.invalidInput("An event needs a description.",
                    "event DESCRIPTION /from dd-MM-yyyy HHmm /to dd-MM-yyyy HHmm");
        }
        if (parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
            throw NicoException.invalidInput("An event needs both a start time and an end time.",
                    "event DESCRIPTION /from dd-MM-yyyy HHmm /to dd-MM-yyyy HHmm");
        }

        LocalDateTime startTime = Parser.parseDateTime(parts[1].trim(), Nico.DATE_TIME_INPUT_FORMAT);
        LocalDateTime endTime = Parser.parseDateTime(parts[2].trim(), Nico.DATE_TIME_INPUT_FORMAT);
        Event newEvent = new Event(parts[0].trim(), startTime, endTime);
        TaskStorage.writeTask(Nico.FILE_PATH, newEvent);
        tasks.add(newEvent);
        ui.showTaskAdded(newEvent, tasks.size());
    }
}
