package gooble.command;

import java.time.LocalDateTime;

import gooble.GoobleException;
import gooble.task.DeadlineDateParser;
import gooble.task.Event;

/** Handles filtering events by an inclusive date-time range. */
public class ListFromCommand extends Command {
    /** Creates a date-range list command from complete user input. */
    public ListFromCommand(String input) {
        super(input);
    }

    /** Displays events contained in the requested inclusive date-time range. */
    public void execute(CommandContext context) throws GoobleException {
        DeadlineDateParser.DeadlineDate[] range = context.parser().parseDateRange(input);
        // Parser guarantees exactly two range endpoints with usable times.
        assert range.length == 2;
        assert range[0].time() != null && range[1].time() != null;
        LocalDateTime from = LocalDateTime.of(range[0].date(), range[0].time());
        LocalDateTime to = LocalDateTime.of(range[1].date(), range[1].time());

        context.ui().showMessage("Here are the events in your list for that period:");
        int matchingNumber = 1;
        for (int i = 0; i < context.tasks().size(); i++) {
            if (!(context.tasks().get(i) instanceof Event event)) {
                continue;
            }
            if (isWithinRange(event, from, to)) {
                context.ui().showMessage(matchingNumber + "." + event);
                matchingNumber++;
            }
        }
    }

    /** Returns whether an event has valid date-times fully inside the requested range. */
    private boolean isWithinRange(Event event, LocalDateTime from, LocalDateTime to) {
        try {
            DeadlineDateParser.DeadlineDate eventFrom =
                    DeadlineDateParser.parse(event.getStartDate());
            DeadlineDateParser.DeadlineDate eventTo =
                    DeadlineDateParser.parse(event.getEndDate());
            if (eventFrom.time() == null || eventTo.time() == null) {
                return false;
            }
            LocalDateTime start = LocalDateTime.of(eventFrom.date(), eventFrom.time());
            LocalDateTime end = LocalDateTime.of(eventTo.date(), eventTo.time());
            return !start.isBefore(from) && !end.isAfter(to);
        } catch (GoobleException e) {
            // Free-form event dates cannot be range-filtered.
            return false;
        }
    }
}
