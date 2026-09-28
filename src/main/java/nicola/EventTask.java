package nicola;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * a task that has start date+time and end date+time
 */
public class EventTask extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM dd uuuu HHmm");

    private final LocalDateTime from;
    private final LocalDateTime to;

    EventTask(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    String getType() {
        return "E";
    }

    @Override
    String getDisplayText() {
        return getDescription() + " (from: " + from.format(DISPLAY_DATE_TIME)
                + " to: " + to.format(DISPLAY_DATE_TIME) + ")";
    }

    @Override
    String serialize() {
        return "E | " + (isDone() ? "1" : "0") + " | " + getDescription()
                + " | " + from + " | " + to;
    }
}