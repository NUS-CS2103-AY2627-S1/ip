package quackers;

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
 * A single chat bubble: the speaker's picture next to the text they said.
 *
 * <p>This is a custom control. Its layout lives in {@code DialogBox.fxml}, which uses the
 * {@code fx:root} construct so that each {@code DialogBox} instance acts as both the root node
 * and the controller of that FXML file.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException error) {
            error.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Returns a dialog box for the user, with the picture on the right.
     *
     * @param text the text the user entered.
     * @param image the user's avatar.
     * @return the dialog box to display.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a dialog box for Quackers, flipped so the picture is on the left.
     *
     * @param text the reply from Quackers.
     * @param image the avatar of Quackers.
     * @return the dialog box to display.
     */
    public static DialogBox getQuackersDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Flips the dialog box so the picture is on the left and the text on the right, which is what
     * tells a reply apart from the user's own message at a glance.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
