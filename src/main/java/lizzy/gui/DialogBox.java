package lizzy.gui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Displays an avatar and one wrapped chat message.
 */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML = "/view/DialogBox.fxml";
    private static final String JANE_AVATAR = "/images/jane-avatar.png";
    private static final String LIZZY_AVATAR = "/images/lizzy-avatar.png";

    @FXML
    private ImageView avatar;
    @FXML
    private VBox contentPane;
    @FXML
    private Label dialog;

    /**
     * Loads the reusable dialog layout and fills it with one message.
     *
     * @param text the message to display
     * @param avatarPath the classpath location of the speaker's avatar
     */
    private DialogBox(String text, String avatarPath) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource(DIALOG_BOX_FXML));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout", exception);
        }
        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.72));
        setAvatar(avatarPath);
    }

    /**
     * Loads the avatar displayed beside a message.
     *
     * @param avatarPath the classpath location of the avatar image
     */
    private void setAvatar(String avatarPath) {
        URL avatarUrl = DialogBox.class.getResource(avatarPath);
        if (avatarUrl == null) {
            throw new IllegalStateException("Unable to load avatar: " + avatarPath);
        }
        avatar.setImage(new Image(avatarUrl.toExternalForm()));
        avatar.setClip(new Circle(24.0, 24.0, 24.0));
    }

    /**
     * Creates a right-aligned dialog for a user command.
     *
     * @param text the command entered by the user
     * @return a dialog styled for the user
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, JANE_AVATAR);
        dialogBox.getChildren().remove(dialogBox.avatar);
        dialogBox.getChildren().add(dialogBox.avatar);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.contentPane.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a chatbot response.
     *
     * @param text Lizzy's response
     * @return a dialog styled for Lizzy
     */
    public static DialogBox getLizzyDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, LIZZY_AVATAR);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.getStyleClass().add("lizzy-dialog");
        return dialogBox;
    }

    /**
     * Creates a visually prominent dialog for an invalid command.
     *
     * @param text Lizzy's explanation of the invalid command
     * @return a dialog styled as an error
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getLizzyDialog(text);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
