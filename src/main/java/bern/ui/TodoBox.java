package bern.ui;

import bern.task.Todo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Represents a task box consisting of a todo.
 */
public class TodoBox extends TaskBox {
    @FXML
    private Label description;

    /**
     * Creates a task box with the given todo.
     *
     * @param todo The todo represented by the task box.
     */
    public TodoBox(Todo todo) {
        loadView("/view/TodoBox.fxml");

        setMark(todo.isDone());
        description.setText(todo.getName());
    }
}
