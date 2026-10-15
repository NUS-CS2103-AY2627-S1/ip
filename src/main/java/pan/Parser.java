package pan;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Makes sense of the raw text a user types: the command word, its arguments,
 * and any date/time inside those arguments.
 *
 * <p>Every method is {@code static} because a parser holds no state - it is
 * just a collection of pure text-to-value helpers. The same helpers are used
 * by {@link Pan} (for live user input) and {@link Storage} (for reloading the
 * save file), so keeping them in one place avoids duplicating the logic.
 */
public class Parser {

    /** The date/time format a user is expected to type, e.g. {@code 2019-10-15 1800}. */
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /** The friendlier format shown back to the user, e.g. {@code Oct 15 2019, 6:00PM}. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);

    // ---------- command text ----------

    /**
     * Returns the first whitespace-delimited word of the input, e.g.
     * {@code "deadline"} for {@code "deadline return book /by ..."}.
     */
    public static String commandWord(String input) {
        String trimmed = input.trim();
        int space = trimmed.indexOf(' ');
        return space == -1 ? trimmed : trimmed.substring(0, space);
    }

    /**
     * Returns everything after the first word, trimmed, or an empty string
     * if the input is only a single word.
     */
    public static String arguments(String input) {
        String trimmed = input.trim();
        int space = trimmed.indexOf(' ');
        return space == -1 ? "" : trimmed.substring(space + 1).trim();
    }

    /**
     * One {@code /option value} pair from an {@code update} command, for
     * example {@code /to 2019-10-15 1900}.
     *
     * <p>A {@code record} rather than a normal class because this is pure data
     * with no behaviour - Java generates the constructor, the {@code name()}
     * and {@code value()} accessors, {@code equals} and {@code toString} for
     * us. Written out as a class with two final fields and two getters it
     * would say the same thing in fifteen more lines.
     *
     * @param name  the option word without its leading slash, e.g. {@code "to"}.
     * @param value the text after the option word, e.g. {@code "2019-10-15 1900"}.
     */
    public record UpdateOption(String name, String value) {}

    /**
     * Splits the "what to change" half of an {@code update} command into the
     * option being changed and its new value.
     *
     * <p>Exactly one option is allowed per command. Without that rule
     * {@code update 2 /desc lunch /to 2019-10-15 1900} would quietly set the
     * description to the whole of {@code "lunch /to 2019-10-15 1900"}.
     *
     * @param args text after the task number, of the form {@code /OPTION NEW_VALUE}.
     * @return the option name (without its slash) and the new value.
     * @throws PanException if the text names no option, gives no value, or
     *     names more than one option.
     */
    public static UpdateOption parseUpdateOption(String args) throws PanException {
        String trimmed = args.trim();
        if (!trimmed.startsWith("/")) {
            throw new PanException(" Urmm... PanPan needs to know WHICH bit to change~ "
                    + "like \"update 2 /by 2019-10-15 1800\"!");
        }

        String withoutSlash = trimmed.substring(1);
        // Folded to lower case so /DESC and /By work like /desc and /by,
        // matching the way the command word itself is treated.
        String name = commandWord(withoutSlash).toLowerCase(Locale.ROOT);
        String value = arguments(withoutSlash);
        if (value.isEmpty()) {
            throw new PanException(" Ooh? PanPan sees \"/" + name
                    + "\" but nothing after it~ change it to what?? (・_・?)");
        }
        // A slash that starts a new word means a second option was given. A
        // slash inside a word (as in "read a/b") is left alone.
        if (value.contains(" /")) {
            throw new PanException(" Ooh, one change at a time please~ "
                    + "PanPan can only fix one thing per update!");
        }
        return new UpdateOption(name, value);
    }

    // ---------- task builders ----------

    /**
     * Builds a {@link Deadline} from the arguments of a {@code deadline}
     * command (everything after the word {@code deadline}).
     *
     * @param args text of the form {@code DESCRIPTION /by yyyy-MM-dd HHmm}.
     * @throws PanException if the description or {@code /by} date is missing,
     *     or the date cannot be understood.
     */
    public static Deadline parseDeadline(String args) throws PanException {
        String[] parts = args.split("/by", 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new PanException(" Ehhh? PanPan is holding an empty deadline~ what should PanPan write on it?");
        }
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new PanException(" Urmm... A deadline needs a /by date, PanPan can't guess it for you~");
        }
        LocalDateTime by = parseDateTime(parts[1].trim());
        return new Deadline(description, by);
    }

    /**
     * Builds an {@link Event} from the arguments of an {@code event} command.
     *
     * @param args text of the form
     *     {@code DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm}.
     * @throws PanException if the description, {@code /from} or {@code /to} is
     *     missing, a date cannot be understood, or the event would end before
     *     it starts.
     */
    public static Event parseEvent(String args) throws PanException {
        String[] parts = args.split("/from", 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new PanException(" Hehe, PanPan can't make an event with no name~ what should PanPan call it?");
        }
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new PanException(" Ooopsie, PanPan needs a /from time "
                    + "before it can pencil in your event~ (＞人＜)");
        }
        String[] fromToParts = parts[1].trim().split("/to", 2);
        if (fromToParts.length < 2 || fromToParts[0].trim().isEmpty() || fromToParts[1].trim().isEmpty()) {
            throw new PanException(" Almost! PanPan still needs a /to time to finish off your event~");
        }
        LocalDateTime from = parseDateTime(fromToParts[0].trim());
        LocalDateTime to = parseDateTime(fromToParts[1].trim());
        Event.requireInOrder(from, to);
        return new Event(description, from, to);
    }

    // ---------- date/time text ----------

    /**
     * Parses date/time text typed by the user (format {@code yyyy-MM-dd HHmm},
     * e.g. {@code 2019-10-15 1800}) into a {@link LocalDateTime}.
     *
     * @throws PanException with a friendly hint if the text is not a valid
     *     date/time in the expected format.
     */
    public static LocalDateTime parseDateTime(String raw) throws PanException {
        try {
            return LocalDateTime.parse(raw.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new PanException(" Urmm, PanPan don't know how to read that date~ "
                    + "write it like: 2019-10-15 1800  (yyyy-MM-dd HHmm)!");
        }
    }

    /** Formats a stored {@link LocalDateTime} for display back to the user. */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMAT);
    }

    /**
     * Parses the ISO-8601 text written by {@code toFileString()} (for example
     * {@code 2019-10-15T18:00}) back into a {@link LocalDateTime}.
     *
     * @throws DateTimeParseException if the text is not valid ISO-8601;
     *     {@link Storage} treats that as a corrupt save line and skips it.
     */
    public static LocalDateTime parseStoredDateTime(String iso) {
        return LocalDateTime.parse(iso.trim());
    }
}
