package zen.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
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

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 36;
    private static final double AVATAR_RADIUS = AVATAR_SIZE / 2;
    private static final double BUBBLE_MAX_WIDTH_RATIO = 0.85;
    private static final double BUBBLE_HORIZONTAL_GUTTER = 24;
    private static final double MINIMUM_BUBBLE_WIDTH = 160;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
        configureResponsiveBubbleWidth();
    }

    /** Configures the message bubble to wrap within the available dialog-row width. */
    private void configureResponsiveBubbleWidth() {
        DoubleBinding maximumBubbleWidth = (DoubleBinding) Bindings.min(
                widthProperty().multiply(BUBBLE_MAX_WIDTH_RATIO),
                widthProperty().subtract(displayPicture.fitWidthProperty()).subtract(BUBBLE_HORIZONTAL_GUTTER));
        dialog.setMinWidth(0);
        dialog.maxWidthProperty().bind(Bindings.max(MINIMUM_BUBBLE_WIDTH, maximumBubbleWidth));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box representing a user message.
     *
     * @param text message text
     * @param image user's display image
     * @return dialog box with the user message
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a dialog box representing a chatbot response.
     *
     * @param text response text
     * @param image chatbot's display image
     * @return dialog box with the chatbot response
     */
    public static DialogBox getZenDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }
}
