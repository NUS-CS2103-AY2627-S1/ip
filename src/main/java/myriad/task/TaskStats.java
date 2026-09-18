package myriad.task;

import java.util.List;

/**
 * The data behind one {@code stats} report: the tasks of each type, the
 * tasks due soon together with how many days "soon" means, and the oldest
 * tasks not yet done. {@code StatsCommand} gathers it and {@code Ui} formats
 * it. Carrying the day count here, rather than writing it into
 * {@code Ui}'s text, keeps that number defined in one place: whoever picks
 * the window also tells the report what it was.
 *
 * @param toDos        every {@link ToDo} in the list.
 * @param deadlines    every {@link Deadline} in the list.
 * @param events       every {@link Event} in the list.
 * @param dueSoonDays  how many days ahead of today count as "due soon".
 * @param dueSoon      the tasks due within that many days.
 * @param oldestUndone the oldest not-done tasks, in list order.
 */
public record TaskStats(List<Task> toDos, List<Task> deadlines, List<Task> events,
        int dueSoonDays, List<Task> dueSoon, List<Task> oldestUndone) {

    /**
     * Returns how many tasks there are of all types combined.
     *
     * @return the number of ToDos, Deadlines and Events together.
     */
    public int getTotalCount() {
        return toDos.size() + deadlines.size() + events.size();
    }
}
