package summer.ui;

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
 * A dialog box: an avatar picture next to a text bubble. User dialog boxes face
 * right; Summer's are flipped to face left.
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
    }

    /** Styles this dialog box as the user's own message (right-aligned, accent color). */
    public void markAsUser() {
        dialog.getStyleClass().add("user-dialog-label");
    }

    /** Styles this dialog box as an error reply. */
    public void markAsError() {
        dialog.getStyleClass().add("error-dialog");
    }

    /** Puts the picture on the left and the text on the right. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a right-facing dialog box for something the user said.
     *
     * @param text the user's message
     * @param img the user's avatar
     * @return the dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.markAsUser();
        return db;
    }

    /**
     * Returns a left-facing dialog box for something Summer said.
     *
     * @param text Summer's reply
     * @param img Summer's avatar
     * @return the dialog box
     */
    public static DialogBox getSummerDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
