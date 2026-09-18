package kenbot.task;

import kenbot.KenbotException;
import kenbot.Parser;

/** Represents a task that takes place between a start and end time. */
public class Event extends Task {

    /** Shown whenever the text after {@code event} is missing one of its three parts. */
    private static final String USAGE_MESSAGE =
            "Events need a description, a /from and a /to. Like:\n"
            + "  event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600";

    private final TaskDate from;
    private final TaskDate to;

    /**
     * Creates an event task.
     *
     * @param description the task description
     * @param from when the event starts
     * @param to when the event ends
     */
    public Event(String description, TaskDate from, TaskDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an event from the text typed after the command word.
     *
     * @param argument the description, start and end, separated by {@code /from}
     *                 and {@code /to}
     * @return the new event
     * @throws KenbotException if any of the three parts is missing or blank
     */
    public static Event of(String argument) throws KenbotException {
        // Tags come off before /from and /to are looked for, so neither split
        // below can pick a trailing tag up as part of a date.
        Tag.TaggedText split = Tag.splitTrailingTags(argument);
        Parser.requireAtMostOne(split.text(), " /from ");
        Parser.requireAtMostOne(split.text(), " /to ");
        String[] descriptionAndRest = split.text().split(" /from ", 2);
        if (descriptionAndRest.length != 2) {
            throw new KenbotException(USAGE_MESSAGE);
        }

        String[] startAndEnd = descriptionAndRest[1].split(" /to ", 2);
        if (startAndEnd.length != 2) {
            throw new KenbotException(USAGE_MESSAGE);
        }

        String description = descriptionAndRest[0];
        String start = startAndEnd[0];
        String end = startAndEnd[1];
        if (description.isBlank() || start.isBlank() || end.isBlank()) {
            throw new KenbotException(USAGE_MESSAGE);
        }

        TaskDate startDate = TaskDate.of(start);
        TaskDate endDate = TaskDate.of(end);
        // Stored the wrong way round this would read back as a task that ends
        // before it begins, which no later command could make sense of.
        if (startDate.isAfter(endDate)) {
            throw new KenbotException("An event can't end before it starts."
                    + " You gave " + startDate + " to " + endDate + ".");
        }

        Event event = new Event(description.trim(), startDate, endDate);
        split.tags().forEach(event::addTag);
        return event;
    }

    /**
     * Returns the letter an event is saved under.
     *
     * @return {@code "E"}
     */
    @Override
    protected String getTypeCode() {
        return "E";
    }

    /**
     * Returns the start and end as they are shown on screen.
     *
     * @return {@code (from: ... to: ...)}, with a leading space
     */
    @Override
    protected String getDisplayDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns the start and end as the save file holds them.
     *
     * <p>They are kept as two fields rather than one so that reading the line
     * back does not have to guess where to split them.</p>
     *
     * @return the two fields an event adds after its description
     */
    @Override
    protected String getStorableDetails() {
        // toStorable() on each date rather than the dates themselves: a plain
        // + would use toString(), whose display format cannot be read back in.
        return from.toStorable() + " | " + to.toStorable();
    }
}
