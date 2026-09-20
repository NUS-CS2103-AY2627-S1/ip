package grower.gui;

import java.io.IOException;
import java.util.Collections;

import grower.ui.ResponseType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
    /** Label containing the message text. */
    @FXML
    private Label dialog;

    /** Image identifying the speaker of the message. */
    @FXML
    private ImageView displayPicture;

    /**
     * Loads the dialog-box layout and fills it with a message and speaker image.
     *
     * @param text Message displayed in the dialog box.
     * @param img Image identifying the speaker.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load a dialog box.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        // Crop the center to a square so the circular avatar does not stretch the photo.
        double side = Math.min(img.getWidth(), img.getHeight());
        double cropX = (img.getWidth() - side) / 2;
        double cropY = (img.getHeight() - side) / 2;
        displayPicture.setViewport(new Rectangle2D(cropX, cropY, side, side));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for a command entered by the user.
     *
     * @param text Command text.
     * @param img User's display image.
     * @return Dialog box aligned to the right.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for a response from Grower.
     *
     * @param text Response text.
     * @param img Grower's display image.
     * @return Dialog box aligned to the left.
     */
    public static DialogBox getGrowerDialog(String text, Image img) {
        return getGrowerDialog(text, img, ResponseType.NORMAL);
    }

    /**
     * Creates a Grower reply with a distinct alert color and a readable status heading.
     *
     * @param text Response text.
     * @param img Grower's display image.
     * @param type Attention level supplied by the application logic.
     * @return Dialog box aligned to the left and styled for its attention level.
     */
    public static DialogBox getGrowerDialog(String text, Image img, ResponseType type) {
        var dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        if (type == ResponseType.ERROR) {
            dialogBox.dialog.getStyleClass().add("error-label");
            dialogBox.dialog.setText("Error\n" + text);
        } else if (type == ResponseType.WARNING) {
            dialogBox.dialog.getStyleClass().add("warning-label");
            dialogBox.dialog.setText("Warning\n" + text);
        }
        return dialogBox;
    }
}
