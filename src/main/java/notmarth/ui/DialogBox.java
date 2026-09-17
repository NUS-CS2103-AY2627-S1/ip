package notmarth.ui;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** A chat message whose visual structure is defined in FXML. */
public final class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String message, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
            getStylesheets().add(
                    DialogBox.class.getResource("/css/dialog-box.css").toExternalForm());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        dialog.setText(message);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(32.0, 32.0, 32.0));
    }

    /** Creates a compact, right-aligned command for the user. */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /** Creates a full-width, left-aligned response for NotMarth. */
    public static DialogBox getNotMarthDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("notmarth-dialog");
        return dialogBox;
    }

    /** Creates a high-contrast response for an invalid command or failed action. */
    public static DialogBox getErrorDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    /** Places the application avatar before the application response. */
    private void flip() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
    }
}
