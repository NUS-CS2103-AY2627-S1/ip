package altair.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
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

/**
 * One entry in the conversation: a text bubble next to a round avatar.
 *
 * <p>This is a reusable custom control. Its layout lives in
 * {@code DialogBox.fxml}; the constructor loads that file with itself as both
 * the controller and the root, so a {@code DialogBox} can be added straight
 * into the window like any other node.</p>
 */
public class DialogBox extends HBox {

    /** How much of the conversation list's width a bubble may use. */
    private static final double BUBBLE_WIDTH_RATIO = 0.72;

    /** The bubble text. */
    @FXML
    private Label dialog;

    /** The speaker's avatar. */
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box showing the given text and avatar.
     *
     * @param text the message to display.
     * @param image the speaker's avatar.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load a dialog box.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Moves the avatar to the left and left-aligns the bubble, so replies from
     * Altair are visually distinct from the user's messages.
     */
    private void flip() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(getChildren());
        Collections.reverse(nodes);
        getChildren().setAll(nodes);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Binds this bubble's maximum width to a proportion of the conversation
     * list's width, so it uses more of a wider window instead of clipping at
     * a fixed pixel width.
     *
     * @param containerWidth the width property of the list this bubble sits in.
     */
    public void bindBubbleWidth(ObservableDoubleValue containerWidth) {
        dialog.maxWidthProperty().bind(Bindings.multiply(containerWidth, BUBBLE_WIDTH_RATIO));
    }

    /**
     * Creates a right-aligned dialog box for something the user typed.
     *
     * <p>The conversation is between the user and the app, not two humans, so
     * only Altair keeps an avatar; the user's bubble is shown on its own.</p>
     *
     * @param text the user's message.
     * @param image the user's avatar; loaded but not shown.
     * @return the dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.dialog.getStyleClass().add("bubble-user");
        box.displayPicture.setVisible(false);
        box.displayPicture.setManaged(false);
        return box;
    }

    /**
     * Creates a left-aligned dialog box for a reply from Altair.
     *
     * @param text Altair's reply.
     * @param image Altair's avatar.
     * @return the dialog box.
     */
    public static DialogBox getAltairDialog(String text, Image image) {
        return getAltairDialog(text, image, false);
    }

    /**
     * Creates a left-aligned dialog box for a reply from Altair, optionally
     * flagged as an error so it can be styled differently.
     *
     * @param text Altair's reply.
     * @param image Altair's avatar.
     * @param isError whether the reply is an error message.
     * @return the dialog box.
     */
    public static DialogBox getAltairDialog(String text, Image image, boolean isError) {
        DialogBox box = new DialogBox(text, image);
        box.flip();
        box.dialog.getStyleClass().add(isError ? "bubble-error" : "bubble-altair");
        return box;
    }
}
