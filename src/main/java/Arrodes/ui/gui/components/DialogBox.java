package arrodes.ui.gui.components;

import java.io.IOException;
import java.util.Collections;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/** Displays a message alongside a speaker's profile image with speaker-specific styling. */
public class DialogBox extends HBox {
    /** Default image size used while constructing a dialog box. */
    private static final double DEFAULT_PROFILE_SIZE = 42.0;
    /** Larger image size used to give Arrodes replies visual prominence. */
    private static final double ARRODES_PROFILE_SIZE = 56.0;
    /** Smaller image size used to keep user messages compact. */
    private static final double USER_PROFILE_SIZE = 32.0;
    /** Profile image displayed beside user messages. */
    private static final Image IMAGE_OF_USER = new Image(DialogBox.class.getResourceAsStream("/images/user.jpg"));
    /** Profile image displayed beside Arrodes messages. */
    private static final Image IMAGE_OF_ARRODES = new Image(DialogBox.class.getResourceAsStream("/images/arrodes.jpg"));

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the specified message and image.
     *
     * @param message message to display
     * @param image image to display alongside the message
     */
    public DialogBox(String message, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout", exception);
        }
        dialog.setText(message);
        displayPicture.setImage(image);
        setProfilePictureSize(DEFAULT_PROFILE_SIZE);
    }

    /**
     * Sets the profile image dimensions and keeps the crop circular.
     *
     * @param size diameter of the circular profile image in pixels
     */
    private void setProfilePictureSize(double size) {
        double radius = size / 2;
        displayPicture.setFitWidth(size);
        displayPicture.setFitHeight(size);
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for a user message.
     *
     * @param message message to display
     * @return dialog box containing the user message
     */
    public static DialogBox getUserDialogBox(String message) {
        DialogBox dialogBox = new DialogBox(message, IMAGE_OF_USER);
        dialogBox.setProfilePictureSize(USER_PROFILE_SIZE);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a dialog box for an Arrodes message.
     *
     * @param message message to display
     * @return dialog box containing the Arrodes message
     */
    public static DialogBox getArrodesDialogBox(String message) {
        DialogBox db = new DialogBox(message, IMAGE_OF_ARRODES);
        db.setProfilePictureSize(ARRODES_PROFILE_SIZE);
        db.flip();
        db.getStyleClass().add("arrodes-dialog");
        db.startTypewriterAnimation();
        return db;
    }

    /**
     * Reveals an Arrodes response one character at a time for a typewriter effect.
     *
     * <p>The response bubble is created before the animation starts so the user can
     * immediately see that Arrodes is preparing a reply.</p>
     */
    private void startTypewriterAnimation() {
        String message = dialog.getText();
        dialog.setText("");

        Timeline typewriter = new Timeline();
        for (int index = 0; index < message.length(); index++) {
            int characterIndex = index + 1;
            typewriter.getKeyFrames().add(new KeyFrame(Duration.millis(characterIndex * 14), event -> {
                dialog.setText(message.substring(0, characterIndex));
            }));
        }
        typewriter.play();
    }

    /**
     * Creates a visually prominent dialog box for an invalid request.
     *
     * @param message error message to display
     * @return dialog box containing the error message
     */
    public static DialogBox getErrorDialogBox(String message) {
        DialogBox dialogBox = new DialogBox(message, IMAGE_OF_ARRODES);
        dialogBox.setProfilePictureSize(ARRODES_PROFILE_SIZE);
        dialogBox.flip();
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
