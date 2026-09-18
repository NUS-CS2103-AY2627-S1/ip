package joebiden.gui;

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
 * Represents a dialog box containing the speaker's
 * profile image and message.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the given text and image.
     *
     * @param text Text to display.
     * @param image Speaker's profile image.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml")
            );

            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);

        dialog.getStyleClass().add("user-label");
        setAlignment(Pos.CENTER_RIGHT);
    }

    /**
     * Flips the dialog box so that the profile picture
     * appears on the left.
     */
    private void flip() {
        ObservableList<Node> temp =
                FXCollections.observableArrayList(getChildren());

        Collections.reverse(temp);
        getChildren().setAll(temp);

        setAlignment(Pos.CENTER_LEFT);

        dialog.getStyleClass().remove("user-label");
        dialog.getStyleClass().add("reply-label");

    }

    /**
     * Creates a dialog box for the user.
     *
     * @param text User's message.
     * @param image User's profile image.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.setMaxWidth(180.0);
        return dialogBox;
    }

    /**
     * Creates a dialog box for Joe Biden.
     *
     * @param text Joe Biden's response.
     * @param image Joe Biden's profile image.
     * @return Joe Biden dialog box.
     */
    public static DialogBox getJoeBidenDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.setMaxWidth(300.0);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates an error dialog box for Joe Biden.
     *
     * @param text Error message.
     * @param image Joe Biden's profile image.
     * @return Error dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);

        dialogBox.dialog.setMaxWidth(300.0);
        dialogBox.flip();

        dialogBox.dialog.getStyleClass().remove("reply-label");
        dialogBox.dialog.getStyleClass().add("error-label");

        return dialogBox;
    }
}
