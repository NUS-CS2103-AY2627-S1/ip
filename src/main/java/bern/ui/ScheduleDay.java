package bern.ui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import bern.logic.DaySchedule;
import bern.task.Deadline;
import bern.task.Event;
import bern.task.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Displays a day's heading, tasks without a duration, and timetable using an FXML layout.
 */
public class ScheduleDay extends VBox {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, d MMM yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private Label dateHeading;
    @FXML
    private Label otherTasksHeading;
    @FXML
    private Label timetableHeading;
    @FXML
    private Label emptyDay;

    /**
     * Creates a day's FXML layout and fills in its date and task sections.
     *
     * @param schedule The day's tasks and their timetable positions.
     * @param taskNumbers The original one-based task numbers; absent entries produce unnumbered cards.
     * @throws IllegalStateException If a required schedule layout cannot be found or loaded.
     */
    public ScheduleDay(DaySchedule schedule, Map<Task, Integer> taskNumbers) {
        ScheduleViewLoader.load(this, "/view/ScheduleDay.fxml");

        setUserData(schedule.getDate());
        dateHeading.setText(schedule.getDate().format(DATE_FORMATTER));
        addUntimedTasks(schedule.getOtherTasks(), taskNumbers);
        if (!schedule.getEntries().isEmpty()) {
            getChildren().add(timetableHeading);
            getChildren().add(new ScheduleTimetable(schedule, taskNumbers));
        } else if (schedule.getOtherTasks().isEmpty()) {
            getChildren().add(emptyDay);
        }
    }

    /**
     * Adds a heading and cards when the day contains tasks outside the timetable.
     *
     * @param tasks The tasks to display outside the timetable, in display order.
     * @param taskNumbers The original one-based task numbers; absent entries produce unnumbered cards.
     * @throws IllegalStateException If a task card layout cannot be found or loaded.
     */
    private void addUntimedTasks(List<Task> tasks, Map<Task, Integer> taskNumbers) {
        if (tasks.isEmpty()) {
            return;
        }
        getChildren().add(otherTasksHeading);
        for (Task task : tasks) {
            getChildren().add(new ScheduleTaskCard(task, getTaskTiming(task), taskNumbers.get(task)));
        }
    }

    /**
     * Returns a timing description for tasks whose duration is unspecified or zero.
     *
     * @param task The task to describe.
     * @return The due time, event timing, or a message indicating no time was specified.
     */
    private String getTaskTiming(Task task) {
        if (task instanceof Deadline deadline) {
            LocalTime dueTime = deadline.getReferenceDateTime().getTime();
            return dueTime == null ? "Due today" : "Due " + dueTime.format(TIME_FORMATTER);
        }
        if (task instanceof Event event) {
            if (event.isAllDay()) {
                return "All day";
            }
            return event.getStartDateTimeString() + " - " + event.getEndDateTimeString();
        }
        return "No time specified";
    }
}
