package jimbo.gui;

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
 * A single chat bubble: a message {@link Label} paired with a speaker
 * avatar. User messages and Jimbo's replies use the same layout, mirrored
 * via {@link #flip()} so the avatar sits on the opposite side.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        setAlignment(Pos.TOP_RIGHT);
    }

    /**
     * Reverses the order of this box's children and left-aligns them, so
     * the avatar appears on the left instead of the right. Used to
     * visually distinguish Jimbo's replies from the user's messages.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for a message typed by the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for a reply from Jimbo, flipped so its avatar
     * appears on the left. {@code isError} styles the bubble as an error
     * (see {@code error-label} in {@code dialog-box.css}) instead of a
     * normal reply, so mistakes catch the user's eye.
     */
    public static DialogBox getJimboDialog(String text, Image img, boolean isError) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        if (isError) {
            db.dialog.getStyleClass().add("error-label");
        }
        return db;
    }
}
