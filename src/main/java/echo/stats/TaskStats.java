package echo.stats;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import echo.task.Task;
import echo.task.TaskType;
import echo.utils.DateTimeUtility;

/**
 * A statistical utility class to compute the statistics of a collection of {@link Task}
 */
public class TaskStats {
    private static final String TOTAL_KEY = "total";
    private static final String WEEKLY_KEY_SUFFIX = "-weekly";
    private static final String TASK_STATS_TEMPLATE =
            "Mission progress report:\n\n"
                    + "All missions:\n"
                    + "Total missions: %d\n"
                    + "Completed: %d\n"
                    + "Active: %d\n"
                    + "Todos completed: %d\n"
                    + "Events completed: %d\n"
                    + "Deadlines completed: %d\n\n"
                    + "This week:\n"
                    + "Completed: %d\n"
                    + "Todos completed: %d\n"
                    + "Events completed: %d\n"
                    + "Deadlines completed: %d";

    /**
     * Calculates the types of tasks and their statuses, along with weekly statistics
     * @param tasks The collection of tasks to calculate the statistics.
     * @param currentTime The current {@link LocalDateTime}.
     * @return String representation of task statistics
     */
    public static String getStatsAsString(Collection<? extends Task> tasks, LocalDateTime currentTime) {
        LocalDateTime weekStart = currentTime.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        HashMap<String, Integer> completedCounts = new HashMap<>(Map.of(
                TOTAL_KEY, 0,
                TOTAL_KEY + WEEKLY_KEY_SUFFIX, 0
        ));
        for (TaskType taskType : TaskType.values()) {
            completedCounts.put(taskType.toString(), 0);
            completedCounts.put(taskType + WEEKLY_KEY_SUFFIX, 0);
        }

        tasks.stream().filter(Task::isDone)
                .forEach(task -> TaskStats.incrementCompletionCounts(task,
                        completedCounts,
                        currentTime,
                        weekStart));
        return String.format(TASK_STATS_TEMPLATE,
                tasks.size(),
                completedCounts.get(TOTAL_KEY),
                tasks.size() - completedCounts.get(TOTAL_KEY),
                completedCounts.get(TaskType.TODO.toString()),
                completedCounts.get(TaskType.EVENT.toString()),
                completedCounts.get(TaskType.DEADLINE.toString()),
                completedCounts.get(TOTAL_KEY + WEEKLY_KEY_SUFFIX),
                completedCounts.get(TaskType.TODO + WEEKLY_KEY_SUFFIX),
                completedCounts.get(TaskType.EVENT + WEEKLY_KEY_SUFFIX),
                completedCounts.get(TaskType.DEADLINE + WEEKLY_KEY_SUFFIX));
    }

    private static void incrementCompletionCounts(Task task, Map<String, Integer> completedCounts,
                                                  LocalDateTime currentTime, LocalDateTime weekStart) {
        boolean isCompletedThisWeek = DateTimeUtility.isWithinThisWeek(
                task.getCompletedAt(),
                weekStart,
                currentTime);
        String taskKey = task.getTaskType().toString();
        completedCounts.put(TOTAL_KEY, completedCounts.get(TOTAL_KEY) + 1);
        completedCounts.put(taskKey, completedCounts.get(taskKey) + 1);
        completedCounts.put(TOTAL_KEY + WEEKLY_KEY_SUFFIX, completedCounts.get(TOTAL_KEY + WEEKLY_KEY_SUFFIX)
                + (isCompletedThisWeek ? 1 : 0));
        completedCounts.put(taskKey + WEEKLY_KEY_SUFFIX, completedCounts.get(taskKey + WEEKLY_KEY_SUFFIX)
                + (isCompletedThisWeek ? 1 : 0));
    }

}
