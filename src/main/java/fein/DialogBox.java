package fein;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** Represents one user or Fein message in the chat history. */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML = "/view/DialogBox.fxml";
    private static final double AVATAR_SIZE = 56;
    private static final double AVATAR_CROP_SCALE = 0.88;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /** Creates a dialog box containing the supplied message and optional avatar. */
    public DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(DIALOG_BOX_FXML));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout", exception);
        }

        // These controls are required by DialogBox.fxml for every message to be displayed.
        assert dialog != null && displayPicture != null : "DialogBox.fxml must inject its message controls";
        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.78));
        displayPicture.setImage(image);
        if (image == null) {
            displayPicture.setManaged(false);
            displayPicture.setVisible(false);
        } else {
            configureAvatar(image);
        }
    }

    /** Returns a dialog box aligned as a user message. */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.dialog.getStyleClass().add("user-message");
        return dialogBox;
    }

    /** Returns a dialog box aligned as a Fein response. */
    public static DialogBox getFeinDialog(String text, Image image, String commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    /** Aligns Fein's response on the left side of the conversation. */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("fein-message");
    }

    /** Crops the avatar slightly above centre and displays it as a circle without stretching the image. */
    private void configureAvatar(Image image) {
        double cropSize = Math.min(image.getWidth(), image.getHeight()) * AVATAR_CROP_SCALE;
        double cropX = (image.getWidth() - cropSize) / 2;
        double cropY = Math.max(0, (image.getHeight() - cropSize) / 2 - cropSize * 0.12);
        double radius = AVATAR_SIZE / 2;
        displayPicture.setViewport(new Rectangle2D(cropX, cropY, cropSize, cropSize));
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /** Applies a subtle, recovery-focused style to an invalid command response. */
    private void changeDialogStyle(String commandType) {
        // Command types are assigned by Fein before a response reaches the UI.
        assert commandType != null : "A response must have a command type for styling";
        if (commandType.equals("error")) {
            dialog.getStyleClass().add("error-message");
        }
    }
}
