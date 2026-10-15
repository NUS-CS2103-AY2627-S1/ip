package sanyueqi.ui;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

class Event extends Task {
    private String startTime;
    private String endTime;

    public Event(String desc) {
        super(false, desc);
    }

    public Event(boolean done, String desc, String from, String to) {
        super(done, desc);
        this.startTime = from;
        this.endTime = to;
    }

    @Override
    public String toString() {
        return String.format("[E][%s] %s (from: %s to: %s)", super.isDone() ? "X" : " ", super.getDesc(), this.startTime, this.endTime);
    }

    /**
     * Serializes an event for writing to a CSV.
     *
     * @return The serialized string.
     */
    @Override
    public String toCsv() {
        return String.format("E,%s,%s,%s,%s", super.isDone() ? "1" : "0", super.serialise(super.getDesc()), super.serialise(this.startTime), super.serialise(this.endTime));
    }

    /**
     * Makes an 'event' type Task.
     *
     * @param parts The parts of the event.
     * @return An event instance.
     */
    public static Event makeEvent(String[] parts) throws SYQException {
        StringBuilder desc = new StringBuilder();
        int i = 1;
        int foundIndex = -1;
        while (i < parts.length) {
            if (parts[i].equals("/to")) {
                if (foundIndex == -1) {
                    throw new SYQException("Sorry! End time must be indicated after start time!");
                } else if (i == foundIndex + 1) {
                    throw new SYQException("Sorry! Starting time cannot be empty!");
                } else if (i == parts.length - 1) {
                    throw new SYQException("Sorry! Ending time cannot be empty!");
                }
                Event event = new Event(desc.toString());
                StringBuilder from = new StringBuilder();
                for (int j = foundIndex + 1; j < i; j++) {
                    from.append(parts[j]);
                    if (j < i - 1) from.append(" ");
                }
                StringBuilder to = new StringBuilder();
                for (int k = i + 1; k < parts.length; k++) {
                    to.append(parts[k]);
                    if (k < parts.length - 1) to.append(" ");
                }
                String formattedFrom;
                String formattedTo;
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                try {
                    formattedFrom = LocalDate.parse(from.toString(), inputFormatter)
                            .format(outputFormatter);
                } catch (DateTimeParseException e) {
                    formattedFrom = from.toString();
                }
                try {
                    formattedTo = LocalDate.parse(to.toString(), inputFormatter)
                            .format(outputFormatter);
                } catch (DateTimeParseException e) {
                    formattedTo = to.toString();
                }
                event.startTime = formattedFrom;
                event.endTime = formattedTo;
                return event;
            }
            else if (parts[i].equals("/from") && foundIndex == -1) {
                if (!desc.isEmpty()) {
                    desc.deleteCharAt(desc.length() - 1);
                    foundIndex = i;
                } else {
                    throw new SYQException("Sorry! Description cannot be empty!");
                }
            } else if (foundIndex == -1) {
                desc.append(parts[i]);
                desc.append(" ");
            }
            i += 1;
        }
        if (i == parts.length) {
            throw new SYQException("Sorry! You didn't indicate either the start or end time!");
        }
        assert false;
        return null;
    }
}