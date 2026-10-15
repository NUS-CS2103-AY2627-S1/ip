package shrekanddonkey;

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

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(49.5, 49.5, 49.5));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> dialogBoxChildren = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(dialogBoxChildren);
        getChildren().setAll(dialogBoxChildren);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box representing user input.
     *
     * @param text text entered by user.
     * @param img user avatar image.
     * @return a new DialogBox for the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a flipped dialog box representing the chatbot's response.
     *
     * @param text text response from the chatbot.
     * @param img chatbot avatar image.
     * @return a new DialogBox for the chatbot.
     */
    public static DialogBox getShrekDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a flipped dialog box styled for error responses from the chatbot.
     *
     * @param text error response from the chatbot.
     * @param img chatbot avatar image.
     * @return a new DialogBox with error styling.
     */
    public static DialogBox getShrekErrorDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }
}
