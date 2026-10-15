package am.ui;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Represents one message in the conversation, loaded from a reusable FXML view.
 */
public class DialogBox extends HBox {
    private static final String AM_AVATAR_IMAGE_PATH = "/images/am-avatar.png";
    private static final String USER_AVATAR = "Y";
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.8;

    @FXML
    private Label dialog;

    @FXML
    private ImageView avatarImage;

    @FXML
    private Label avatarLabel;

    /**
     * Creates a message box and loads its reusable FXML layout.
     *
     * @param message message text to display
     * @param isUser whether the message was entered by the user
     */
    private DialogBox(String message, boolean isUser) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box view.", exception);
        }

        dialog.setText(message);
        avatarLabel.setText(USER_AVATAR);
        avatarLabel.setVisible(isUser);
        avatarLabel.setManaged(isUser);
        avatarImage.setVisible(!isUser);
        avatarImage.setManaged(!isUser);
        if (!isUser) {
            avatarImage.setImage(loadAmAvatar());
        }
        getStyleClass().add(isUser ? "user-dialog" : "am-dialog");

        if (isUser) {
            // Leave room for the avatar and an empty margin beside long user commands.
            dialog.maxWidthProperty().bind(Bindings.createDoubleBinding(() -> Math.max(0,
                    getWidth() - getInsets().getLeft() - getInsets().getRight()
                            - avatarLabel.getWidth() - getSpacing()) * USER_MESSAGE_WIDTH_RATIO,
                    widthProperty(), insetsProperty(), avatarLabel.widthProperty(), spacingProperty()));
            flip();
        } else {
            dialog.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(dialog, Priority.ALWAYS);
        }
    }

    /**
     * Creates a message box containing user input.
     *
     * @param message user input to display
     * @return a right-aligned user message box
     */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox(message, true);
    }

    /**
     * Creates a message box containing AM's response.
     *
     * @param message response to display
     * @return a left-aligned AM message box
     */
    public static DialogBox getAmDialog(String message) {
        return new DialogBox(message, false);
    }

    /**
     * Creates an error response with a text heading and distinct visual styling.
     *
     * @param message error explanation to display
     * @return a left-aligned AM error message box
     */
    public static DialogBox getErrorDialog(String message) {
        DialogBox dialogBox = new DialogBox("Error\n" + message, false);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    /**
     * Loads the supplied profile picture for AM.
     *
     * @return AM's profile picture
     */
    private static Image loadAmAvatar() {
        URL imageUrl = DialogBox.class.getResource(AM_AVATAR_IMAGE_PATH);
        if (imageUrl == null) {
            throw new IllegalStateException("Unable to find AM's profile picture.");
        }
        return new Image(imageUrl.toExternalForm());
    }

    /** Reverses the child order and aligns the user message to the right. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_RIGHT);
    }
}
