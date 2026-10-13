package baymax.ui;

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
 * Represents a chat message with text and an avatar for its speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        assert text != null : "DialogBox should receive display text.";
        assert image != null : "DialogBox should receive an avatar image.";

        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        assert dialog != null : "DialogBox.fxml should inject dialog.";
        assert displayPicture != null : "DialogBox.fxml should inject displayPicture.";

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    private void flip() {
        assert getChildren().size() == 2
                : "DialogBox.fxml should contain text and avatar nodes.";

        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    private void showAsError() {
        dialog.getStyleClass().add("error-label");
    }

    /**
     * Creates a right-aligned dialog for user input.
     *
     * @param text the user's message
     * @param image the user's avatar
     * @return a dialog box for the user message
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog for a Baymax response.
     *
     * @param text Baymax's response
     * @param image Baymax's avatar
     * @param isError whether the response should use error highlighting
     * @return a dialog box for the Baymax response
     */
    public static DialogBox getBaymaxDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        if (isError) {
            dialogBox.showAsError();
        }
        return dialogBox;
    }
}
