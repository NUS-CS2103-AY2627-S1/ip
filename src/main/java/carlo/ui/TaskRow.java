package carlo.ui;

import java.time.LocalDateTime;

import carlo.task.Deadline;
import carlo.task.Event;
import carlo.task.Task;
import carlo.task.TaskGroup;
import carlo.task.TaskSchedule;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * One line in the task list: a checkbox to mark the task done, its
 * description, a small tag showing its type, a coloured "pill" describing
 * when it is due, and a delete button.
 */
public class TaskRow extends HBox {
    /**
     * Creates a row for the given task.
     *
     * @param task the task to show
     * @param group the urgency group the task is in, which decides the pill colour
     * @param now the current date and time, used to describe the task's timing
     * @param actions what to do when the user ticks or deletes the task
     */
    public TaskRow(Task task, TaskGroup group, LocalDateTime now, TaskListPane.TaskActions actions) {
        getStyleClass().add("task-row");
        if (task.isDone()) {
            getStyleClass().add("done");
        }
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(task.isDone());
        checkBox.setOnAction(event -> actions.setDone(task, checkBox.isSelected()));

        Label description = new Label(task.getDescription());
        description.getStyleClass().add("task-description");
        description.setMinWidth(0);

        Label typeTag = new Label(typeOf(task));
        typeTag.getStyleClass().add("type-tag");

        // An empty, stretchy region that pushes the pill and delete button to the right edge.
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(checkBox, description, typeTag, spacer);

        String timing = TaskSchedule.describeTiming(task, now);
        if (!timing.isEmpty() && !task.isDone()) {
            Label pill = new Label(timing);
            pill.getStyleClass().addAll("pill", "pill-" + group.name().toLowerCase().replace('_', '-'));
            pill.setMinWidth(Region.USE_PREF_SIZE);
            getChildren().add(pill);
        }

        Button deleteButton = new Button("✕");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setTooltip(new Tooltip("Delete"));
        deleteButton.setOnAction(event -> actions.delete(task));
        getChildren().add(deleteButton);

        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(event -> actions.edit(task));
        MenuItem toggleItem = new MenuItem(task.isDone() ? "Mark as not done" : "Mark as done");
        toggleItem.setOnAction(event -> actions.setDone(task, !task.isDone()));
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> actions.delete(task));
        ContextMenu menu = new ContextMenu(editItem, toggleItem, deleteItem);
        setOnContextMenuRequested(event -> menu.show(this, event.getScreenX(), event.getScreenY()));

        // Double-clicking the row edits the task. Double-clicks on the checkbox or
        // delete button are ignored, since those already do their own thing.
        setOnMouseClicked(event -> {
            boolean isOnButton = checkBox.isHover() || deleteButton.isHover();
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !isOnButton) {
                actions.edit(task);
            }
        });
        Tooltip.install(description, new Tooltip("Double-click to edit"));
    }

    private static String typeOf(Task task) {
        if (task instanceof Deadline) {
            return "deadline";
        }
        if (task instanceof Event) {
            return "event";
        }
        return "todo";
    }
}
