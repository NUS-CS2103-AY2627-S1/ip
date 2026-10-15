package bern.ui;

import bern.task.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Represents a task box consisting of an event.
 */
public class EventBox extends TaskBox {
    @FXML
    private Label startTime;
    @FXML
    private Label endTime;
    @FXML
    private Label description;

    /**
     * Creates a task box with the given event.
     *
     * @param event The event represented by the task box.
     */
    public EventBox(Event event) {
        loadView("/view/EventBox.fxml");

        setMark(event.isDone());
        startTime.setText(event.getStartDateTimeString());
        endTime.setText(event.getEndDateTimeString());
        description.setText(event.getName());
    }
}
