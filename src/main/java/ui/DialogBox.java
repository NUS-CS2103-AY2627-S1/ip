package ui;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a chat bubble containing a dialog label and a display picture,
 * used to render user and bot messages in the chat window.
 */
public class DialogBox extends HBox {
    @FXML private Label dialog;
    @FXML private ImageView displayPicture;

    /**
     * Constructs a DialogBox loaded from FXML, displaying the given text and image.
     *
     * @param text the message text to display.
     * @param img the display picture (avatar) to show alongside the text.
     */
    private DialogBox(String text, Image img) {
        assert text != null : "text null";
        assert img != null : "img null";

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert dialog != null : "dialog Label not injected -> FXML load failed";
        assert displayPicture != null : "displayPicture ImageView not injected -> FXML load failed";

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box so the picture appears on the left and text on the right,
     * used to visually distinguish bot messages from user messages.
     */
    private void flip() {
        assert !getChildren().isEmpty() : "no children -> flip invalid";
        setAlignment(Pos.TOP_LEFT);
        FXCollections.reverse(getChildren());
        assert getChildren().size() == 2 : "child count changed after flip";
    }

    /**
     * Creates a dialog box representing a message from the user.
     *
     * @param text the message text.
     * @param img the user's display picture.
     * @return the constructed DialogBox.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        assert text != null : "text null";
        assert img != null : "img null";
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-bubble");
        return db;
    }

    /**
     * Creates a dialog box representing a message from Remy, flipped to appear
     * on the opposite side from user messages.
     *
     * @param text the message text.
     * @param img Remy's display picture.
     * @return the constructed, flipped DialogBox.
     */
    public static DialogBox getRemyDialog(String text, Image img) {
        assert text != null : "text null";
        assert img != null : "img null";
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
