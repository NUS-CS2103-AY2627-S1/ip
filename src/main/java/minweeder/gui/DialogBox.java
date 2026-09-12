package minweeder.gui;

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
 * A dialog bubble pairing a speaker's avatar with a label of their message.
 * User dialogs and Minweeder's dialogs are built from the same FXML layout,
 * mirrored left/right by {@link #flip()} so replies read as coming from the
 * other side of the conversation.
 */
public class DialogBox extends HBox {
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
            // The FXML layout is a bundled resource, not user input, so a failure here means
            // the jar is broken rather than something the user or caller can recover from.
            throw new IllegalStateException("Failed to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);

        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Mirrors the dialog box so the avatar sits on the left and the text on the
     * right, and switches the label to the "reply" style.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for something the user typed.
     *
     * @param text the user's input.
     * @param img the user's avatar.
     * @return the dialog box.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for Minweeder's response, flipped to the opposite side.
     *
     * @param text Minweeder's response.
     * @param img Minweeder's avatar.
     * @return the dialog box.
     */
    public static DialogBox getMinweederDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Creates a dialog box for an error response from Minweeder, flipped like a
     * normal reply but styled to catch the user's attention.
     *
     * @param text the error message.
     * @param img Minweeder's avatar.
     * @return the dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        db.dialog.getStyleClass().add("error-label");
        return db;
    }
}
