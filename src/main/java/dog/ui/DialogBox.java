package dog.ui;

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

/**
 * JavaFX node for GUI chat bubbles.
 */
public class DialogBox extends HBox {
    private static final String REPLY_STYLE_CLASS = "reply-label";
    private static final String ERROR_STYLE_CLASS = "error-label";

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
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    private void applyCssStyle(String style) {
        this.dialog.getStyleClass().add(style);
    }

    public static DialogBox getUserDialog(String s, Image i) {
        return new DialogBox(s, i);
    }

    public static DialogBox getDogDialog(String s, Image i) {
        DialogBox db = new DialogBox(s, i);
        db.flip();
        db.applyCssStyle(REPLY_STYLE_CLASS);
        return db;
    }

    public static DialogBox getDogErrorDialog(String s, Image i) {
        DialogBox db = new DialogBox(s, i);
        db.flip();
        db.applyCssStyle(ERROR_STYLE_CLASS);
        return db;
    }
}
