package yqr.gui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Displays one chat message together with a compact speaker avatar.
 */
public class DialogBox extends HBox {
    private static final double PROFILE_IMAGE_SIZE = 34;
    private static final Image DUKE_PROFILE_IMAGE = loadProfileImage();

    @FXML
    private Label dialog;
    @FXML
    private StackPane avatar;
    @FXML
    private ImageView avatarImage;
    @FXML
    private Label errorBadge;

    /**
     * Loads the reusable dialog layout and fills it with a message and Duke's profile image.
     *
     * @param text message to display.
     */
    private DialogBox(String text) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box", e);
        }

        dialog.setText(text);
        configureProfileImage();
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.76));
    }

    /**
     * Creates a right-aligned dialog for a user command.
     *
     * @param text command entered by the user.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.getChildren().remove(dialogBox.avatar);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a chatbot response.
     *
     * @param text response produced by yqr.
     * @return chatbot dialog box.
     */
    public static DialogBox getYqrDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.formatAsYqrDialog();
        return dialogBox;
    }

    /**
     * Creates a highlighted left-aligned dialog for an error response.
     *
     * @param text error response produced by yqr.
     * @return highlighted error dialog box.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.formatAsYqrDialog();
        dialogBox.dialog.getStyleClass().add("error-bubble");
        dialogBox.avatar.getStyleClass().add("error-avatar");
        dialogBox.errorBadge.setVisible(true);
        return dialogBox;
    }

    /** Loads, center-crops, and circularly clips Duke's bundled profile picture. */
    private void configureProfileImage() {
        double cropSize = Math.min(DUKE_PROFILE_IMAGE.getWidth(), DUKE_PROFILE_IMAGE.getHeight());
        double cropX = (DUKE_PROFILE_IMAGE.getWidth() - cropSize) / 2;
        double cropY = (DUKE_PROFILE_IMAGE.getHeight() - cropSize) / 2;
        avatarImage.setImage(DUKE_PROFILE_IMAGE);
        avatarImage.setViewport(new Rectangle2D(cropX, cropY, cropSize, cropSize));
        avatarImage.setClip(new Circle(
                PROFILE_IMAGE_SIZE / 2, PROFILE_IMAGE_SIZE / 2, PROFILE_IMAGE_SIZE / 2));
    }

    /** Loads Duke's profile picture once for reuse by every response dialog. */
    private static Image loadProfileImage() {
        URL profileResource = DialogBox.class.getResource("/view/duke-profile.jpg");
        if (profileResource == null) {
            throw new IllegalStateException("Duke profile picture is missing");
        }
        return new Image(profileResource.toExternalForm());
    }

    /** Places the avatar before the response and applies chatbot-specific styles. */
    private void formatAsYqrDialog() {
        getChildren().setAll(avatar, dialog);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("yqr-dialog");
        dialog.getStyleClass().add("yqr-bubble");
    }
}
