package tem;

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
 * A single chat bubble containing a message and an avatar image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Label avatarInitial;

    private DialogBox(String text, Image image, String initial) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load /view/DialogBox.fxml", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        avatarInitial.setText(initial);
        getStyleClass().add("dialog-box");
        dialog.getStyleClass().add("dialog-label");
        displayPicture.getStyleClass().add("avatar");
    }

    /**
     * Flips the dialog box so the avatar appears on the left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog box for user input.
     *
     * @param text message entered by the user.
     * @param image avatar shown beside the message.
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, "Y");
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for Tem's reply.
     *
     * @param text response from Tem.
     * @param image avatar shown beside the message.
     * @return Tem dialog box
     */
    public static DialogBox getTemDialog(String text, Image image) {
        return getTemDialog(text, image, false);
    }

    /**
     * Creates a left-aligned Tem dialog, optionally highlighted as an error.
     *
     * @param text response from Tem.
     * @param image avatar shown beside the message.
     * @param isError whether the response corrects invalid input.
     * @return Tem dialog box
     */
    public static DialogBox getTemDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image, "T");
        dialogBox.flip();
        dialogBox.getStyleClass().add(isError ? "error-dialog" : "tem-dialog");
        return dialogBox;
    }
}
