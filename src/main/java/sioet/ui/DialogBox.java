package sioet.ui;

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
 * Represents a dialog box in the chatbot interface.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box.
     *
     * @param text the text displayed in the dialog box
     * @param img  the image displayed beside the dialog
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(
                            MainWindow.class.getResource("/view/DialogBox.fxml"));

            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box so that it appears on the opposite side.
     */
    private void flip() {
        ObservableList<Node> tmp =
                FXCollections.observableArrayList(this.getChildren());

        Collections.reverse(tmp);

        getChildren().setAll(tmp);

        setAlignment(Pos.TOP_LEFT);

        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for the user.
     *
     * @param text the user's message
     * @param img  the user's image
     * @return the user dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-label");
        return db;
    }

    /**
     * Creates a dialog box for Sioet.
     *
     * @param text Sioet's response
     * @param img  Sioet's image
     * @return Sioet's dialog box
     */
    public static DialogBox getSioetDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Creates a dialog box for an error response.
     *
     * @param text the error message
     * @param img  Sioet's image
     * @return the error dialog box
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.dialog.getStyleClass().add("error-label");
        return db;
    }
}
