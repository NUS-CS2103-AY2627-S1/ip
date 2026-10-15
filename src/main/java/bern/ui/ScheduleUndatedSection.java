package bern.ui;

import java.util.List;
import java.util.Map;

import bern.task.Task;
import javafx.scene.layout.VBox;

/**
 * Displays the undated heading and task cards using an FXML section layout.
 */
public class ScheduleUndatedSection extends VBox {
    /**
     * Creates an undated section and fills it with task cards in the supplied order.
     *
     * @param tasks The undated tasks to display.
     * @param taskNumbers The original one-based task numbers; absent entries produce unnumbered cards.
     * @throws IllegalStateException If a required schedule layout cannot be found or loaded.
     */
    public ScheduleUndatedSection(List<Task> tasks, Map<Task, Integer> taskNumbers) {
        ScheduleViewLoader.load(this, "/view/ScheduleUndatedSection.fxml");

        for (Task task : tasks) {
            getChildren().add(new ScheduleTaskCard(task, "To do", taskNumbers.get(task)));
        }
    }
}
