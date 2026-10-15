package gnaix;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import gnaix.task.Deadline;
import gnaix.task.Event;
import gnaix.task.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * JavaFX view component that displays one task in a structured, scannable form.
 */
public class TaskCard extends HBox {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Creates a card for a task displayed in a Gnaix response.
     *
     * @param taskDisplay Task and number to render.
     */
    public TaskCard(TaskDisplay taskDisplay) {
        Task task = taskDisplay.getTask();

        getStyleClass().add("task-card");
        if (task.isCompleted()) {
            getStyleClass().add("task-card-completed");
        }
        setMaxWidth(Double.MAX_VALUE);

        HBox summary = new HBox();
        summary.getStyleClass().add("task-summary");
        summary.setAlignment(Pos.TOP_LEFT);
        summary.setMaxWidth(Double.MAX_VALUE);

        HBox identity = new HBox();
        identity.getStyleClass().add("task-identity");
        identity.setAlignment(Pos.CENTER_LEFT);

        Label number = new Label(String.valueOf(taskDisplay.getNumber()));
        number.getStyleClass().add("task-number");

        Label type = new Label(getTaskType(task));
        type.getStyleClass().addAll("task-type", getTaskTypeStyle(task));

        Label status = new Label(task.isCompleted() ? "\u2611" : "\u2610");
        status.getStyleClass().add("task-status");
        status.getStyleClass().add(task.isCompleted()
                ? "task-status-completed"
                : "task-status-open");

        VBox details = new VBox();
        details.getStyleClass().add("task-details");
        details.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label description = new Label(task.getDescription());
        description.getStyleClass().add("task-description");
        description.setWrapText(true);
        description.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(description, Priority.ALWAYS);

        identity.getChildren().addAll(number, type);
        summary.getChildren().addAll(identity, status, description);
        details.getChildren().add(summary);
        addMetadata(task, details);
        addTags(task, details);

        getChildren().add(details);
    }

    /**
     * Adds deadline or event timing details when the task has them.
     *
     * @param task Task being displayed.
     * @param details Container for task details.
     */
    private void addMetadata(Task task, VBox details) {
        String metadata = getMetadata(task);

        if (metadata.isEmpty()) {
            return;
        }

        Label metadataLabel = new Label(metadata);
        metadataLabel.getStyleClass().add("task-metadata");
        metadataLabel.setWrapText(false);
        metadataLabel.setMaxWidth(Double.MAX_VALUE);
        details.getChildren().add(metadataLabel);
    }

    /**
     * Adds tag chips while preserving the task's tag order.
     *
     * @param task Task being displayed.
     * @param details Container for task details.
     */
    private void addTags(Task task, VBox details) {
        if (task.getTags().isEmpty()) {
            return;
        }

        FlowPane tagPane = new FlowPane();
        tagPane.getStyleClass().add("task-tags");

        task.getTags().forEach(tag -> {
            Label tagLabel = new Label("#" + tag);
            tagLabel.getStyleClass().add("task-tag");
            tagPane.getChildren().add(tagLabel);
        });

        details.getChildren().add(tagPane);
    }

    /**
     * Returns human-friendly date or event timing text for the task.
     *
     * @param task Task being displayed.
     * @return Timing text, or an empty string for todos.
     */
    private String getMetadata(Task task) {
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "Due " + formatDate(deadline.getDoBy());
        }

        if (task instanceof Event) {
            Event event = (Event) task;
            return formatEventTime(event.getFrom(), event.getTo());
        }

        return "";
    }

    /**
     * Returns the compact task type label used beside each task card.
     *
     * @param task Task being displayed.
     * @return TODO, DEADLINE, or EVENT depending on the task type.
     */
    private String getTaskType(Task task) {
        if (task instanceof Deadline) {
            return "DEADLINE";
        }

        if (task instanceof Event) {
            return "EVENT";
        }

        return "TODO";
    }

    /**
     * Returns the CSS class that gives each task type its visual accent.
     *
     * @param task Task being displayed.
     * @return Style class for the task type chip.
     */
    private String getTaskTypeStyle(Task task) {
        if (task instanceof Deadline) {
            return "task-type-deadline";
        }

        if (task instanceof Event) {
            return "task-type-event";
        }

        return "task-type-todo";
    }

    /**
     * Formats an event period for compact GUI display.
     *
     * @param from Event start.
     * @param to Event end.
     * @return Human-friendly event period.
     */
    private String formatEventTime(LocalDateTime from, LocalDateTime to) {
        if (from.toLocalDate().equals(to.toLocalDate())) {
            return formatDate(from.toLocalDate()) + " \u00b7 "
                    + from.format(TIME_FORMAT) + "\u2013" + to.format(TIME_FORMAT);
        }

        return formatDateTime(from) + " \u2013 " + formatDateTime(to);
    }

    private String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return formatDate(dateTime.toLocalDate()) + " \u00b7 "
                + dateTime.format(TIME_FORMAT);
    }
}
