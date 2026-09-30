package kong.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
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
 * Represents one message and its speaker's avatar in the chat window.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_RADIUS = 20.0;
    private static final double MIN_BUBBLE_WIDTH = 240.0;
    private static final double MAX_BUBBLE_WIDTH = 640.0;
    private static final double BUBBLE_WIDTH_RATIO = 0.75;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /** Loads the FXML layout and supplies the message content. */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        assert dialog != null : "DialogBox.fxml must inject the dialog label";
        assert displayPicture != null : "DialogBox.fxml must inject the display picture";
        dialog.setText(text);
        displayPicture.setImage(image);
        applyCircularClip();
        bindResponsiveWidth();
    }

    /** Crops the avatar into a circular shape. */
    private void applyCircularClip() {
        Circle clip = new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS);
        displayPicture.setClip(clip);
    }

    /** Binds the dialog label's max width dynamically based on the container width. */
    private void bindResponsiveWidth() {
        dialog.maxWidthProperty().bind(Bindings.createDoubleBinding(
                this::computeMaxBubbleWidth, widthProperty()));
    }

    /** Computes the dynamic maximum width for message bubbles based on dialog box width. */
    private double computeMaxBubbleWidth() {
        return Math.min(Math.max(getWidth() * BUBBLE_WIDTH_RATIO, MIN_BUBBLE_WIDTH), MAX_BUBBLE_WIDTH);
    }

    /** Places Kong's avatar on the left and styles the bubble as a reply or error. */
    private void flip(boolean isError) {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().remove("dialog-label");
        if (isError) {
            dialog.getStyleClass().add("error-label");
        } else {
            dialog.getStyleClass().add("reply-label");
        }
    }

    /**
     * Creates a right-aligned dialog for a user's command.
     *
     * @param text command text
     * @param image user's avatar
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog for Kong's response.
     *
     * @param text response text
     * @param image Kong's avatar
     * @return Kong dialog box
     */
    public static DialogBox getKongDialog(String text, Image image) {
        return getKongDialog(text, image, false);
    }

    /**
     * Creates a left-aligned dialog for Kong's response, styled for errors when specified.
     *
     * @param text response text
     * @param image Kong's avatar
     * @param isError whether the response represents an error message
     * @return Kong dialog box
     */
    public static DialogBox getKongDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip(isError);
        return dialogBox;
    }
}
