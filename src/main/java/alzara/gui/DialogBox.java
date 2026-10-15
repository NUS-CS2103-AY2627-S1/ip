package alzara.gui;

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
 * A dialog box consisting of an {@link ImageView} for the speaker's face and a
 * {@link Label} containing the speaker's text.
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
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load DialogBox.fxml", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        clipToCircle();
    }

    /**
     * Clips the avatar {@link ImageView} to a circle so it blends into the
     * bubble instead of showing as a hard-edged square thumbnail.
     */
    private void clipToCircle() {
        double radius = displayPicture.getFitWidth() / 2;
        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);
    }

    /**
     * Flips the dialog box so the image is on the left and text on the right,
     * used to visually distinguish Alzara's replies from the user's input.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for a line of user input.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.dialog.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a dialog box for one of Alzara's replies, flipped so the image
     * sits on the left.
     */
    public static DialogBox getAlzaraDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.dialog.getStyleClass().add("bot-bubble");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a dialog box for one of Alzara's error replies, flipped like a
     * normal reply but styled to catch the user's attention.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.dialog.getStyleClass().add("error-bubble");
        dialogBox.flip();
        return dialogBox;
    }
}
