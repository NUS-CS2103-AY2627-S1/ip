package penny.ui;

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

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a dialog box with the specified text and image.
     *
     * @param text The message string to display.
     * @param img The image of the speaker.
     */
    private DialogBox(String text, Image img) {
        assert text != null : "Dialog text should not be null";
        assert img != null : "Speaker avatar image should not be null";

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert dialog != null : "fx:id 'dialog' was not injected: check DialogBox.fxml";
        assert displayPicture != null : "fx:id 'displayPicture' was not injected: check DialogBox.fxml";

        dialog.setText(text);
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(30.0, 30.0, 30.0));
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

    /**
     * Returns a dialog box for the user.
     *
     * @param text The user's input text.
     * @param img The user avatar image.
     * @return A dialog box representing the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-label");
        return db;
    }

    /**
     * Returns a dialog box for Penny, flipped to place the avatar on the left.
     *
     * @param text Penny's response text.
     * @param img Penny's avatar image.
     * @return A flipped dialog box representing Penny.
     */
    public static DialogBox getPennyDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("penny-label");
        db.flip();
        return db;
    }
}
