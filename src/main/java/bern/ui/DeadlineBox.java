package bern.ui;

import bern.task.Deadline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Represents a task box consisting of a deadline.
 */
public class DeadlineBox extends TaskBox {
    @FXML
    private Label time;
    @FXML
    private Label description;

    /**
     * Creates a task box with the given deadline.
     *
     * @param deadline The deadline represented by the task box.
     */
    public DeadlineBox(Deadline deadline) {
        loadView("/view/DeadlineBox.fxml");

        setMark(deadline.isDone());
        time.setText(deadline.getDateTimeString());
        description.setText(deadline.getName());
    }
}
