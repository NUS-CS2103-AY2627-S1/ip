package food.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import food.exception.FoodInputException;

/**
 * A task that spans a range of dates, e.g. "event camp /from 2026-09-07 /to 2026-09-09".
 *
 * <p>The only task kind that holds two dates. Both are parsed into {@link LocalDate} so that a bad
 * date is rejected when the task is created rather than when it is later displayed.
 */
public class Event extends Task {
    /** The day the event ends. */
    private final LocalDate to;
    /** The day the event starts. */
    private final LocalDate from;
    /** Printed in front of every Event so the user can tell the task kinds apart at a glance. */
    private static final String SYMBOL = "[E]";

    /**
     * Creates an Event from the line the user typed.
     *
     * @param task the full line, which must look like
     *             "event &lt;description&gt; /from &lt;date&gt; /to &lt;date&gt;".
     * @throws FoodInputException if the line does not match that shape, gives /from or /to twice,
     *                            the description is blank, either date is not a real date in
     *                            yyyy-mm-dd form, or the event ends before it starts.
     */
    public Event(String task) throws FoodInputException {
        rejectRepeated(task, "/from");
        rejectRepeated(task, "/to");
        Pattern p = Pattern.compile("^event (?<name>.+?) /from (?<from>.+?) /to (?<to>.+)$");
        Matcher m = p.matcher(task);
        if (!m.matches()) {
            throw new FoodInputException("That's not how we write a ticket, chef. "
                    + "Try: event <description> /from <date> /to <date>");
        }
        assert m.group("name") != null : "a full match must capture the mandatory \"name\" group";
        super(m.group("name"), task);
        assert m.group("from") != null : "a full match must capture the mandatory \"from\" group";
        assert m.group("to") != null : "a full match must capture the mandatory \"to\" group";
        try {
            this.to = LocalDate.parse(m.group("to"));
            this.from = LocalDate.parse(m.group("from"));
        } catch (DateTimeParseException e) {
            throw new FoodInputException(
                    "Dates go on the ticket as yyyy-mm-dd, chef, e.g. /from 2026-09-07", e);
        }
        // Same-day events are allowed: a one-day event starts and ends on the same date.
        if (this.from.isAfter(this.to)) {
            throw new FoodInputException("An event can't end before it starts, chef.");
        }
    }

    /**
     * Returns the task as the user sees it, e.g.
     * "[E] [X] camp (from: 7/9/2026 to: 9/9/2026)".
     *
     * @return the symbol, the status and description from {@link Task#toString}, and both dates.
     */
    @Override
    public String toString() {
        return String.format("%s %s (from: %d/%d/%d to: %d/%d/%d)",
                                SYMBOL,
                                super.toString(),
                                this.from.getDayOfMonth(),
                                this.from.getMonthValue(),
                                this.from.getYear(),
                                this.to.getDayOfMonth(),
                                this.to.getMonthValue(),
                                this.to.getYear());
    }
}
