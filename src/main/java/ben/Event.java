package ben;

/** A task that starts and ends at specific date/times. */
class Event extends Task {
    private final String from;
    private final String to;

    /**
     * @throws BenException if {@code from} and {@code to} are the same
     *     text (an event cannot start and end at the same time)
     */
    Event(String description, String from, String to) throws BenException {
        super(description);
        if (from.strip().equalsIgnoreCase(to.strip())) {
            throw new BenException("An event's \"/from\" and \"/to\" cannot be the same.");
        }
        this.from = from;
        this.to = to;
    }

    @Override
    String getTypeIcon() {
        return "E";
    }

    @Override
    String extraFields() {
        return " | " + from + " | " + to;
    }

    @Override
    String extraInfo() {
        return " (from: " + from + " to: " + to + ")";
    }
}
