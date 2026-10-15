package ace.gui;

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

/**
 * A single message bubble in the chat history: a text label paired with an
 * avatar image, laid out side by side. Its layout is defined in
 * {@code DialogBox.fxml}, loaded into this instance via the {@code fx:root}
 * pattern. Use {@link #getUserDialog(String, Image)} or
 * {@link #getAceDialog(String, Image, boolean)} to create one, rather than
 * the constructor directly, so user and Ace messages are styled consistently.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);

        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Reverses the order of this dialog box's children and left-aligns it,
     * so the avatar appears on the left instead of the right, and swaps in
     * the "reply-label" style so the bubble's color and corner rounding
     * visually distinguish Ace's replies from the user's own messages.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for a message the user sent.
     *
     * @param s the message text.
     * @param i the user's avatar image.
     * @return a right-aligned dialog box.
     */
    public static DialogBox getUserDialog(String s, Image i) {
        return new DialogBox(s, i);
    }

    /**
     * Creates a dialog box for one of Ace's responses.
     *
     * @param text the message text.
     * @param image Ace's avatar image.
     * @param isError true if this response is an error message, so it should
     *     be highlighted (red background, white text) to catch the user's
     *     attention, rather than styled like a normal Ace reply.
     * @return a left-aligned dialog box, visually distinct from the user's own messages.
     */
    public static DialogBox getAceDialog(String text, Image image, boolean isError) {
        var db = new DialogBox(text, image);
        db.flip();
        if (isError) {
            db.dialog.getStyleClass().add("error-label");
        }
        return db;
    }
}
