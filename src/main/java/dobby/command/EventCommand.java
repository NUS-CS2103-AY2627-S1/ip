package dobby.command;

import dobby.logic.DobbyLogic;
import dobby.util.DateTimeUtil;

/**
 * Adds a new event task with start and end dates and times.
 */
public final class EventCommand extends Command {
    /** Description supplied for the event. */
    private final String description;
    /** Parsed start date and optional time. */
    private final DateTimeUtil.ParsedDateTime from;
    /** Parsed end date and optional time. */
    private final DateTimeUtil.ParsedDateTime to;
    /**
     * Creates a command with event details.
     *
     * @param description event description
     * @param from parsed start date and optional time
     * @param to parsed end date and optional time
     */
    public EventCommand(String description, DateTimeUtil.ParsedDateTime from, DateTimeUtil.ParsedDateTime to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /** Adds the event task. */
    @Override
    public String execute(DobbyLogic logic) {
        return logic.createEvent(description, from, to);
    }
}
