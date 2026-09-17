package quu.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

import quu.exception.InvalidDateException;
import quu.exception.MissingArgumentException;
import quu.exception.QuuException;

/**
 * A task that must be completed by a given date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final String FLAG_BY = "by";
    private static final Set<String> EDIT_FLAGS = Set.of(FLAG_BY);
    private static final String EDIT_USAGE = "edit <task number> [<task>] [/by <yyyy-mm-dd>]";

    private final LocalDate deadline;

    /**
     * Creates a deadline.
     *
     * @param description text describing what the task is
     * @param deadline the due date in ISO form, {@code yyyy-mm-dd}
     * @throws DateTimeParseException if the date is not in ISO form
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = LocalDate.parse(deadline);
    }

    /**
     * Rebuilds a deadline from a line of the save file.
     *
     * <p>The caller must have checked that {@code fields} holds all three parts;
     * {@link quu.storage.Storage#readFile()} rejects a shorter line before calling this.
     *
     * @param fields the save-file line split into type, done flag and payload
     * @return the reconstructed deadline
     * @throws MissingArgumentException if the description or the due date is missing
     * @throws InvalidDateException if the due date cannot be parsed
     */
    public static Deadline fromFileString(String[] fields)
            throws MissingArgumentException, InvalidDateException {
        assert fields.length >= 3 : "Storage checks the field count before rebuilding a task";
        try {
            String[] parts = fields[2].split(" /by ", 2);
            return new Deadline(parts[0], parts[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MissingArgumentException(fields[0] + " <task> /by <yyyy-mm-dd>");
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(e.getParsedString());
        }
    }

    /**
     * Returns a copy of this deadline with the requested edits applied.
     *
     * <p>A due date the edits leave alone is carried over in ISO form, which is the form
     * the constructor reads, so the unedited value makes the same round trip as a value the
     * user typed.
     *
     * @param edits the requested edits, keyed by {@link Task#KEY_DESCRIPTION} or {@code by}
     * @return a new deadline holding the edited details
     * @throws QuuException if an edit names an unsupported flag, asks for a blank
     *     description, or gives a due date that cannot be read
     */
    @Override
    public Deadline withEdits(Map<String, String> edits) throws QuuException {
        requireSupportedEdits(edits, EDIT_FLAGS, EDIT_USAGE);
        String editedDescription = resolveEditedDescription(edits, EDIT_USAGE);
        String editedDeadline = edits.getOrDefault(FLAG_BY, deadline.toString());
        try {
            return new Deadline(editedDescription, editedDeadline);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(e.getParsedString());
        }
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + String.format(" (by: %s)", deadline.format(DISPLAY_FORMAT));
    }

    @Override
    public String toFileString() {
        return "D " + super.toFileString() + String.format(" /by %s", deadline);
    }
}
