package erina.gui;

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
 * One line of the conversation: a picture beside the text that was said.
 *
 * <p>The user's dialog boxes read left to right, and Erina's are flipped so
 * the two speakers are easy to tell apart at a glance. Problems Erina reports
 * get a style of their own, so they are not mistaken for ordinary replies.
 *
 * <p>Adapted from the {@code DialogBox} class in the se-education.org JavaFX
 * tutorial: https://se-education.org/guides/tutorials/javaFx.html
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
            // The FXML is packaged with the application, so failing to load it
            // means the build is broken rather than anything the user did.
            System.err.println("Could not load a dialog box: " + e.getMessage());
        }

        // Both controls are declared with fx:id in DialogBox.fxml, so a null
        // here means the FXML and this class have drifted apart.
        assert dialog != null && displayPicture != null : "DialogBox.fxml did not inject its controls";

        dialog.setText(text);
        displayPicture.setImage(img);

        // A circular avatar reads as a portrait rather than a pasted square.
        displayPicture.setClip(new Circle(24.0, 24.0, 24.0));
    }

    /** Puts the picture on the left and the text on the right. */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box showing something the user said.
     *
     * @param text what the user typed
     * @param img  the user's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Returns a dialog box showing something Erina said.
     *
     * @param text what Erina replied
     * @param img  Erina's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getErinaDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Returns a dialog box showing a problem Erina is reporting, such as a
     * mistyped command, highlighted so that the user notices it.
     *
     * @param text what went wrong, as Erina explains it
     * @param img  Erina's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getErinaErrorDialog(String text, Image img) {
        DialogBox db = getErinaDialog(text, img);
        db.dialog.getStyleClass().add("error-label");
        return db;
    }
}
