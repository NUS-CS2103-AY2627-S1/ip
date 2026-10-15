package bern.ui;

import java.util.List;
import java.util.Map;

import bern.logic.DaySchedule;
import bern.task.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Assembles daily and undated schedule sections inside a scrollable FXML layout.
 */
public class ScheduleFrame extends VBox {
    @FXML
    private VBox dayContainer;
    @FXML
    private Label emptyMessage;

    /**
     * Creates a scrollable schedule with unnumbered cards for the supplied days and undated tasks.
     *
     * @param schedules The daily layouts in display order.
     * @param undatedTasks The tasks without an associated date.
     * @throws IllegalStateException If a required schedule layout cannot be found or loaded.
     */
    public ScheduleFrame(List<DaySchedule> schedules, List<Task> undatedTasks) {
        this(schedules, undatedTasks, Map.of());
    }

    /**
     * Creates a timetable whose cards retain the task numbers used by task commands.
     *
     * @param schedules The daily layouts in display order.
     * @param undatedTasks The tasks without an associated date.
     * @param taskNumbers The original one-based task numbers; absent entries produce unnumbered cards.
     * @throws IllegalStateException If a required schedule layout cannot be found or loaded.
     */
    public ScheduleFrame(List<DaySchedule> schedules, List<Task> undatedTasks, Map<Task, Integer> taskNumbers) {
        Map<Task, Integer> displayedTaskNumbers = Map.copyOf(taskNumbers);
        ScheduleViewLoader.load(this, "/view/ScheduleContainer.fxml");

        if (!undatedTasks.isEmpty()) {
            dayContainer.getChildren().add(new ScheduleUndatedSection(undatedTasks, displayedTaskNumbers));
        }

        for (DaySchedule schedule : schedules) {
            dayContainer.getChildren().add(new ScheduleDay(schedule, displayedTaskNumbers));
        }

        if (schedules.isEmpty() && undatedTasks.isEmpty()) {
            dayContainer.getChildren().add(emptyMessage);
        }
    }
}
