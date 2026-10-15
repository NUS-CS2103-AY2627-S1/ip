package hu9o.gui;

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

/**
 * A single chat bubble: the speaker's picture beside a wrapped text label.
 *
 * <p>The layout lives in {@code DialogBox.fxml} and is loaded with the
 * {@code fx:root} technique, so one object is both the root {@link HBox} and its
 * own controller. The static factory methods produce the three kinds of bubble
 * used by the GUI: the user's line, one of Hu9o's replies, and an error reply.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Loads the shared layout, then fills in this bubble's text and picture.
     *
     * @param text  the message to show.
     * @param image the speaker's picture.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Returns a right-aligned bubble for something the user typed.
     *
     * @param text  the user's message.
     * @param image the user's picture.
     * @return the dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a left-aligned bubble for one of Hu9o's replies.
     *
     * @param text  Hu9o's reply.
     * @param image Hu9o's picture.
     * @return the dialog box.
     */
    public static DialogBox getHu9oDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.flip();
        return box;
    }

    /**
     * Returns a left-aligned bubble showing the animated typing indicator,
     * displayed while Hu9o's real reply is being prepared and then discarded.
     *
     * @param typingImage the animated image to show while "typing".
     * @return the dialog box.
     */
    public static DialogBox getTypingDialog(Image typingImage) {
        DialogBox box = new DialogBox("", typingImage);
        box.flip();
        return box;
    }

    /**
     * Returns a left-aligned bubble for an error reply, styled to stand out.
     *
     * @param text  the error message.
     * @param image Hu9o's picture.
     * @return the dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.flip();
        box.dialog.getStyleClass().add("error-label");
        return box;
    }

    /**
     * Flips the bubble so the picture is on the left and the text on the right,
     * and restyles the label as a reply.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        this.getChildren().setAll(children);
        this.setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }
}
