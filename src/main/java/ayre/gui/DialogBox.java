package ayre.gui;

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
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final double DIALOG_HORIZONTAL_OVERHEAD = 74;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Initializes the fxmlLoader for a new DialogBox to display the text and image.
     *
     * @param text Text to be displayed.
     * @param img Profile icon.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace(); // legit cannot be bothered to deal with this
        }

        dialog.setText(text);
        // Short messages use their natural width. Longer messages wrap to the
        // available space, which is recalculated whenever the window is resized.
        dialog.setMaxWidth(330);
        widthProperty().addListener((obs, oldWidth, newWidth) ->
                dialog.setMaxWidth(Math.max(0, newWidth.doubleValue() - DIALOG_HORIZONTAL_OVERHEAD)));
        displayPicture.setImage(img);
    }

    /**
     * Applies the style used for a user-authored message.
     */
    private void styleAsUserMessage() {
        dialog.getStyleClass().add("user-label");
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Applies the style used for an Ayre error message.
     */
    private void styleAsErrorMessage() {
        dialog.getStyleClass().add("error-label");
    }

    /**
     * Creates a new DialogBox to echo user input.
     *
     * @param text User input.
     * @param img User profile icon.
     * @return DialogBox.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.styleAsUserMessage();
        return db;
    }

    /**
     * Creates a new DialogBox to display Ayre dialog or reply
     *
     * @param text Ayre message
     * @param img Ayre profile icon
     * @return DialogBox
     */
    public static DialogBox getAyreDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Creates a new Ayre dialog box styled to highlight an error message.
     *
     * @param text Error message from Ayre.
     * @param img Ayre profile icon.
     * @return Error-styled Ayre dialog box.
     */
    public static DialogBox getAyreErrorDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        db.styleAsErrorMessage();
        return db;
    }
}
