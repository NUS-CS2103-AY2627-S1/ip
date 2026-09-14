package penguin.gui;

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
 * Represents a dialog box containing the speaker's image and message.
 */
public class DialogBox extends HBox {
    /** Diameter of the circular avatar image. */
    private static final double AVATAR_DIAMETER = 52;

    /** Radius of the circular avatar image. */
    private static final double AVATAR_RADIUS = AVATAR_DIAMETER / 2;

    /** Label displaying the dialog message. */
    @FXML
    private Label dialog;

    /** Avatar displayed beside the dialog message. */
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box from the FXML layout and supplied content.
     *
     * @param text text displayed in the dialog.
     * @param image image displayed beside the text.
     * @throws IllegalStateException if the FXML layout cannot be loaded.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DialogBox.fxml.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(
                AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> reorderedChildren =
                FXCollections.observableArrayList(getChildren());
        Collections.reverse(reorderedChildren);
        getChildren().setAll(reorderedChildren);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog box for the user.
     *
     * @param text text to display.
     * @param image image representing the user.
     * @return user dialog box.
     * @throws IllegalStateException if the dialog layout cannot be loaded.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for Penguin.
     *
     * @param text text to display.
     * @param image image representing Penguin.
     * @return Penguin dialog box.
     * @throws IllegalStateException if the dialog layout cannot be loaded.
     */
    public static DialogBox getPenguinDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("penguin-dialog");
        return dialogBox;
    }
}
