package dude.gui;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import dude.task.CorruptedTask;
import dude.task.Deadline;
import dude.task.Event;
import dude.task.Task;

/**
 * Renders one task as a structured row instead of a formatted text line.
 */
public class TaskCell extends ListCell<Task> {
    /**
     * Creates an empty task cell for the JavaFX list view.
     */
    public TaskCell() {
    }

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        Label number = new Label(String.format("%d", getIndex() + 1));
        number.getStyleClass().add("task-number");
        Label status = new Label(task.isDone() ? "✓" : "○");
        status.getStyleClass().addAll("task-status", task.isDone() ? "done" : "pending");
        Label type = new Label(getType(task));
        type.getStyleClass().add("task-type");
        Label description = new Label(task.getDescription());
        description.setWrapText(true);
        description.setMaxWidth(Double.MAX_VALUE);
        description.getStyleClass().add("task-description");
        VBox details = new VBox(2, type, description);
        details.setMaxWidth(Double.MAX_VALUE);
        String extraDetails = getExtraDetails(task);
        if (!extraDetails.isBlank()) {
            Label extra = new Label(extraDetails);
            extra.setWrapText(true);
            extra.setMaxWidth(Double.MAX_VALUE);
            extra.getStyleClass().add("task-details");
            details.getChildren().add(extra);
        }
        HBox.setHgrow(details, Priority.ALWAYS);
        HBox row = new HBox(10, number, status, details);
        row.getStyleClass().add("task-row");
        setGraphic(row);
    }

    /**
     * Returns the display label for a task subtype.
     *
     * @param task Task to classify.
     * @return Task subtype label.
     */
    private static String getType(Task task) {
        if (task instanceof Deadline) {
            return "DEADLINE";
        } else if (task instanceof Event) {
            return "EVENT";
        } else if (task instanceof CorruptedTask) {
            return "CORRUPTED";
        }
        return "TODO";
    }

    /**
     * Returns date details to display below a dated task.
     *
     * @param task Task whose details are displayed.
     * @return Additional task details, or an empty string.
     */
    private static String getExtraDetails(Task task) {
        if (task instanceof Deadline deadline) {
            return "Due " + deadline.getBy();
        } else if (task instanceof Event event) {
            return String.format("From %s%nTo %s", event.getFrom(), event.getTo());
        }
        return "";
    }
}
