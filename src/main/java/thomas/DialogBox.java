package thomas;

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
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the
 * speaker's face and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_RADIUS = 18;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
        HBox.setHgrow(dialog, Priority.ALWAYS);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left
     * and text on the right.
     */
    private void flip() {
        ObservableList<Node> dialogElements = FXCollections.observableArrayList(
                this.getChildren());
        Collections.reverse(dialogElements);
        getChildren().setAll(dialogElements);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a user dialog box.
     *
     * @param text The user's message.
     * @param image The user's avatar image.
     * @return A dialog box for the user.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a Thomas dialog box.
     *
     * @param text Thomas's response message.
     * @param image Thomas's avatar image.
     * @return A dialog box for Thomas.
     */
    public static DialogBox getThomasDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("thomas-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates an error dialog box with an attention-catching treatment.
     *
     * @param text Error response from Thomas.
     * @param image Thomas's avatar image.
     * @return An error-styled dialog box for Thomas.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getThomasDialog(text, image);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
