package serangooner.gui;

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
 * Represents one message in the conversation.
 * A message is shown as what was said beside the face of whoever said it. The
 * user's messages face one way and the chatbot's the other, so that a glance
 * down the window is enough to tell the two apart.
 */
public class DialogBox extends HBox {
    private static final String STYLE_CLASS_REPLY = "reply-label";
    private static final String STYLE_CLASS_ERROR = "error-label";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image picture) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            // A message that cannot be built leaves nothing worth carrying on with.
            throw new IllegalStateException("cannot load a dialog box", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(picture);
    }

    /**
     * Returns a message from the user.
     *
     * @param text Words the user typed.
     * @param picture Face to show beside them.
     */
    public static DialogBox getUserDialog(String text, Image picture) {
        return new DialogBox(text, picture);
    }

    /**
     * Returns a message from the chatbot, facing the other way.
     *
     * @param text Words the chatbot replied with.
     * @param picture Face to show beside them.
     * @param isError True if the message explains a refused command.
     */
    public static DialogBox getChatbotDialog(String text, Image picture, boolean isError) {
        DialogBox box = new DialogBox(text, picture);
        box.flip();
        if (isError) {
            box.dialog.getStyleClass().add(STYLE_CLASS_ERROR);
        }
        return box;
    }

    /**
     * Turns this box around, so that the face sits on the left of the words
     * rather than the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);

        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add(STYLE_CLASS_REPLY);
    }
}
