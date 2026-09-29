package emma.command;

import java.time.LocalDate;

import emma.Event;
import emma.Task;

/**
 * Adds a task that runs between a start and an end date.
 */
public class AddEventCommand extends AddCommand {

    private final String description;
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates the command.
     *
     * @param description what the event is.
     * @param from the date the event starts.
     * @param to the date the event ends.
     */
    public AddEventCommand(String description, LocalDate from, LocalDate to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    @Override
    protected Task createTask() {
        return new Event(description, from, to);
    }
}
