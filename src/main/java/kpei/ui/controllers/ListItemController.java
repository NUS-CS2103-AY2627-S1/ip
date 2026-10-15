package kpei.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import kpei.datatypes.Deadline;
import kpei.datatypes.Event;
import kpei.datatypes.Task;

/**
 * Controller for an individual task item card displayed in the List View.
 */
public class ListItemController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label dateLabel;

    /**
     * Binds a task model and its 1-based index to this card component.
     *
     * @param index 1-based index of the task.
     * @param task Task data to display.
     */
    public void setTask(int index, Task task) {
        if (task == null) {
            return;
        }

        String markPrefix = task.isMarked() ? "[X] " : "";
        titleLabel.setText(String.format("%s%d. %s", markPrefix, index, task.getDescription()));

        titleLabel.getStyleClass().removeAll("list-item-title-marked", "list-item-title");
        if (task.isMarked()) {
            titleLabel.getStyleClass().add("list-item-title-marked");
        } else {
            titleLabel.getStyleClass().add("list-item-title");
        }

        typeLabel.setText(task.getType());

        if (task instanceof Deadline deadline) {
            dateLabel.setText("by " + deadline.getFormattedByDate());
            dateLabel.setVisible(true);
            dateLabel.setManaged(true);
        } else if (task instanceof Event event) {
            dateLabel.setText(String.format("from %s to %s",
                    event.getFormattedFromDate(), event.getFormattedToDate()));
            dateLabel.setVisible(true);
            dateLabel.setManaged(true);
        } else {
            dateLabel.setText("");
            dateLabel.setVisible(false);
            dateLabel.setManaged(false);
        }
    }
}
