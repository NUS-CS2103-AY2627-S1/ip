package chud.task;

import java.time.LocalDate;
import java.util.Comparator;

/** The key to sort by, for the {@code list /sort <key> [asc|desc]} option. */
public enum SortKey {
    /**
     * By date: a Deadline's {@code /by} date, or an Event's {@code /from} (start) date. A Todo
     * has no date and always sorts last, regardless of direction -- only the ordering among
     * dated tasks is reversed for {@code desc}, not where the undated ones land.
     */
    DATE {
        @Override
        public Comparator<Task> comparator(SortDirection direction) {
            Comparator<LocalDate> dateOrder = direction == SortDirection.DESC
                    ? Comparator.reverseOrder()
                    : Comparator.naturalOrder();
            return Comparator.comparing(Task::getSortDate, Comparator.nullsLast(dateOrder));
        }
    },

    /** By description, alphabetically and case-insensitively. */
    DESCRIPTION {
        @Override
        public Comparator<Task> comparator(SortDirection direction) {
            Comparator<Task> byDescription = Comparator.comparing(
                    task -> task.getDescription().toLowerCase());
            return direction == SortDirection.DESC ? byDescription.reversed() : byDescription;
        }
    },

    /** By task type. Ascending order is Todo, Deadline, Event -- TaskType's declared order. */
    TYPE {
        @Override
        public Comparator<Task> comparator(SortDirection direction) {
            Comparator<Task> byType = Comparator.comparing(Task::getTaskType);
            return direction == SortDirection.DESC ? byType.reversed() : byType;
        }
    },

    /** By done status. Ascending order is not-done before done. */
    DONE {
        @Override
        public Comparator<Task> comparator(SortDirection direction) {
            Comparator<Task> byDone = Comparator.comparing(Task::isDone);
            return direction == SortDirection.DESC ? byDone.reversed() : byDone;
        }
    };

    /** Returns the Comparator that orders tasks by this key, in the given direction. */
    public abstract Comparator<Task> comparator(SortDirection direction);

    /**
     * Matches a typed sort key word (exact, lowercase, e.g. "date") to its SortKey, or null if
     * it doesn't match any recognized key.
     */
    public static SortKey fromArgument(String text) {
        for (SortKey key : values()) {
            if (key.name().toLowerCase().equals(text)) {
                return key;
            }
        }
        return null;
    }
}
