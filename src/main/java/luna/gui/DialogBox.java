package luna.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Represents a dialog box containing the speaker's face and chat message.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 40;
    private static final double BOT_MESSAGE_HORIZONTAL_SPACE = 76;
    private static final double USER_MESSAGE_HORIZONTAL_SPACE = 76;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    @FXML
    private StackPane displayPictureFrame;

    /**
     * Creates a dialog box representing user input or Luna's response.
     *
     * @param text Message to display.
     * @param image Image representing the speaker, or {@code null} when no image is shown.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog-box layout.", e);
        }

        dialog.setText(text);
        configureDisplayPicture(image);
    }

    /**
     * Configures an optional rounded-square display picture.
     *
     * @param image Image to display, or {@code null} to hide the display-picture space.
     */
    private void configureDisplayPicture(Image image) {
        boolean hasImage = image != null;
        displayPictureFrame.setManaged(hasImage);
        displayPictureFrame.setVisible(hasImage);

        if (hasImage) {
            displayPicture.setImage(image);
            Rectangle roundedClip = new Rectangle(AVATAR_SIZE, AVATAR_SIZE);
            roundedClip.setArcWidth(14);
            roundedClip.setArcHeight(14);
            displayPicture.setClip(roundedClip);
        }
    }

    /**
     * Creates a dialog box representing a user message.
     *
     * @param text User's message.
     * @param image Avatar image for the user.
     * @return A {@code DialogBox} styled as a user dialog.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getChildren().setAll(dialogBox.dialog, dialogBox.displayPictureFrame);
        dialogBox.dialog.maxWidthProperty().bind(
                dialogBox.widthProperty().subtract(USER_MESSAGE_HORIZONTAL_SPACE));
        return dialogBox;
    }

    /**
     * Creates a dialog box representing Luna's response.
     *
     * @param text Message text to display.
     * @param image Avatar image for Luna.
     * @return A {@code DialogBox} styled as a Luna dialog.
     */
    public static DialogBox getLunaDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("luna-dialog");
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.dialog.maxWidthProperty().bind(
                dialogBox.widthProperty().subtract(BOT_MESSAGE_HORIZONTAL_SPACE));
        HBox.setHgrow(dialogBox.dialog, Priority.ALWAYS);
        return dialogBox;
    }
}
