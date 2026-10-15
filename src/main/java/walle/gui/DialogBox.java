package walle.gui;

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
 * Represents one chat bubble: an {@link ImageView} for the speaker's avatar
 * and a {@link Label} for their message text.
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

    // Flips the dialog box so the avatar is on the left and text on the right
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for a message from the user, right-aligned.
     *
     * @param text the message text.
     * @param img  the user's avatar image.
     * @return the dialog box.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.getStyleClass().add("user-dialog-box");
        return db;
    }

    /**
     * Creates a dialog box for a normal (non-error) message from WALLE,
     * left-aligned.
     *
     * @param text the message text.
     * @param img  WALLE's avatar image.
     * @return the dialog box.
     */
    public static DialogBox getWalleDialog(String text, Image img) {
        return getWalleDialog(text, img, false);
    }

    /**
     * Creates a dialog box for a message from WALLE, left-aligned, styled
     * differently if it reports an error.
     *
     * @param text    the message text.
     * @param img     WALLE's avatar image.
     * @param isError whether the message reports an error, so it can be
     *                styled to catch the user's attention.
     * @return the dialog box.
     */
    public static DialogBox getWalleDialog(String text, Image img, boolean isError) {
        var db = new DialogBox(text, img);
        db.flip();
        db.getStyleClass().add("walle-dialog-box");
        if (isError) {
            db.getStyleClass().add("error-dialog-box");
        }
        return db;
    }

}
