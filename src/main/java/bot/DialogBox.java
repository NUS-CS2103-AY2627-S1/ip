package bot;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Displays one chat message together with its sender's avatar.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 36.0;
    private static final double MESSAGE_WIDTH_RATIO = 0.78;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing a message and an avatar.
     *
     * @param message message to display.
     * @param image avatar to display beside the message.
     */
    private DialogBox(String message, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load the dialog box layout.", error);
        }

        dialog.setText(message);
        dialog.maxWidthProperty().bind(widthProperty().multiply(MESSAGE_WIDTH_RATIO));
        configureAvatar(image);
    }

    /**
     * Creates a right-aligned dialog for a message sent by the user.
     *
     * @param message message sent by the user.
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message, null);
        dialogBox.dialog.getStyleClass().add("user-message");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a response sent by Bot.
     *
     * @param message response sent by Bot.
     * @param image avatar representing Bot.
     * @return Bot dialog box
     */
    public static DialogBox getBotDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a visually emphasized dialog for an error response from Bot.
     *
     * @param message error response sent by Bot.
     * @param image avatar representing Bot.
     * @return error dialog box
     */
    public static DialogBox getErrorDialog(String message, Image image) {
        DialogBox dialogBox = getBotDialog(message, image);
        dialogBox.dialog.getStyleClass().add("error-message");
        return dialogBox;
    }

    /**
     * Crops an avatar to a compact circle, or removes it from the layout when absent.
     *
     * @param image avatar to configure, or {@code null} for no avatar.
     */
    private void configureAvatar(Image image) {
        if (image == null) {
            displayPicture.setManaged(false);
            displayPicture.setVisible(false);
            return;
        }

        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double cropX = (image.getWidth() - cropSize) / 2;
        double cropY = (image.getHeight() - cropSize) / 2;
        displayPicture.setImage(image);
        displayPicture.setViewport(new Rectangle2D(cropX, cropY, cropSize, cropSize));
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setPreserveRatio(false);
        displayPicture.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
    }

    /**
     * Places the avatar on the left and the message on the right.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        dialog.getStyleClass().add("bot-message");
    }
}
