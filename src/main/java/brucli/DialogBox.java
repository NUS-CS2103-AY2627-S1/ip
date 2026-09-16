package brucli;

import java.io.IOException;
import java.util.Collections;

import brucli.ui.ResponseType;
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
 * Displays one chat message together with an image representing its speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Reverses the dialog contents and aligns them to the left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a right-aligned dialog for a message entered by the user.
     *
     * @param text Message to display.
     * @param image Image representing the user.
     * @return Dialog containing the user's message.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog for a response from BruCLI.
     *
     * @param text Response to display.
     * @param image Image representing BruCLI.
     * @param responseType Visual category of the response.
     * @return Dialog containing BruCLI's response.
     */
    public static DialogBox getBruCliDialog(
            String text,
            Image image,
            ResponseType responseType
    ) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.dialog.getStyleClass().add(responseType.getStyleClass());
        return dialogBox;
    }
}
