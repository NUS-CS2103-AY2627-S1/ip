package laby;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** Represents one chat message with a circular profile picture. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 48.0;

    private final Label text;
    private final ImageView displayPicture;

    /**
     * Creates a chat message with a profile picture.
     *
     * @param message Message to display.
     * @param image Profile picture to display.
     */
    public DialogBox(String message, Image image) {
        text = new Label(message);
        displayPicture = new ImageView(image);

        text.setWrapText(true);
        text.setMaxWidth(520.0);
        text.getStyleClass().add("chat-label");

        cropToSquare(image);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setPreserveRatio(true);
        displayPicture.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2,
                AVATAR_SIZE / 2));
        displayPicture.getStyleClass().add("avatar-image");

        setSpacing(10.0);
        setPadding(new Insets(8.0, 12.0, 8.0, 12.0));
        getStyleClass().add("dialog");
        getChildren().addAll(text, displayPicture);
    }

    /**
     * Creates a dialog aligned as a user message.
     *
     * @param message User message to display.
     * @param image User profile picture.
     * @return User message dialog.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a dialog aligned as a Laby message.
     *
     * @param message Laby message to display.
     * @param image Laby profile picture.
     * @return Laby message dialog.
     */
    public static DialogBox getLabyDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("laby-dialog");
        if (isErrorMessage(message)) {
            dialogBox.getStyleClass().add("error-dialog");
        }
        return dialogBox;
    }

    /** Places the blank placeholder before the message for left-aligned dialogs. */
    private void flip() {
        getChildren().setAll(displayPicture, text);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Configures the image view to display the centered square portion of an image.
     *
     * @param image Image whose centered square portion should be displayed.
     */
    private void cropToSquare(Image image) {
        double side = Math.min(image.getWidth(), image.getHeight());
        double x = (image.getWidth() - side) / 2;
        double y = (image.getHeight() - side) / 2;
        displayPicture.setViewport(new Rectangle2D(x, y, side, side));
    }

    /**
     * Checks whether a message uses the application's standard error prefix.
     *
     * @param message Message to inspect.
     * @return True if the message is an application error message.
     */
    private static boolean isErrorMessage(String message) {
        String trimmedMessage = message.trim();
        return trimmedMessage.regionMatches(true, 0, Ui.MSG_ERROR, 0,
                Ui.MSG_ERROR.length());
    }
}
