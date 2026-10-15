package bern.ui;

import bern.task.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 * Displays a timetable task using an FXML layout, including its completion state and full details.
 */
public class ScheduleTaskCard extends VBox {
    @FXML
    private Label description;
    @FXML
    private Label time;

    /**
     * Creates a task card from its FXML layout and fills in the task's display details.
     * Disables completed tasks and applies their completed styling.
     *
     * @param task The task represented by the card.
     * @param timing The time range or other timing description to display.
     * @param taskNumber The one-based command index, or {@code null} when no index is supplied.
     * @throws IllegalStateException If the task card layout cannot be found or loaded.
     */
    public ScheduleTaskCard(Task task, String timing, Integer taskNumber) {
        ScheduleViewLoader.load(this, "/view/ScheduleTaskCard.fxml");

        String taskNumberPrefix = taskNumber == null ? "" : taskNumber + ". ";
        description.setText(taskNumberPrefix + task.getName());
        time.setText(timing);
        if (task.isDone()) {
            getStyleClass().add("schedule-completed");
            this.setDisable(true);
        }
        setAccessibleText(task.toString());
        Tooltip.install(this, new Tooltip(task.toString()));
    }
}
