package wally;

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
 * Represents a dialog box consisting of an ImageView to represent the speaker's
 * face and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the
     * right.
     */
    private void flip() {
        ObservableList<Node> dialogNodes = FXCollections.observableArrayList(getChildren());
        Collections.reverse(dialogNodes);
        getChildren().setAll(dialogNodes);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box for a user message.
     *
     * @param text Text of the message.
     * @param image Image representing the user.
     * @return Dialog box containing the user message.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 8 12;");
        return dialogBox;
    }

    /**
     * Returns a dialog box for a Wally message.
     *
     * @param text Text of the message.
     * @param image Image representing Wally.
     * @return Dialog box containing the Wally message.
     */
    public static DialogBox getWallyDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }
}
