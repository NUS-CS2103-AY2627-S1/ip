package sherlock.gui;

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
 * Displays one speaker avatar beside a wrapped chat message.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box view.", exception);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /** Places the avatar on the left for Sherlock's replies. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog for user input.
     *
     * @param text user-entered command
     * @param image user avatar
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog-box");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Sherlock's response.
     *
     * @param text Sherlock's response
     * @param image Sherlock avatar
     * @return Sherlock dialog box
     */
    public static DialogBox getSherlockDialog(String text, Image image) {
        return getSherlockDialog(text, image, false);
    }

    /**
     * Creates a left-aligned Sherlock dialog, highlighting it when it reports an error.
     *
     * @param text Sherlock's response
     * @param image Sherlock avatar
     * @param isError whether the response describes an invalid command or failed operation
     * @return Sherlock dialog box with the appropriate visual style
     */
    public static DialogBox getSherlockDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add(isError ? "error-dialog-box" : "sherlock-dialog-box");
        return dialogBox;
    }
}
