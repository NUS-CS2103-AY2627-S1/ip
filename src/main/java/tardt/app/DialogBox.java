package tardt.app;

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
    private static final int DEFAULT_COLOUR_INDEX = 1;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img, int colourIndex) {
        try {
            // FXML creates the label and image controls declared by this JavaFX component before they are configured.
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog layout.", exception);
        }

        dialog.setText(text.stripTrailing());
        displayPicture.setImage(img);
        getStyleClass().add("dialog-colour-" + colourIndex);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return getUserDialog(text, img, DEFAULT_COLOUR_INDEX);
    }

    /**
     * Creates a user dialog using the requested colour from the dialog palette.
     *
     * @param text message to display
     * @param img image representing the user
     * @param colourIndex palette colour number, from 1 to 5
     * @return a dialog box containing the user's message
     */
    public static DialogBox getUserDialog(String text, Image img, int colourIndex) {
        return new DialogBox(text, img, colourIndex);
    }

    public static DialogBox getTardTDialog(String text, Image img) {
        return getTardTDialog(text, img, DEFAULT_COLOUR_INDEX);
    }

    /**
     * Creates a TardT dialog using the requested colour from the dialog palette.
     *
     * @param text message to display
     * @param img image representing TardT
     * @param colourIndex palette colour number, from 1 to 5
     * @return a flipped dialog box containing TardT's message
     */
    public static DialogBox getTardTDialog(String text, Image img, int colourIndex) {
        DialogBox dialogBox = new DialogBox(text, img, colourIndex);
        dialogBox.flip();
        return dialogBox;
    }
}
