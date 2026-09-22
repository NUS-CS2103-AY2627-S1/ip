package panda.gui;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Displays compact user commands and full-width Panda response cards.
 */
public class DialogBox extends HBox {
    private static final Pattern TASK_LINE =
            Pattern.compile("^(?:(\\d+)\\.)?\\[([TDE])\\]\\[([ X])\\] (.*)$");
    private static final Pattern TIMING =
            Pattern.compile("^(.*) \\((by: .*|from: .* to: .*)\\)$");

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Loads a message row and constrains its text to the available width.
     */
    private DialogBox(String text, Image image) {
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box layout.", exception);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(20, 20, 20));
        setMinWidth(0);
    }

    /**
     * Creates a right-aligned command chip without an avatar.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox row = new DialogBox(text, null);
        row.getChildren().remove(row.displayPicture);
        row.setAlignment(Pos.TOP_RIGHT);
        row.dialog.getStyleClass().add("user-command");
        row.dialog.maxWidthProperty().bind(row.widthProperty().multiply(0.85));
        return row;
    }

    /**
     * Creates a wide response card with a small circular Panda avatar.
     */
    public static DialogBox getPandaDialog(String text, Image image) {
        DialogBox row = new DialogBox(text, image);
        row.dialog.getStyleClass().add("panda-response");
        row.dialog.setMaxWidth(Double.MAX_VALUE);
        row.renderTaskCards(text);
        return row;
    }

    /**
     * Creates an error card whose accent and text distinguish it from success.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox row = getPandaDialog(text, image);
        row.dialog.getStyleClass().add("error-response");
        return row;
    }

    /**
     * Replaces console indentation with individually styled task cards.
     * Only recognized task responses are interpreted; errors and user text stay literal.
     */
    private void renderTaskCards(String text) {
        String[] lines = text.split("\\R");
        boolean isList = text.startsWith("Here are");
        boolean isAdded = text.startsWith("Got it. I've added");
        boolean isDeleted = text.startsWith("Noted. I've removed");
        boolean isUpdated = text.startsWith("Got it. I've updated");
        boolean isMarked = text.startsWith("Nice! I've marked");
        boolean isUnmarked = text.startsWith("OK, I've marked");
        if (isMarked || isUnmarked) {
            renderStatusCard(lines, isMarked);
            return;
        }
        if (!isList && !isAdded && !isDeleted && !isUpdated) {
            return;
        }
        VBox content = new VBox(8);
        content.setMinWidth(0);
        content.getStyleClass().add("task-response");
        String heading = isAdded ? "Task added (^_^)" : isDeleted ? "Task removed" : lines[0];
        content.getChildren().add(createLabel(heading, "response-heading"));
        int taskCount = 0;
        for (int i = 1; i < lines.length; i++) {
            Matcher task = TASK_LINE.matcher(lines[i].stripLeading());
            if (task.matches()) {
                content.getChildren().add(createTaskCard(task, isDeleted));
                taskCount++;
            } else if (!lines[i].isBlank()) {
                content.getChildren().add(createLabel(lines[i].strip(), "response-caption"));
            }
        }
        if (isList && taskCount == 0) {
            content.getChildren().add(createLabel("No tasks here. A clear bamboo patch! (^_^)", "empty-message"));
        }
        HBox.setHgrow(content, Priority.ALWAYS);
        getChildren().set(getChildren().indexOf(dialog), content);
    }

    /**
     * Highlights the affected task without relying on the console's leading spaces.
     * Status confirmations contain only a status marker and the literal task name.
     */
    private void renderStatusCard(String[] lines, boolean isMarked) {
        VBox content = new VBox(8);
        content.setMinWidth(0);
        content.getStyleClass().add("task-response");
        content.getChildren().add(createLabel(
                isMarked ? "Task completed (^_^)" : "Task reopened (^_^)", "response-heading"));
        VBox card = new VBox(4);
        card.setMinWidth(0);
        card.getStyleClass().addAll("task-card", isMarked ? "completed-task" : "reopened-task");
        card.getChildren().add(createLabel(isMarked ? "Done" : "Open", "task-metadata"));
        String taskLine = lines[1].stripLeading();
        card.getChildren().add(createLabel(taskLine.substring(4), "task-title"));
        content.getChildren().add(card);
        HBox.setHgrow(content, Priority.ALWAYS);
        getChildren().set(getChildren().indexOf(dialog), content);
    }

    /**
     * Separates a task title from its type, status, number, and timing.
     */
    private VBox createTaskCard(Matcher task, boolean isDeleted) {
        String type = switch (task.group(2)) {
            case "D" -> "Deadline";
            case "E" -> "Event";
            default -> "To-do";
        };
        String state = task.group(3).equals("X") ? "Done" : "Open";
        String number = task.group(1) == null ? "" : "#" + task.group(1) + "  |  ";
        VBox card = new VBox(4);
        card.setMinWidth(0);
        card.getStyleClass().addAll("task-card", "task-" + task.group(2).toLowerCase(java.util.Locale.ROOT));
        if (isDeleted) {
            card.getStyleClass().add("removed-task");
        }
        card.getChildren().add(createLabel(number + type + "  |  " + state, "task-metadata"));
        String title = task.group(4);
        Matcher timing = TIMING.matcher(title);
        // Only dated task types can have a generated timing suffix.
        boolean hasTiming = !task.group(2).equals("T") && timing.matches();
        card.getChildren().add(createLabel(hasTiming ? timing.group(1) : title, "task-title"));
        if (hasTiming) {
            card.getChildren().add(createLabel(timing.group(2), "task-metadata"));
        }
        return card;
    }

    /**
     * Creates a wrapping label that can shrink with a narrow phone-sized window.
     */
    private Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMinHeight(USE_PREF_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add(style);
        return label;
    }

    String getDialogText() {
        return dialog.getText();
    }
}
