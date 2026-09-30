package bean.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private StackPane avatarContainer;
    @FXML
    private Circle imageClip;
    @FXML
    private Circle avatarBorder;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
            if (dialog == null || displayPicture == null) {
                throw new IllegalStateException("DialogBox.fxml must inject both dialog controls");
            }
            if (getChildren().size() != 2) {
                throw new IllegalStateException("DialogBox.fxml must contain two child nodes");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        if (isTaskListResponse(text)) {
            getChildren().set(0, createTaskList(text));
        } else if (isTaskActionResponse(text)) {
            getChildren().set(0, createTaskAction(text));
        }
    }

    /** Returns whether the response should be rendered as a structured task list. */
    private boolean isTaskListResponse(String text) {
        return text.startsWith("Here are the tasks in your list:");
    }

    /** Returns whether the response describes an add or delete task action. */
    private boolean isTaskActionResponse(String text) {
        return text.startsWith("Alrighty! I've added the following task:")
                || text.startsWith("Alrighty! I've removed the following task:");
    }

    /** Creates a styled list of task rows from the command response. */
    private VBox createTaskList(String text) {
        VBox taskList = new VBox(6);
        taskList.setMaxWidth(Double.MAX_VALUE);
        taskList.getStyleClass().add("task-list");

        String[] lines = text.split("\\R");
        Label header = new Label(lines[0]);
        header.getStyleClass().add("task-list-header");
        taskList.getChildren().add(header);

        int taskCount = 0;
        for (int i = 1; i < lines.length; i++) {
            if (!lines[i].isBlank()) {
                taskList.getChildren().add(createTaskRow(lines[i], "task-list-item"));
                taskCount++;
            }
        }

        if (taskCount == 0) {
            Label emptyMessage = new Label("No tasks yet.");
            emptyMessage.getStyleClass().add("task-list-empty");
            taskList.getChildren().add(emptyMessage);
        }
        return taskList;
    }

    /** Creates a structured panel for an add or delete response. */
    private VBox createTaskAction(String text) {
        String[] sections = text.split("Here are the tasks in your list:", 2);
        String[] actionLines = sections[0].split("\\R");

        VBox actionPanel = new VBox(6);
        actionPanel.setMaxWidth(Double.MAX_VALUE);
        actionPanel.getStyleClass().add("task-action");

        Label header = new Label(actionLines[0]);
        header.getStyleClass().add("task-action-header");
        actionPanel.getChildren().add(header);

        if (actionLines.length > 2 && !actionLines[2].isBlank()) {
            actionPanel.getChildren().add(createTaskRow(actionLines[2], "task-action-item"));
        }

        if (actionLines.length > 4 && !actionLines[4].isBlank()) {
            Label footer = new Label(actionLines[4]);
            footer.setWrapText(true);
            footer.getStyleClass().add("task-action-footer");
            actionPanel.getChildren().add(footer);
        }

        if (sections.length > 1) {
            actionPanel.getChildren().add(
                    createTaskList("Here are the tasks in your list:" + sections[1]));
        }
        return actionPanel;
    }

    /** Creates a styled task row with priority and completion state. */
    private TextFlow createTaskRow(String taskText, String rowStyleClass) {
        Text taskLabel = new Text(taskText);
        taskLabel.setStrikethrough(taskText.contains("[X]"));
        taskLabel.getStyleClass().add("task-row-text");

        TextFlow taskRow = new TextFlow(taskLabel);
        taskRow.setMaxWidth(Double.MAX_VALUE);
        taskRow.getStyleClass().add(rowStyleClass);
        taskRow.getStyleClass().add(getPriorityStyleClass(taskText));
        if (taskText.contains("[X]")) {
            taskRow.getStyleClass().add("task-completed");
        }
        return taskRow;
    }

    /** Returns the style class associated with a task's priority prefix. */
    private String getPriorityStyleClass(String taskText) {
        if (taskText.contains("HIGH ")) {
            return "priority-high";
        }
        if (taskText.contains("MEDIUM ")) {
            return "priority-medium";
        }
        return "priority-low";
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    public static StackPane getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.compactUserAvatar();
        return wrap(dialogBox, "user-dialog", 700);
    }

    /** Makes the user avatar more compact while preserving its circular border. */
    private void compactUserAvatar() {
        double avatarSize = 91;
        double imageSize = 81;
        double center = imageSize / 2;

        avatarContainer.setPrefSize(avatarSize, avatarSize);
        displayPicture.setFitWidth(imageSize);
        displayPicture.setFitHeight(imageSize);
        imageClip.setCenterX(center);
        imageClip.setCenterY(center);
        imageClip.setRadius(center);
        avatarBorder.setCenterX(center);
        avatarBorder.setCenterY(center);
        avatarBorder.setRadius(center - 1.5);
    }

    public static StackPane getBeanDialog(String text, Image img) {
        return getBeanDialog(text, img, false);
    }

    /** Creates a Bean response dialog, optionally styled as an error. */
    public static StackPane getBeanDialog(String text, Image img, boolean isError) {
        var dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        String styleClass = isError ? "error-dialog" : "bean-dialog";
        return wrap(dialogBox, styleClass, -700);
    }

    /** Creates a Bean introduction with a graphic above the greeting text. */
    public static StackPane getBeanIntroDialog(String text, Image avatar, Image introGraphic) {
        DialogBox dialogBox = new DialogBox(text, avatar);
        dialogBox.setIntroGraphic(introGraphic);
        dialogBox.flip();
        return wrap(dialogBox, "bean-dialog", -700);
    }

    /** Renders the introduction graphic above the text while preserving the dialog label. */
    private void setIntroGraphic(Image introGraphic) {
        ImageView graphic = new ImageView(introGraphic);
        graphic.setFitWidth(330);
        graphic.setFitHeight(100);
        graphic.setPreserveRatio(true);
        graphic.setSmooth(false);
        dialog.setGraphic(graphic);
        dialog.setContentDisplay(ContentDisplay.TOP);
        dialog.setGraphicTextGap(10);
    }

    /** Wraps a dialogue in a transparent node that supplies the outer shadow. */
    private static StackPane wrap(DialogBox dialogBox, String styleClass, double startX) {
        StackPane wrapper = new StackPane(dialogBox);
        wrapper.getStyleClass().add("dialog-wrapper");
        wrapper.getStyleClass().add(styleClass);
        animateIn(wrapper, startX);
        return wrapper;
    }

    /** Animates a dialogue into place from its speaker's side. */
    private static void animateIn(StackPane wrapper, double startX) {
        Duration duration = Duration.millis(220);

        FadeTransition fade = new FadeTransition(duration, wrapper);
        fade.setFromValue(0);
        fade.setToValue(1);

        double reboundX = startX > 0 ? -14 : 14;
        TranslateTransition slide = new TranslateTransition(duration, wrapper);
        slide.setFromX(startX);
        slide.setToX(reboundX);
        slide.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition settle = new TranslateTransition(Duration.millis(90), wrapper);
        settle.setFromX(reboundX);
        settle.setToX(0);
        settle.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition pop = new ScaleTransition(duration, wrapper);
        pop.setFromX(0.94);
        pop.setFromY(0.94);
        pop.setToX(1);
        pop.setToY(1);
        pop.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(
                fade,
                pop,
                new SequentialTransition(slide, settle)).play();
    }
}
