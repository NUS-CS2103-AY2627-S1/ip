package chudgpt;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final double PROFILE_PICTURE_RADIUS = 52;
    private static final double USER_MESSAGE_MAX_WIDTH = 280;
    private static final Pattern PRIORITY_PATTERN = Pattern.compile(
            "\\[P:(EXTREME|HIGH|MEDIUM|LOW|NONE)\\s*\\]");

    @FXML
    private TextFlow dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setDialogText(text);
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(PROFILE_PICTURE_RADIUS, PROFILE_PICTURE_RADIUS,
                PROFILE_PICTURE_RADIUS));
    }

    /** Adds text nodes while applying a priority-specific style to each priority token. */
    private void setDialogText(String text) {
        Matcher matcher = PRIORITY_PATTERN.matcher(text);
        int previousEnd = 0;

        while (matcher.find()) {
            addTextNode(text.substring(previousEnd, matcher.start()), "dialog-text");
            String priorityStyle = "priority-" + matcher.group(1).toLowerCase(Locale.ROOT);
            addTextNode(matcher.group(), "dialog-text", priorityStyle);
            previousEnd = matcher.end();
        }
        addTextNode(text.substring(previousEnd), "dialog-text");
    }

    /** Adds one text segment with the supplied CSS classes. */
    private void addTextNode(String text, String... styleClasses) {
        Text textNode = new Text(text);
        textNode.getStyleClass().addAll(styleClasses);
        dialog.getChildren().add(textNode);
    }

    /** Formats this dialog as a left-aligned application response. */
    private void formatAsApplicationDialog(String styleClass) {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add(styleClass);
        dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialog, Priority.ALWAYS);
    }

    /**
     * Creates a compact, right-aligned dialog for a command entered by the user.
     *
     * @param text command entered by the user.
     * @param img image representing the user.
     * @return formatted user dialog.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.dialog.setMaxWidth(USER_MESSAGE_MAX_WIDTH);
        return dialogBox;
    }

    /**
     * Creates a wide, left-aligned dialog for a normal application response.
     *
     * @param text response returned by the application.
     * @param img image representing the application.
     * @return formatted application dialog.
     */
    public static DialogBox getDukeDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.formatAsApplicationDialog("duke-dialog");
        return dialogBox;
    }

    /**
     * Creates a green dialog confirming that a task was added.
     *
     * @param text task-creation response returned by the application.
     * @param img image representing the application.
     * @return formatted success dialog.
     */
    public static DialogBox getSuccessDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.formatAsApplicationDialog("success-dialog");
        dialogBox.dialog.setAccessibleText("Success: " + text);
        return dialogBox;
    }

    /**
     * Creates a visually emphasized dialog for an application error.
     *
     * @param text error response returned by the application.
     * @param img image representing the application.
     * @return formatted error dialog.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox("⚠ " + text, img);
        dialogBox.formatAsApplicationDialog("error-dialog");
        dialogBox.dialog.setAccessibleText("Error: " + text);
        return dialogBox;
    }
}
