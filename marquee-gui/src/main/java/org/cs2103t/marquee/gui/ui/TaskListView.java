package org.cs2103t.marquee.gui.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.cs2103t.marquee.core.task.Task;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * Controller for a task list view.
 */
public class TaskListView extends ListView<Task> {
    private final ListProperty<Task> taskList =
            new SimpleListProperty<>(this, "taskList", FXCollections.observableList(new ArrayList<>()));

    /**
     * Creates a new {@code TaskListView}.
     */
    public TaskListView() {
        getStylesheets().add(getClass().getResource("/style/task-list.css").toExternalForm());
        setMaxWidth(Double.MAX_VALUE);
        setFixedCellSize(75);
        setCellFactory(_ -> new ListCell<>() {
            private TaskItemView taskView;

            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (taskView == null) {
                    try {
                        taskView = new TaskItemView(this);
                        taskView.maxWidthProperty().bind(widthProperty());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (taskView == null) {
                        setText(item.toString());
                    } else {
                        taskView.setTask(item);
                        taskView.setIndex(getIndex() + 1);
                        setGraphic(taskView);
                    }
                }
            }
        });
        setPlaceholder(new Label("(No tasks yet)"));
    }
}
