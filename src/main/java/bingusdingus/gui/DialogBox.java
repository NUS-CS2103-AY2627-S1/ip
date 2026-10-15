package bingusdingus.gui;

import java.io.IOException;

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

/** Displays a message and its associated profile picture. */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /** Creates a dialog box containing the supplied message and optional profile picture. */
    public DialogBox(String message, Image profilePicture) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(message);
        displayPicture.setImage(profilePicture);
        displayPicture.setClip(new Circle(28, 28, 28));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> dialogChildren = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(dialogChildren);
        this.getChildren().setAll(dialogChildren);
    }

    /**
     * Returns a dialog box displaying a user's message.
     *
     * @param message the user's message.
     * @return the configured user dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message, null);
        dialogBox.displayPicture.setVisible(false);
        dialogBox.displayPicture.setManaged(false);
        dialogBox.getStyleClass().add("user-message");
        return dialogBox;
    }

    /**
     * Returns a dialog box displaying Bingus Dingus's message.
     *
     * @param message Bingus Dingus's message.
     * @param profilePicture the profile picture to display.
     * @return the configured Bingus Dingus dialog box.
     */
    public static DialogBox getBingusDingusDialog(String message, Image profilePicture) {
        DialogBox dialogBox = new DialogBox(message, profilePicture);
        dialogBox.flip();
        dialogBox.getStyleClass().add("assistant-message");
        return dialogBox;
    }
}
