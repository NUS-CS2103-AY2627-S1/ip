package stewie.ui.gui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Creates the shared task-card visuals used by Chat and My List.
 */
final class TaskCardFactory {
    private static final Pattern TASK_PATTERN = Pattern.compile("\\[([TDE])] \\[(X| )] (.*)");

    private TaskCardFactory() {
    }

    /**
     * Creates a card with its type, description, original number, and completion styling.
     */
    static HBox create(int index, String taskText) {
        return createTaskCardLayout(index, parseTaskCardData(taskText));
    }

    /**
     * Returns the completion state encoded in a formatted task.
     */
    static boolean isDone(String taskText) {
        return parseTaskCardData(taskText).isDone();
    }

    /**
     * Represents the display fields extracted from a formatted task.
     *
     * @param type Task type badge.
     * @param isDone Whether the task is complete.
     * @param description Task description including any dates.
     */
    private record TaskCardData(String type, boolean isDone, String description) {
    }

    /**
     * Decodes task text once, retaining the existing fallback for unrecognized text.
     */
    private static TaskCardData parseTaskCardData(String taskText) {
        Matcher matcher = TASK_PATTERN.matcher(taskText);
        if (!matcher.matches()) {
            return new TaskCardData("T", false, taskText);
        }
        return new TaskCardData(matcher.group(1), "X".equals(matcher.group(2)), matcher.group(3));
    }

    /**
     * Assembles a task card from its styled container, badge, and description.
     */
    private static HBox createTaskCardLayout(int index, TaskCardData data) {
        HBox card = createTaskCardContainer(data.isDone());
        card.getChildren().addAll(createTaskTypeBadge(data.type()), createTaskDetails(index, data.description()));
        return card;
    }

    /**
     * Creates the outer card with its completion styling.
     */
    private static HBox createTaskCardContainer(boolean isDone) {
        HBox card = new HBox(12);
        card.getStyleClass().add("task-card");
        if (isDone) {
            card.getStyleClass().add("task-done");
        }
        card.setAlignment(Pos.CENTER_LEFT);
        return card;
    }

    /**
     * Creates a badge identifying the task type.
     */
    private static Label createTaskTypeBadge(String type) {
        Label typeBadge = new Label(type);
        typeBadge.getStyleClass().addAll("task-type", "task-type-" + type.toLowerCase());
        return typeBadge;
    }

    /**
     * Creates the task description and its original one-based number.
     */
    private static VBox createTaskDetails(int index, String description) {
        VBox details = new VBox(3);
        HBox.setHgrow(details, Priority.ALWAYS);
        Label taskLabel = new Label(description);
        taskLabel.getStyleClass().add("task-description");
        taskLabel.setWrapText(true);
        Label numberLabel = new Label(String.format("task %02d", index));
        numberLabel.getStyleClass().add("task-number");
        details.getChildren().addAll(taskLabel, numberLabel);
        return details;
    }
}
