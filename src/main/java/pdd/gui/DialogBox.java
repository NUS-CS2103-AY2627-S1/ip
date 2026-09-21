package pdd.gui;

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
 * A message bubble showing one line of dialog next to a speaker's avatar.
 * User messages are right-aligned; PDD's own messages are left-aligned via
 * {@link #flip()}, which reverses the child order and alignment.
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
            throw new IllegalStateException("Failed to load DialogBox.fxml", e);
        }
        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /** Reverses this box's children and left-aligns it, for messages from the other speaker. */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /** Creates a right-aligned dialog box for a message the user typed. */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a left-aligned dialog box for a message from PDD, styled as an
     * error bubble instead of a normal reply when {@code isError} is true.
     */
    public static DialogBox getPddDialog(String text, Image img, boolean isError) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.dialog.getStyleClass().add(isError ? "error-dialog" : "bot-dialog");
        return db;
    }
}
