package bern.logic;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bern.task.Task;
import bern.task.Todo;
import bern.ui.DialogBox;
import bern.ui.ScheduleFrame;
import javafx.scene.Node;

/**
 * Displays tasks as daily timetables, with a separate section for undated tasks.
 */
public class ScheduleResponse extends Response {
    private final List<DaySchedule> schedules;
    private final List<Task> undatedTasks;

    /**
     * Maps tasks to their original one-based command indices; missing entries leave cards unnumbered.
     */
    private final Map<Task, Integer> taskNumbers;

    /**
     * Creates a timetable response using defensive copies of its lists and task-number map.
     * The task objects themselves remain shared with the supplied lists.
     *
     * @param text The introductory message.
     * @param schedules The daily timetables in display order.
     * @param undatedTasks The tasks to display outside the daily timetables.
     * @param taskNumbers The original one-based task numbers, if available.
     */
    private ScheduleResponse(String text, List<DaySchedule> schedules, List<Task> undatedTasks,
            Map<Task, Integer> taskNumbers) {
        this.text = text;
        this.schedules = List.copyOf(schedules);
        this.undatedTasks = List.copyOf(undatedTasks);
        this.taskNumbers = Map.copyOf(taskNumbers);
    }

    /**
     * Returns a response grouping dated tasks by date and displaying todos separately.
     * Task cards are unnumbered; {@link #withTaskNumbers(List)} returns a numbered copy.
     *
     * @param text The introductory message.
     * @param tasks The tasks to display, including undated tasks.
     * @return The timetable response.
     */
    public static ScheduleResponse getScheduleResponse(String text, List<Task> tasks) {
        return new ScheduleResponse(text, DaySchedule.createSchedules(tasks),
                tasks.stream().filter(task -> task instanceof Todo).toList(), Map.of());
    }

    /**
     * Returns a response restricted to the selected day, clipping events at midnight.
     * Undated tasks are omitted, and task cards are initially unnumbered.
     *
     * @param text The introductory message.
     * @param tasks The tasks to consider for the selected day.
     * @param date The day to display.
     * @return The timetable response for that day.
     */
    public static ScheduleResponse getScheduleResponse(String text, List<Task> tasks, LocalDate date) {
        return new ScheduleResponse(text, List.of(new DaySchedule(date, tasks)), List.of(), Map.of());
    }

    /**
     * Returns a copy labeled with the original task numbers used by mark, unmark, and delete.
     * A repeated task receives the number of its first occurrence; tasks absent from the list stay unnumbered.
     *
     * @param taskOrder The complete task list in command-index order, including tasks outside this response.
     * @return A response with stable task numbers even when the timetable reorders tasks by date.
     */
    public ScheduleResponse withTaskNumbers(List<Task> taskOrder) {
        Map<Task, Integer> numbers = new HashMap<>();
        for (int i = 0; i < taskOrder.size(); i++) {
            numbers.putIfAbsent(taskOrder.get(i), i + 1);
        }
        return new ScheduleResponse(text, schedules, undatedTasks, numbers);
    }

    /**
     * Creates the introductory dialog and the timetable display.
     *
     * @return A list containing Bern's message first, followed by the schedule frame.
     */
    @Override
    public List<Node> getResponseNodes() {
        return List.of(
                DialogBox.getBernDialog(text),
                new ScheduleFrame(schedules, undatedTasks, taskNumbers)
        );
    }
}
