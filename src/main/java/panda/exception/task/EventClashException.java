package panda.exception.task;

import java.util.List;

import panda.exception.ApplicationException;
import panda.task.Event;
import panda.task.Task;

/**
 * Signals that a proposed event overlaps events in the current task list.
 */
public class EventClashException extends ApplicationException {
    private final List<Event> conflictingEvents;

    /**
     * Creates an exception that lists every conflicting event and its task number.
     *
     * @param tasks Current tasks in displayed order.
     * @param conflictingEvents Conflicting events in chronological order.
     */
    public EventClashException(List<Task> tasks, List<Event> conflictingEvents) {
        super(createMessage(tasks, conflictingEvents));
        this.conflictingEvents = List.copyOf(conflictingEvents);
    }

    /**
     * Returns the events that conflict with the proposed event.
     *
     * @return Conflicting events in chronological order.
     */
    public List<Event> getConflictingEvents() {
        return conflictingEvents;
    }

    private static String createMessage(List<Task> tasks, List<Event> conflictingEvents) {
        assert tasks != null : "Task list used to describe event clashes must not be null";
        assert conflictingEvents != null && !conflictingEvents.isEmpty()
                : "At least one conflicting event is required";

        StringBuilder message = new StringBuilder(
                "Unless your kungfu includes cloning, this event clashes with:");
        for (Event event : conflictingEvents) {
            int taskNumber = findTaskNumber(tasks, event);
            assert taskNumber > 0 : "Conflicting event must exist in the task list";
            message.append(System.lineSeparator())
                    .append(taskNumber)
                    .append(".")
                    .append(event);
        }
        return message.append(System.lineSeparator())
                .append("Pick another time. Yes, schedules have rules too.")
                .toString();
    }

    private static int findTaskNumber(List<Task> tasks, Event event) {
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index) == event) {
                return index + 1;
            }
        }
        return -1;
    }
}
