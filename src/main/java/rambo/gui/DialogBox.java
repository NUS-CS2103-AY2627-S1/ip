package rambo.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A chat bubble containing a message and its speaker image.
 */
public class DialogBox extends HBox {
    private static final int IMAGE_SIZE = 56;
    private static final int MESSAGE_IMAGE_SPACING = 8;
    private static final double MESSAGE_MAX_WIDTH = 280.0;

    private final Label text;
    private final ImageView displayPicture;

    private DialogBox(String message, Image image) {
        text = new Label(message);
        text.setWrapText(true);
        text.setMaxWidth(MESSAGE_MAX_WIDTH);
        displayPicture = new ImageView(image);
        displayPicture.setFitWidth(IMAGE_SIZE);
        displayPicture.setFitHeight(IMAGE_SIZE);
        displayPicture.setPreserveRatio(true);
        getChildren().addAll(text, displayPicture);
        setAlignment(Pos.TOP_RIGHT);
        setSpacing(MESSAGE_IMAGE_SPACING);
    }

    /**
     * Creates a dialog box for messages entered by the user.
     *
     * @param message message text
     * @param image user display image
     * @return dialog box for a user message
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a dialog box for messages returned by Rambo.
     *
     * @param message message text
     * @param image Rambo display image
     * @return dialog box for a Rambo message
     */
    public static DialogBox getRamboDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("rambo-dialog");
        return dialogBox;
    }

    private void flip() {
        getChildren().setAll(displayPicture, text);
        setAlignment(Pos.TOP_LEFT);
    }
}
