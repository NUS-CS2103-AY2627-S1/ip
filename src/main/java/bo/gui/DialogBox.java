package bo.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Displays a message together with the image of its speaker.
 */
public class DialogBox extends HBox {
    private final Label messageLabel;
    private final ImageView speakerImage;

    private DialogBox(String message, Image image, boolean isUser) {
        messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add(isUser ? "user-label" : "reply-label");

        speakerImage = new ImageView(image);
        speakerImage.setFitHeight(52);
        speakerImage.setFitWidth(52);
        speakerImage.setPreserveRatio(true);

        getStyleClass().add(isUser ? "user-dialog" : "bo-dialog");
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        setSpacing(8);
        setPadding(new Insets(5));

        if (!isUser) {
            messageLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(messageLabel, Priority.ALWAYS);
        }

        getChildren().addAll(isUser ? messageLabel : speakerImage,
                isUser ? speakerImage : messageLabel);
    }

    /**
     * Creates a dialog spoken by the user.
     *
     * @param message message text
     * @param image user image
     * @return a user dialog box
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image, true);
    }

    /**
     * Creates a dialog spoken by Bo.
     *
     * @param message message text
     * @param image Bo image
     * @return a Bo dialog box
     */
    public static DialogBox getBoDialog(String message, Image image) {
        return new DialogBox(message, image, false);
    }

    /**
     * Creates an error dialog spoken by Bo.
     *
     * @param message error text
     * @param image Bo image
     * @return an error dialog box
     */
    public static DialogBox getErrorDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image, false);
        dialogBox.getStyleClass().add("error-dialog");
        dialogBox.messageLabel.getStyleClass().add("error-label");
        return dialogBox;
    }
}
