package ada;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Compares tasks according to completion status, task type and time.
 *
 * <p>Unfinished tasks come before finished tasks. Within each group,
 * deadlines come before events, and events come before todos.</p>
 */
public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task first, Task second) {
        int completionOrder = Boolean.compare(
                first.isDone(),
                second.isDone());

        if (completionOrder != 0) {
            return completionOrder;
        }

        int typeOrder = Integer.compare(
                getTypeOrder(first),
                getTypeOrder(second));

        if (typeOrder != 0) {
            return typeOrder;
        }

        int timeOrder = compareDateTime(
                getSortDateTime(first),
                getSortDateTime(second));

        if (timeOrder != 0) {
            return timeOrder;
        }

        return first.getDescription()
                .compareToIgnoreCase(second.getDescription());
    }

    private int getTypeOrder(Task task) {
        if (task instanceof Deadline) {
            return 1;
        }

        if (task instanceof Event) {
            return 2;
        }

        if (task instanceof Todo) {
            return 3;
        }

        return 4;
    }

    private LocalDateTime getSortDateTime(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }

        if (task instanceof Event event) {
            return event.getStart();
        }

        return null;
    }

    private int compareDateTime(
            LocalDateTime first,
            LocalDateTime second) {

        if (first == null && second == null) {
            return 0;
        }

        if (first == null) {
            return 1;
        }

        if (second == null) {
            return -1;
        }

        return first.compareTo(second);
    }
}
