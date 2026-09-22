package nico;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows the incomplete events and deadlines with the earliest date-time values.
 */
public class UrgentCommand extends Command {
    /**
     * Displays the incomplete events and deadlines with the earliest date-time values.
     *
     * @param tasks tasks currently managed by the chatbot.
     * @param ui user interface used to display urgent task groups.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) {
        List<Event> closestEvents = findClosestEvents(tasks);
        List<Deadline> closestDeadlines = findClosestDeadlines(tasks);
        ui.showTaskGroup("Event to settle first hor", closestEvents);
        ui.showTaskGroup("Deadline to settle first hor", closestDeadlines);
    }

    /**
     * Finds all incomplete events that share the earliest start time among all saved tasks.
     *
     * @param tasks tasks to examine.
     * @return all incomplete events with the earliest start time
     */
    private List<Event> findClosestEvents(List<Task> tasks) {
        List<Event> closestEvents = new ArrayList<>();
        LocalDateTime closestStartTime = null;
        for (Task task : tasks) {
            if (!(task instanceof Event event) || task.isDone()) {
                continue;
            }

            if (closestStartTime == null || event.getStartTime().isBefore(closestStartTime)) {
                closestEvents.clear();
                closestEvents.add(event);
                closestStartTime = event.getStartTime();
            } else if (event.getStartTime().isEqual(closestStartTime)) {
                closestEvents.add(event);
            }
        }
        LocalDateTime selectedStartTime = closestStartTime;
        assert closestEvents.isEmpty() || closestEvents.stream().allMatch(event ->
                !event.isDone() && event.getStartTime().equals(selectedStartTime))
                : "urgent events must be incomplete and tied for the earliest start time";
        return closestEvents;
    }

    /**
     * Finds all incomplete deadlines that share the earliest due time among all saved tasks.
     *
     * @param tasks tasks to examine.
     * @return all incomplete deadlines with the earliest due time
     */
    private List<Deadline> findClosestDeadlines(List<Task> tasks) {
        List<Deadline> closestDeadlines = new ArrayList<>();
        LocalDateTime closestDueTime = null;
        for (Task task : tasks) {
            if (!(task instanceof Deadline deadline) || task.isDone()) {
                continue;
            }

            if (closestDueTime == null || deadline.getDueTime().isBefore(closestDueTime)) {
                closestDeadlines.clear();
                closestDeadlines.add(deadline);
                closestDueTime = deadline.getDueTime();
            } else if (deadline.getDueTime().isEqual(closestDueTime)) {
                closestDeadlines.add(deadline);
            }
        }
        LocalDateTime selectedDueTime = closestDueTime;
        assert closestDeadlines.isEmpty() || closestDeadlines.stream().allMatch(deadline ->
                !deadline.isDone() && deadline.getDueTime().equals(selectedDueTime))
                : "urgent deadlines must be incomplete and tied for the earliest due time";
        return closestDeadlines;
    }
}
