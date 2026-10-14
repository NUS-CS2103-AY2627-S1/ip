package verity.gui;

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
            e.printStackTrace();
        }

        // FXMLLoader.load() injects every @FXML field before returning; if it didn't throw,
        // dialog/displayPicture are guaranteed set. A null here would mean DialogBox.fxml's
        // fx:id attributes no longer match these field names.
        assert dialog != null && displayPicture != null : "FXML fields must be injected after load()";
        dialog.setText(text);
        dialog.getStyleClass().add("user-label");
        dialog.setMinWidth(0);
        dialog.maxWidthProperty().bind(widthProperty().subtract(70));
        displayPicture.setImage(img);
        // Keep the pixel-art avatars crisp instead of blurred when JavaFX scales them.
        displayPicture.setSmooth(false);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for something the user typed.
     *
     * @param text Text the user typed.
     * @param img User's avatar.
     * @return Dialog box aligned to the right.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for Verity's response, mirrored to the left.
     *
     * @param text Verity's response text.
     * @param img Verity's avatar.
     * @return Dialog box aligned to the left.
     */
    public static DialogBox getVerityDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Creates a left-aligned Verity dialog with the error treatment applied.
     *
     * @param text Error message to show.
     * @param img Verity's avatar.
     * @return A highlighted error dialog.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        var db = getVerityDialog(text, img);
        db.dialog.getStyleClass().remove("reply-label");
        db.dialog.getStyleClass().add("error-label");
        return db;
    }
}
