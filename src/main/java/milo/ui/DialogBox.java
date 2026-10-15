package milo.ui;

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
import javafx.scene.layout.Priority;

/** Represents a chat dialog containing a message and its speaker's avatar. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /** Loads this dialog's FXML layout and supplies its text and avatar. */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /** Flips this dialog so Milo's avatar appears on the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /** Creates a right-aligned dialog for user input. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /** Creates a left-aligned response card that fills the space beside Milo's avatar. */
    public static DialogBox getMiloDialog(String text, Image image) {
        return getMiloDialog(text, image, false);
    }

    /** Creates a response card with distinct styling when command processing failed. */
    public static DialogBox getMiloDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("milo-dialog");
        if (isError) {
            dialogBox.getStyleClass().add("error-dialog");
        }
        dialogBox.dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialogBox.dialog, Priority.ALWAYS);
        return dialogBox;
    }
}
