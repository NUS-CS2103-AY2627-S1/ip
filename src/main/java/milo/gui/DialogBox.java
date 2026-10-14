package milo.gui;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays one chat message with its sender's name and optional avatar.
 */
public class DialogBox extends HBox {
    private static final String MILO_AVATAR_PATH = "/images/milo-avatar.png";
    private static final Image MILO_AVATAR = new Image(Objects.requireNonNull(
            DialogBox.class.getResourceAsStream(MILO_AVATAR_PATH),
            "Missing Milo avatar: " + MILO_AVATAR_PATH));

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Label senderName;
    @FXML
    private VBox messageContent;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
    }

    /**
     * Creates a right-aligned dialog for the user's input.
     *
     * @param text user's input.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.dialog.getStyleClass().add("user-bubble");
        dialogBox.senderName.getStyleClass().add("user-name");
        dialogBox.senderName.setText("You");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Milo's response.
     *
     * @param text Milo's response.
     * @return Milo dialog box.
     */
    public static DialogBox getMiloDialog(String text) {
        return getMiloDialog(text, "milo-bubble");
    }

    /**
     * Creates a visually distinct response for a recoverable command error.
     *
     * @param text error response to show.
     * @return error dialog box.
     */
    public static DialogBox getErrorDialog(String text) {
        return getMiloDialog(text, "error-bubble");
    }

    /** Creates a left-aligned Milo dialog with the specified bubble style. */
    private static DialogBox getMiloDialog(String text, String bubbleStyleClass) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.dialog.getStyleClass().add(bubbleStyleClass);
        dialogBox.senderName.getStyleClass().add("milo-name");
        dialogBox.senderName.setText("Milo");
        dialogBox.displayPicture.setImage(MILO_AVATAR);
        dialogBox.displayPicture.setManaged(true);
        dialogBox.displayPicture.setVisible(true);
        dialogBox.alignLeft();
        return dialogBox;
    }

    /** Aligns Milo's responses with the left edge of the conversation. */
    private void alignLeft() {
        setAlignment(Pos.TOP_LEFT);
        messageContent.setAlignment(Pos.TOP_LEFT);
    }
}
