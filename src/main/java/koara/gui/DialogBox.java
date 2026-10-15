package koara.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Displays a chat message beside an image of its speaker.
 */
public class DialogBox extends HBox {
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.72;
    private static final double KOARA_MESSAGE_WIDTH_RATIO = 0.88;
    private static final double AVATAR_RADIUS = 18.0;

    @FXML
    private Label dialogText;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the supplied message and speaker image.
     *
     * @param text Message to display.
     * @param image Image representing the speaker.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load a dialog box.", exception);
        }

        assert dialogText != null : "FXML loader must inject the dialog label";
        assert displayPicture != null : "FXML loader must inject the display picture";
        dialogText.setText(text);
        configureAvatar(image);
    }

    /**
     * Creates a dialog box for a message from the user.
     *
     * @param text Message to display.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.dialogText.getStyleClass().add("user-message");
        dialogBox.dialogText.maxWidthProperty().bind(
                dialogBox.widthProperty().multiply(USER_MESSAGE_WIDTH_RATIO));
        return dialogBox;
    }

    /**
     * Creates a dialog box for a response from Koara.
     *
     * @param text Response to display.
     * @param image Image representing Koara.
     * @return Koara dialog box.
     */
    public static DialogBox getKoaraDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.configureKoaraLayout("koara-message");
        return dialogBox;
    }

    /**
     * Creates a highlighted dialog box for an error response from Koara.
     *
     * @param text Error response to display.
     * @param image Image representing Koara.
     * @return Error dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.configureKoaraLayout("error-message");
        return dialogBox;
    }

    private void configureKoaraLayout(String messageStyleClass) {
        getStyleClass().add("koara-dialog");
        dialogText.getStyleClass().add(messageStyleClass);
        dialogText.maxWidthProperty().bind(
                widthProperty().multiply(KOARA_MESSAGE_WIDTH_RATIO));
        getChildren().setAll(displayPicture, dialogText);
        setAlignment(Pos.TOP_LEFT);
    }

    private void configureAvatar(Image image) {
        if (image == null) {
            displayPicture.setManaged(false);
            displayPicture.setVisible(false);
            return;
        }
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
    }
}
