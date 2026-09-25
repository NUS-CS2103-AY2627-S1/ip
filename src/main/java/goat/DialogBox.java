package goat;

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
 * Displays a speaker's avatar beside their chat message.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.78));
    }

    /**
     * Places the avatar on the left and the message on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog for the user.
     *
     * @param text user's command
     * @param image user's avatar
     * @return user dialog
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.displayPicture.setManaged(false);
        dialogBox.displayPicture.setVisible(false);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Goat.
     *
     * @param text Goat's response
     * @param image Goat's avatar
     * @return Goat dialog
     */
    public static DialogBox getGoatDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("goat-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned, attention-grabbing error dialog from Goat.
     *
     * @param text error response
     * @param image Goat's avatar
     * @return Goat error dialog
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getGoatDialog(text, image);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
