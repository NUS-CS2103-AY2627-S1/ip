package adrian;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Displays one chat message beside the sender's avatar.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 38.0;

    private final Label message;
    private final ImageView avatar;

    /**
     * Creates a dialog box containing a message and avatar.
     *
     * @param text message to display.
     * @param image avatar belonging to the sender.
     */
    private DialogBox(String text, Image image) {
        message = new Label(text);
        message.setWrapText(true);

        avatar = new ImageView(image);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(false);
        avatar.setSmooth(true);
        avatar.setClip(new Circle(
                AVATAR_SIZE / 2,
                AVATAR_SIZE / 2,
                AVATAR_SIZE / 2));
        avatar.getStyleClass().add("avatar");

        setMaxWidth(Double.MAX_VALUE);
        setPadding(new Insets(3.0, 0.0, 3.0, 0.0));
        setSpacing(8.0);
    }

    /**
     * Creates a right-aligned dialog box for a message from the user.
     *
     * @param text user message to display.
     * @param image Rocky avatar representing the user.
     * @return dialog box for the user message.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.message.getStyleClass().addAll("dialog-label", "user-dialog");
        dialogBox.message.maxWidthProperty().bind(dialogBox.widthProperty().multiply(0.68));
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getChildren().addAll(dialogBox.message, dialogBox.avatar);
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for a message from Adrian.
     *
     * @param text Adrian message to display.
     * @param image planet avatar representing Adrian.
     * @return dialog box for the Adrian message.
     */
    public static DialogBox getAdrianDialog(String text, Image image) {
        return getAdrianDialog(text, image, false);
    }

    /**
     * Creates a visually highlighted dialog box for an error reported by Adrian.
     *
     * @param text error message to display.
     * @param image planet avatar representing Adrian.
     * @return highlighted dialog box for the error message.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        return getAdrianDialog(text, image, true);
    }

    /**
     * Creates Adrian's asymmetric response card, optionally styled as an error.
     *
     * @param text Adrian message to display.
     * @param image planet avatar representing Adrian.
     * @param isError whether the response reports an error.
     * @return dialog box for Adrian's response.
     */
    private static DialogBox getAdrianDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        Label sender = new Label(isError ? "ADRIAN  /  ERROR" : "ADRIAN");
        sender.getStyleClass().add("sender-label");

        VBox responseCard = new VBox(4.0, sender, dialogBox.message);
        responseCard.getStyleClass().add(isError ? "error-card" : "adrian-card");
        responseCard.maxWidthProperty().bind(dialogBox.widthProperty().multiply(0.78));

        dialogBox.message.setMaxWidth(Double.MAX_VALUE);
        dialogBox.message.getStyleClass().addAll(
                "dialog-label",
                isError ? "error-dialog" : "adrian-dialog");
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.getChildren().addAll(dialogBox.avatar, responseCard);
        return dialogBox;
    }
}
