package duke.gui;

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
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of a circular image to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Circle displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        if (img != null) {
            displayPicture.setFill(new ImagePattern(img));
        }
    }

    /**
     * Flips the dialog box such that the profile picture is on the left and text is on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.displayPicture.getStyleClass().add("user-picture");
        return dialogBox;
    }

    public static DialogBox getDukeDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.displayPicture.getStyleClass().add("duke-picture");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a visually highlighted dialog for an error response from Duke.
     *
     * @param text Error message to display.
     * @param img Image representing Duke.
     * @return Dialog box styled as an error.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox dialogBox = getDukeDialog(text, img);
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }
}
