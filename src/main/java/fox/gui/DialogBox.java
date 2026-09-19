package fox.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
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

/** Represents one conversation entry with its speaker image and text. */
public class DialogBox extends HBox {
    private static final double DIALOG_HORIZONTAL_MARGIN = 82.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Fox's dialog view.", exception);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        dialog.maxWidthProperty().bind(Bindings.max(0.0,
                widthProperty().subtract(DIALOG_HORIZONTAL_MARGIN)));
    }

    /** Returns a right-aligned dialog entry for user input. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-label");
        return dialogBox;
    }

    /** Returns a left-aligned dialog entry for Fox's response. */
    public static DialogBox getFoxDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("reply-label");
        dialogBox.movePictureToLeft();
        return dialogBox;
    }

    /** Returns a visually distinct left-aligned dialog entry for an error response. */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("error-label");
        dialogBox.movePictureToLeft();
        return dialogBox;
    }

    /** Moves Fox's picture before the response text. */
    private void movePictureToLeft() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
