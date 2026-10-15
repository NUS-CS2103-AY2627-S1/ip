package michael;

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

import java.io.IOException;
import java.util.Collections;

/** A reusable dialog box containing a speaker image and message text. */
public class DialogBox extends HBox {
    @FXML private Label dialog;
    @FXML private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", e);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        setMaxWidth(Double.MAX_VALUE);
    }

    /** Flips the dialog so that the speaker image is on the right. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /** Creates a flipped dialog for a user message. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        return dialogBox;
    }

    /** Creates a dialog for a Michael message. */
    public static DialogBox getMichaelDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /** Creates a visually prominent dialog for an invalid command or input. */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox("⚠ " + text, image);
        dialogBox.dialog.setStyle(
                "-fx-font-size: 14px;"
                        + " -fx-padding: 8px;"
                        + " -fx-font-weight: bold;"
                        + " -fx-text-fill: #8b0000;"
                        + " -fx-background-color: #ffe1e1;"
                        + " -fx-border-color: #d32f2f;"
                        + " -fx-border-width: 2px;"
                        + " -fx-border-radius: 5px;"
                        + " -fx-background-radius: 5px;");
        return dialogBox;
    }
}
