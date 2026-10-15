package tblade.gui;

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
            // dialog/displayPicture are @FXML-injected by load() above; without it they stay null
            // and setText()/setImage() below would NPE anyway, so fail fast with a clear cause.
            throw new RuntimeException("Failed to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog box for text the user typed.
     *
     * @param text the user's input
     * @param img the user's avatar
     * @return the user's dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.getStyleClass().add("user-dialog");
        return db;
    }

    /**
     * Creates a left-aligned dialog box for TBlade's reply.
     *
     * @param text TBlade's response
     * @param img TBlade's avatar
     * @return TBlade's dialog box
     */
    public static DialogBox getTBladeDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        db.getStyleClass().add("tblade-dialog");
        return db;
    }
}
