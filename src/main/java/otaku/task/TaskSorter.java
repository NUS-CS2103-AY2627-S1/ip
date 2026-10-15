package otaku.task;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/** Sorts tasks into useful display orders. */
public final class TaskSorter {
    private static final LocalDate UNDATED_TASK_DATE = LocalDate.MAX;

    private TaskSorter() {
    }

    /** Sorts dated tasks chronologically and places undated tasks last. */
    public static void sortByDate(List<Task> tasks) {
        tasks.sort(Comparator.comparing(TaskSorter::getSortDate));
    }

    /** Returns the date used to order a task. */
    private static LocalDate getSortDate(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        if (task instanceof Event event) {
            return event.getFrom();
        }
        return UNDATED_TASK_DATE;
    }
}
