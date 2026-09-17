package quu.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

import quu.exception.InvalidDateException;
import quu.exception.InvalidDurationException;
import quu.exception.MissingArgumentException;
import quu.exception.QuuException;

/**
 * A task that spans a period between two dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final String FLAG_FROM = "from";
    private static final String FLAG_TO = "to";
    private static final Set<String> EDIT_FLAGS = Set.of(FLAG_FROM, FLAG_TO);
    private static final String EDIT_USAGE =
            "edit <task number> [<task>] [/from <yyyy-mm-dd>] [/to <yyyy-mm-dd>]";

    private final LocalDate eventStart;
    private final LocalDate eventEnd;

    /**
     * Creates an event.
     *
     * @param description text describing what the task is
     * @param eventStart the start date in ISO form, {@code yyyy-mm-dd}
     * @param eventEnd the end date in ISO form, {@code yyyy-mm-dd}
     * @throws InvalidDurationException if the end date falls before the start date
     * @throws DateTimeParseException if either date is not in ISO form
     */
    public Event(String description, String eventStart, String eventEnd) throws InvalidDurationException {
        super(description);
        this.eventStart = LocalDate.parse(eventStart);
        this.eventEnd = LocalDate.parse(eventEnd);
        if (this.eventEnd.isBefore(this.eventStart)) {
            throw new InvalidDurationException(this.eventStart, this.eventEnd);
        }
    }

    /**
     * Rebuilds an event from a line of the save file.
     *
     * <p>The caller must have checked that {@code fields} holds all three parts;
     * {@link quu.storage.Storage#readFile()} rejects a shorter line before calling this.
     *
     * @param fields the save-file line split into type, done flag and payload
     * @return the reconstructed event
     * @throws MissingArgumentException if the description or either date is missing
     * @throws InvalidDateException if a date cannot be parsed
     * @throws InvalidDurationException if the end date falls before the start date
     */
    public static Event fromFileString(String[] fields)
            throws InvalidDurationException, InvalidDateException, MissingArgumentException {
        assert fields.length >= 3 : "Storage checks the field count before rebuilding a task";
        try {
            String[] descriptionAndDates = fields[2].split(" /from ", 2);
            String[] dates = descriptionAndDates[1].split(" /to ", 2);
            return new Event(descriptionAndDates[0], dates[0], dates[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MissingArgumentException(fields[0] + " <task> /from <yyyy-mm-dd> /to <yyyy-mm-dd>");
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(e.getParsedString());
        }
    }

    /**
     * Returns a copy of this event with the requested edits applied.
     *
     * <p>Both dates go through the constructor together, so moving one end of the event
     * still checks the pair: an edit that would leave the event ending before it starts is
     * rejected, whichever end the user moved.
     *
     * @param edits the requested edits, keyed by {@link Task#KEY_DESCRIPTION},
     *     {@code from} or {@code to}
     * @return a new event holding the edited details
     * @throws QuuException if an edit names an unsupported flag, asks for a blank
     *     description, gives a date that cannot be read, or ends the event before it starts
     */
    @Override
    public Event withEdits(Map<String, String> edits) throws QuuException {
        requireSupportedEdits(edits, EDIT_FLAGS, EDIT_USAGE);
        String editedDescription = resolveEditedDescription(edits, EDIT_USAGE);
        String editedStart = edits.getOrDefault(FLAG_FROM, eventStart.toString());
        String editedEnd = edits.getOrDefault(FLAG_TO, eventEnd.toString());
        try {
            return new Event(editedDescription, editedStart, editedEnd);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(e.getParsedString());
        }
    }

    @Override
    public String toString() {
        assert !eventEnd.isBefore(eventStart) : "the constructor rejects an end date before the start date";
        return "[E]" + super.toString() + String.format(" (from: %s to: %s)",
                eventStart.format(DISPLAY_FORMAT),
                eventEnd.format(DISPLAY_FORMAT));
    }

    @Override
    public String toFileString() {
        return "E " + super.toFileString() + String.format(" /from %s /to %s", eventStart, eventEnd);
    }
}
