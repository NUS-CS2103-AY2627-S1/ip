package hse.gui;

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

/** Displays one chat message with its sender's image. */
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
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> dialogNodes = FXCollections.observableArrayList(getChildren());
        Collections.reverse(dialogNodes);
        getChildren().setAll(dialogNodes);
        setAlignment(Pos.TOP_LEFT);
    }

    /** Creates a dialog box displaying a message from the user. */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /** Creates a dialog box displaying a message from HSE. */
    public static DialogBox getHseDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        dialogBox.getStyleClass().add("hse-dialog");
        return dialogBox;
    }

    /** Creates a visually distinct dialog box displaying an error from HSE. */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox dialogBox = getHseDialog(text, img);
        dialogBox.getStyleClass().remove("hse-dialog");
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
