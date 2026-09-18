package alpha.gui;

import alpha.task.Task;
import java.util.function.IntConsumer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/** Displays one task with controls for changing its status or deleting it. */
public class TaskCell extends ListCell<Task> {
    private final IntConsumer toggleHandler;
    private final IntConsumer deleteHandler;

    /** Creates a task cell with callbacks for its two actions. */
    public TaskCell(IntConsumer toggleHandler, IntConsumer deleteHandler) {
        this.toggleHandler = toggleHandler;
        this.deleteHandler = deleteHandler;
    }

    /** Updates the cell whenever its task changes. */
    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null) {
            this.setText(null);
            this.setGraphic(null);
            return;
        }

        int taskNumber = this.getIndex() + 1;
        Label numberLabel = new Label(String.format("%02d", taskNumber));
        numberLabel.getStyleClass().add("task-number");

        Label taskLabel = new Label(task.toString());
        taskLabel.setWrapText(true);
        taskLabel.setMaxWidth(Double.MAX_VALUE);
        taskLabel.getStyleClass().add(task.isDone() ? "completed-task" : "task-description");
        HBox.setHgrow(taskLabel, Priority.ALWAYS);

        Button toggleButton = new Button(task.isDone() ? "Unmark" : "Mark");
        toggleButton.getStyleClass().add("secondary-button");
        toggleButton.setTooltip(new Tooltip(task.isDone() ? "Mark task as incomplete" : "Mark task as complete"));
        toggleButton.setOnAction(event -> this.toggleHandler.accept(taskNumber));
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setTooltip(new Tooltip("Delete task"));
        deleteButton.setOnAction(event -> this.deleteHandler.accept(taskNumber));

        HBox row = new HBox(10, numberLabel, taskLabel, toggleButton, deleteButton);
        row.getStyleClass().add("task-card");
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(Pos.CENTER_LEFT);
        this.setText(null);
        this.setGraphic(row);
    }
}
