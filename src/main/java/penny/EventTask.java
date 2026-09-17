package penny;

import java.time.LocalDateTime;

/**
 * EventTask with a description, a from date and a to date.
 */
public class EventTask extends Task {
    private static final String FROM_KEYWORD = "/from";
    private static final String TO_KEYWORD = "/to";

    protected LocalDateTime from;
    protected LocalDateTime to;

    protected EventTask(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
        this.icon = "E";
    }

    /**
     * Returns EventTask.
     *
     * @param arguments description /from from /to to of EventTask.
     * @return Eventtask.
     * @throws PennyException if /from or /to is missing, or description or from or to is empty.
     */
    public static EventTask create(String arguments) throws PennyException {
        String[] parts = TaskArguments.split(arguments, FROM_KEYWORD, TO_KEYWORD);
        LocalDateTime start = DateTime.parse(parts[1]);
        LocalDateTime end = DateTime.parse(parts[2]);
        if (!end.isAfter(start)) {
            throw new PennyException("Event end must be later than its start.");
        }
        return new EventTask(parts[0], start, end);
    }

    @Override
    public boolean isDueOn(LocalDateTime dateTime) {
        return this.from.toLocalDate().equals(dateTime.toLocalDate());
    }

    @Override
    public String toString() {
        return super.toString()
                + " (from: " + DateTime.format(this.from)
                + " | to: " + DateTime.format(this.to) + ")";
    }
}
