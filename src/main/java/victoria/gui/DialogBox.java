package victoria.gui;

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
import javafx.scene.layout.Region;

/** Represents one chat message with its speaker image. */
public class DialogBox extends HBox {
    private static final double VICTORIA_DIALOG_MAX_WIDTH = 340;

    @FXML private Label dialog;
    @FXML private ImageView displayPicture;

    /** Builds a dialog with the supplied text and image. */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a chat dialog.", exception);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /** Creates a right-aligned command entered by the user. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-command-label");
        return dialogBox;
    }

    /** Creates a left-aligned Victoria dialog. */
    public static DialogBox getVictoriaDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.dialog.setPrefWidth(VICTORIA_DIALOG_MAX_WIDTH);
        dialogBox.dialog.setMaxWidth(VICTORIA_DIALOG_MAX_WIDTH);
        dialogBox.dialog.setMinHeight(Region.USE_PREF_SIZE);
        dialogBox.dialog.getStyleClass().add("victoria-reply-label");
        return dialogBox;
    }

    /** Creates a left-aligned Victoria error reply. */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getVictoriaDialog(text, image);
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }

    /** Reverses the speaker image and text positions for a reply. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }
}
