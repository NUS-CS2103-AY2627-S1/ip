package squirtlebot.task;

import java.util.ArrayList;

import squirtlebot.TemporalPair;
import squirtlebot.exception.CommandException;

/**
 * Represents the event task that users can add to their list of tasks.
 * Contains a task description, and possible start and end dates.
 */
public class Event extends Task {
    private ArrayList<TemporalPair> possibleSchedules;
    private boolean isDateConfirmed;


    /**
     * Constructs a new Event object based on user-provided inputs.
     *
     * @param taskDescription a description of the event.
     * @param possibleSchedules a list containing pairs of possible start/end dates for the event.
     */
    public Event(String taskDescription, ArrayList<TemporalPair> possibleSchedules) {
        super(taskDescription);
        this.possibleSchedules = possibleSchedules;
        if (possibleSchedules.size() > 1) {
            isDateConfirmed = false;
        } else if (possibleSchedules.size() == 1) {
            isDateConfirmed = true;
        }
    }

    /**
     * Confirms the date of an unconfirmed event.
     * If an event already has a confirmed date, no operations are performed.
     *
     * @param index zero-based integer indicating the start/end date
     *              in the list of possible start/end dates to set as the confirmed date.
     * @throws CommandException if {@code index} is out of bounds for list of possible schedules.
     */
    public void confirmEventDate(int index) {
        if (isDateConfirmed) {
            return;
        }
        try {
            TemporalPair confirmedDate = possibleSchedules.get(index);
            possibleSchedules = new ArrayList<>();
            possibleSchedules.add(confirmedDate);

            isDateConfirmed = true;
        } catch (IndexOutOfBoundsException e) {
            throw new CommandException("Invalid index entered for confirmed date :(", e);
        }

        assert possibleSchedules.size() == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof Event otherEvent) {
            boolean arePossibleSchedulesEqual = possibleSchedules.equals(otherEvent.possibleSchedules);
            boolean areConfirmedDatesEqual = isDateConfirmed == otherEvent.isDateConfirmed;
            boolean areTaskAttributesEqual = super.equals(otherEvent);

            return arePossibleSchedulesEqual && areConfirmedDatesEqual && areTaskAttributesEqual;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (isDateConfirmed) {
            TemporalPair confirmedDate = possibleSchedules.get(0);
            return String.format("[E] %s (from: %s to: %s)", super.toString(),
                    confirmedDate.startDate(), confirmedDate.endDate());
        }
        String formattedSchedules = possibleSchedules.stream()
                .map(x -> String.format("from: %s to: %s", x.startDate(), x.endDate()))
                .reduce("", (x, y) -> x.isEmpty() ? x + "\t\t" + y : x + "\n\t\t" + y);
        return String.format("[E] %s \n\tPossible Schedules: \n%s", super.toString(), formattedSchedules);
    }
}
