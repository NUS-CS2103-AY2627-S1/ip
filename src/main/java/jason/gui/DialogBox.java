package jason.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** Represents one chatbot message with a simple speaker avatar. */
public class DialogBox extends HBox {
    private static final String USER_AVATAR_PATH = "/images/user-avatar.jpg";
    private static final String JASON_AVATAR_PATH = "/images/jason-avatar.png";

    @FXML
    private Label text;
    @FXML
    private ImageView avatar;

    /**
     * Creates a dialog box from its FXML view.
     *
     * @param message message to display.
     * @param avatarPath classpath path of the speaker avatar to display.
     */
    private DialogBox(String message, String avatarPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box GUI.", exception);
        }

        text.setText(message);
        avatar.setImage(new Image(DialogBox.class.getResourceAsStream(avatarPath)));
        avatar.setFitHeight(32.0);
        avatar.setFitWidth(32.0);
        avatar.setPreserveRatio(true);
        avatar.setClip(new Circle(16.0, 16.0, 16.0));
        getStyleClass().add("dialog-box");
        text.getStyleClass().add("dialog-text");
        avatar.getStyleClass().add("avatar");
        getStylesheets().add(DialogBox.class.getResource("/css/dialog-box.css").toExternalForm());
    }

    /**
     * Creates a dialog box representing a user message.
     *
     * @param message message written by the user.
     * @return a right-aligned user dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message, USER_AVATAR_PATH);
        dialogBox.avatar.setManaged(false);
        dialogBox.avatar.setVisible(false);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a dialog box representing a Jason response.
     *
     * @param message response written by Jason.
     * @return a left-aligned Jason dialog box.
     */
    public static DialogBox getJasonDialog(String message) {
        DialogBox dialogBox = new DialogBox(message, JASON_AVATAR_PATH);
        dialogBox.flip();
        dialogBox.getStyleClass().add("jason-dialog");
        return dialogBox;
    }

    /**
     * Creates a Jason response dialog with styling based on the command type.
     *
     * @param message response written by Jason.
     * @param commandType type of command that generated the response.
     * @return a styled, left-aligned Jason dialog box.
     */
    public static DialogBox getJasonDialog(String message, String commandType) {
        DialogBox dialogBox = getJasonDialog(message);
        if (commandType != null) {
            dialogBox.getStyleClass().add(commandType.toLowerCase().replace("command", "-command"));
        }
        return dialogBox;
    }

    /**
     * Creates a Jason dialog box styled for an error response.
     *
     * @param message error message to display.
     * @return a highlighted error dialog box.
     */
    public static DialogBox getJasonErrorDialog(String message) {
        DialogBox dialogBox = getJasonDialog(message);
        dialogBox.getStyleClass().add("error-response");
        return dialogBox;
    }

    /** Places the avatar on the left to distinguish Jason's responses. */
    private void flip() {
        getChildren().setAll(avatar, text);
        setAlignment(Pos.TOP_LEFT);
    }
}
