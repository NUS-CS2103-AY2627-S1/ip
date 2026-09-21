package melodie.gui;

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
 * Represents one chat row containing a message and its speaker's avatar.
 */
public class DialogBox extends HBox {
    private static final double MAX_DIALOG_WIDTH_RATIO = 0.72;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box", e);
        }

        this.dialog.setText(text);
        this.displayPicture.setImage(image);
        this.dialog.maxWidthProperty().bind(
                this.widthProperty().multiply(MAX_DIALOG_WIDTH_RATIO));
    }

    /**
     * Creates a right-aligned user message.
     *
     * @param text Message text.
     * @param image User avatar.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.dialog.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a left-aligned Melodie response.
     *
     * @param text Response text.
     * @param image Melodie avatar.
     * @return Melodie dialog box.
     */
    public static DialogBox getMelodieDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("melodie-dialog");
        dialogBox.dialog.getStyleClass().add("melodie-bubble");
        return dialogBox;
    }

    /**
     * Creates a visually prominent Melodie error response.
     *
     * @param text Error message.
     * @param image Melodie avatar.
     * @return Error dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("melodie-dialog");
        dialogBox.dialog.getStyleClass().add("error-bubble");
        return dialogBox;
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        this.getChildren().setAll(children);
        this.setAlignment(Pos.TOP_LEFT);
    }
}
