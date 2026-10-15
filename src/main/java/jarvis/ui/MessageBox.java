package jarvis.ui;

import java.io.IOException;
import java.util.Collections;

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
import javafx.scene.shape.Rectangle;

/**
 * Represents a message box consisting of an ImageView
 * and a Label containing the message text.
 */
public class MessageBox extends HBox {

    /** Represents all possible message types. */
    public enum MessageType {
        USER,
        JARVIS,
        ERROR
    }

    @FXML
    private Label textLabel;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a message box.
     *
     * @param message message text
     * @param image speaker image
     * @param messageType the message type
     */
    public MessageBox(String message, Image image, MessageType messageType) {

        if (message == null) {
            throw new IllegalArgumentException("Message must not be null.");
        }

        if (messageType == null) {
            throw new IllegalArgumentException("Message type must not be null.");
        }

        FXMLLoader loader = new FXMLLoader(
                MessageBox.class.getResource("/view/MessageBox.fxml")
        );

        if (loader.getLocation() == null) {
            throw new IllegalStateException(
                    "Unable to find /view/MessageBox.fxml."
            );
        }

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load MessageBox.fxml", e
            );
        }

        textLabel.setText(message);
        displayPicture.setImage(image);

        switch (messageType) {
            case USER:
                getStyleClass().add("user-message");
                clipAsCircle();
                break;

            case JARVIS:
                getStyleClass().add("jarvis-message");
                clipAsRoundedRectangle();
                flip();
                break;

            case ERROR:
                getStyleClass().add("error-message");
                clipAsRoundedRectangle();
                flip();
                break;

            default:
                throw new IllegalArgumentException(
                        "Unknown message type: " + messageType
                );
        }
    }

    /**
     * Clips the profile image into a circle.
     */
    private void clipAsCircle() {
        double width = displayPicture.getFitWidth();
        double height = displayPicture.getFitHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        double radius = Math.min(width, height) / 2.0;

        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);
    }

    /**
     * Clips the image into a rounded rectangle.
     */
    private void clipAsRoundedRectangle() {
        double width = displayPicture.getFitWidth();
        double height = displayPicture.getFitHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(15);
        clip.setArcHeight(15);

        displayPicture.setClip(clip);
    }

    /**
     * Flips the message box so that the image is on the left
     * and the message is on the right.
     */
    private void flip() {

        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());

        Collections.reverse(children);

        getChildren().setAll(children);

        setAlignment(Pos.CENTER_LEFT);

        textLabel.getStyleClass().add("reply-label");
    }
}
