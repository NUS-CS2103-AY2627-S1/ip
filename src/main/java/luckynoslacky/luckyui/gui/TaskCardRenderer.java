package luckynoslacky.luckyui.gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskView;

/**
 * Builds the JavaFX nodes used to display one parsed task as a card.
 */
final class TaskCardRenderer {
    private static final DateTimeFormatter TASK_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("EEE MMM dd uuuu, h.mma", Locale.ENGLISH);
    private static final double TYPE_ICON_SIZE = 14.0;
    private static final double STATUS_ICON_SIZE = 18.0;
    private static final Image TODO_ICON = loadIcon("/images/icons/task-todo.png");
    private static final Image DEADLINE_ICON = loadIcon("/images/icons/task-deadline.png");
    private static final Image EVENT_ICON = loadIcon("/images/icons/task-event.png");
    private static final Image COMPLETED_ICON = loadIcon("/images/icons/task-completed.png");
    private static final Image INCOMPLETE_ICON = loadIcon("/images/icons/task-incomplete.png");

    private TaskCardRenderer() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Creates one semantic task card from structured task data.
     *
     * @param task task data to render
     * @return task card node
     */
    static VBox createTaskCard(TaskView task) {
        VBox taskCard = new VBox();
        taskCard.getStyleClass().addAll(
                "personality-task-card",
                "personality-task-card-" + getTypeStyle(task.taskType()));

        Label typeLabel = createTextLabel(
                getTypeLabel(task.taskType()), "personality-task-type");
        ImageView typeIcon = createIcon(
                getTypeIcon(task.taskType()),
                TYPE_ICON_SIZE,
                "personality-task-type-icon");
        HBox typeRow = new HBox(typeLabel, typeIcon);
        typeRow.getStyleClass().add("personality-task-type-row");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        typeRow.getChildren().add(spacer);
        if (task.taskNumber() != null) {
            typeRow.getChildren().add(createTextLabel(
                    "#" + task.taskNumber(), "personality-task-number"));
        }
        ImageView statusIcon = createIcon(
                task.isDone() ? COMPLETED_ICON : INCOMPLETE_ICON,
                STATUS_ICON_SIZE,
                "personality-task-status");
        Label titleLabel = createTextLabel(
                task.description(), "personality-task-title");
        HBox titleRow = new HBox(statusIcon, titleLabel);
        titleRow.getStyleClass().add("personality-task-title-row");

        taskCard.getChildren().addAll(typeRow, titleRow);
        for (String detail : getDetails(task)) {
            taskCard.getChildren().add(
                    createTextLabel(detail, "personality-task-details"));
        }
        return taskCard;
    }

    /**
     * Returns the spoken description used by assistive technologies.
     *
     * @param task task data to describe
     * @return accessible task description without decorative symbols
     */
    static String getAccessibleDescription(TaskView task) {
        String taskNumberDescription = task.taskNumber() == null
                ? ""
                : ", task " + task.taskNumber();
        String statusDescription = task.isDone() ? "completed" : "incomplete";
        String description = getTypeLabel(task.taskType())
                + taskNumberDescription
                + ", " + statusDescription
                + ", " + task.description();
        List<String> details = getDetails(task);
        return details.isEmpty()
                ? description
                : description + ", " + String.join(", ", details);
    }

    /** Returns the user-facing label for a task type. */
    private static String getTypeLabel(Task.TaskType taskType) {
        return switch (taskType) {
            case TODO -> "TODO";
            case DEADLINE -> "DEADLINE";
            case EVENT -> "EVENT";
        };
    }

    /** Returns the bundled decorative icon for a task type. */
    private static Image getTypeIcon(Task.TaskType taskType) {
        return switch (taskType) {
            case TODO -> TODO_ICON;
            case DEADLINE -> DEADLINE_ICON;
            case EVENT -> EVENT_ICON;
        };
    }

    /** Creates a fixed-size decorative icon excluded from assistive output. */
    private static ImageView createIcon(
            Image icon,
            double iconSize,
            String style) {
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(iconSize);
        imageView.setFitHeight(iconSize);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setAccessibleRole(AccessibleRole.NODE);
        imageView.setAccessibleText("");
        imageView.setFocusTraversable(false);
        imageView.setMouseTransparent(true);
        imageView.getStyleClass().add(style);
        return imageView;
    }

    /** Loads one bundled Noto Emoji image. */
    private static Image loadIcon(String resourcePath) {
        return new Image(Objects.requireNonNull(
                TaskCardRenderer.class.getResource(resourcePath),
                "Missing task icon resource: " + resourcePath).toExternalForm());
    }

    /** Returns the CSS modifier for a task type. */
    private static String getTypeStyle(Task.TaskType taskType) {
        return switch (taskType) {
            case TODO -> "todo";
            case DEADLINE -> "deadline";
            case EVENT -> "event";
        };
    }

    /** Returns formatted schedule details for a task card. */
    private static List<String> getDetails(TaskView task) {
        return switch (task.taskType()) {
            case TODO -> List.of();
            case DEADLINE -> List.of("by "
                    + formatDateTime(task.taskTimes().getEndTime()));
            case EVENT -> List.of(
                    "from " + formatDateTime(task.taskTimes().getStartTime()),
                    "to " + formatDateTime(task.taskTimes().getEndTime()));
        };
    }

    /** Formats a task time with the existing CLI display convention. */
    private static String formatDateTime(LocalDateTime dateTime) {
        return TASK_TIME_FORMATTER.format(dateTime)
                .replace("AM", "am")
                .replace("PM", "pm");
    }

    /** Creates a wrapped, non-interactive task-card label. */
    private static Label createTextLabel(String text, String... styles) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setAccessibleRole(AccessibleRole.NODE);
        label.setAccessibleText("");
        label.setFocusTraversable(false);
        label.getStyleClass().addAll(styles);
        return label;
    }
}
