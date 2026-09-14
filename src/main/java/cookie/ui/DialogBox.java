package cookie.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
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

/** Represents one message and its speaker's avatar in the chat dialog. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 36.0;
    private static final double COOKIE_MESSAGE_RESERVED_WIDTH = 80.0;
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.74;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a right-aligned dialog box.
     *
     * @param message The message to display.
     */
    private DialogBox(String message) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        dialog.setText(message);
    }

    /** Configures a left-aligned Cookie response with a compact circular avatar. */
    private void configureCookieResponse(Image image) {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.maxWidthProperty().bind(Bindings.max(
                120.0, widthProperty().subtract(COOKIE_MESSAGE_RESERVED_WIDTH)));

        displayPicture.setImage(image);
        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double cropX = (image.getWidth() - cropSize) / 2;
        double cropY = (image.getHeight() - cropSize) / 2;
        displayPicture.setViewport(new Rectangle2D(cropX, cropY, cropSize, cropSize));
        displayPicture.setClip(new Circle(
                AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
    }

    /**
     * Creates a right-aligned dialog box for a user's message.
     *
     * @param message The user's message.
     * @return A right-aligned dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.displayPicture.setManaged(false);
        dialogBox.displayPicture.setVisible(false);
        dialogBox.dialog.maxWidthProperty().bind(
                dialogBox.widthProperty().multiply(USER_MESSAGE_WIDTH_RATIO));
        dialogBox.getStyleClass().add("user-message");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for Cookie's response.
     *
     * @param message Cookie's response.
     * @param image Cookie's avatar.
     * @return A left-aligned dialog box.
     */
    public static DialogBox getCookieDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.configureCookieResponse(image);
        dialogBox.getStyleClass().add("cookie-message");
        return dialogBox;
    }

    /**
     * Creates a visually prominent left-aligned error from Cookie.
     *
     * @param message The error message to display.
     * @param image Cookie's avatar.
     * @return A left-aligned error dialog box.
     */
    public static DialogBox getErrorDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.configureCookieResponse(image);
        dialogBox.getStyleClass().add("error-message");
        return dialogBox;
    }
}
