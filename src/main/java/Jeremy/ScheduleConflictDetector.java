package jeremy;

import java.util.ArrayList;
import java.util.List;

import jeremy.task.Event;
import jeremy.task.Task;

/** Detects schedule collisions among tasks that contain explicit time slots. */
public final class ScheduleConflictDetector {

    private ScheduleConflictDetector() {
        // Utility class; do not instantiate.
    }

    /**
     * Returns existing events that share a start or end time marker with the
     * candidate event. The parser intentionally accepts free-form time text,
     * so matching normalized markers is safer than guessing at date formats.
     */
    public static List<Task> findConflicts(Task candidate, List<Task> existingTasks) {
        assert candidate != null : "Candidate task must not be null";
        assert existingTasks != null : "Existing tasks must not be null";

        List<Task> conflicts = new ArrayList<>();
        if (!(candidate instanceof Event)) {
            return conflicts;
        }

        Event candidateEvent = (Event) candidate;
        for (Task existingTask : existingTasks) {
            assert existingTask != null : "Task list must not contain null tasks";
            if (existingTask instanceof Event
                    && sharesScheduleMarker(candidateEvent, (Event) existingTask)) {
                conflicts.add(existingTask);
            }
        }
        return conflicts;
    }

    private static boolean sharesScheduleMarker(Event first, Event second) {
        String firstFrom = normalize(first.getFrom());
        String firstTo = normalize(first.getTo());
        String secondFrom = normalize(second.getFrom());
        String secondTo = normalize(second.getTo());

        return firstFrom.equals(secondFrom)
                || firstFrom.equals(secondTo)
                || firstTo.equals(secondFrom)
                || firstTo.equals(secondTo);
    }

    private static String normalize(String value) {
        assert value != null : "Schedule marker must not be null";
        return value.trim().toLowerCase();
    }
}
