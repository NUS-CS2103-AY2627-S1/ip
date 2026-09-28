package baron.controller;

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
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final String USER_DIALOG_STYLE = "-fx-background-color: #f4a261;"
            + " -fx-background-radius: 15;"
            + " -fx-padding: 8 12 8 12;";
    private static final String BARON_DIALOG_STYLE = "-fx-background-color: #4a90e2;"
            + " -fx-background-radius: 15;"
            + " -fx-padding: 8 12 8 12;";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing the specified text and speaker image.
     *
     * @param text The message to display.
     * @param image The image representing the speaker.
     * @param dialogStyle The style for the speaker's message.
     */
    private DialogBox(String text, Image image, String dialogStyle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.setStyle(dialogStyle);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(28.0, 28.0, 28.0));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> childNodes = FXCollections.observableArrayList(getChildren());
        Collections.reverse(childNodes);
        getChildren().setAll(childNodes);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box for a message sent by the user.
     *
     * @param text The message to display.
     * @param image The image representing the user.
     * @return The user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image, USER_DIALOG_STYLE);
    }

    /**
     * Returns a dialog box for a message sent by Baron.
     *
     * @param text The message to display.
     * @param image The image representing Baron.
     * @return The Baron dialog box.
     */
    public static DialogBox getBaronDialog(String text, Image image) {
        DialogBox baronDialog = new DialogBox(text, image, BARON_DIALOG_STYLE);
        baronDialog.flip();
        return baronDialog;
    }
}
