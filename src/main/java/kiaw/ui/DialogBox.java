package kiaw.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Represents a dialog box containing a message and the speaker's avatar.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private StackPane avatarContainer;

    @FXML
    private Label avatarText;

    /**
     * Creates a dialog box containing the specified text.
     *
     * @param text message to display
     */
    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(DialogBox.class.getResource(
                            "/view/DialogBox.fxml"));

            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load DialogBox.fxml.", e);
        }

        dialog.setText(text);
    }

    /**
     * Flips the dialog box so the avatar appears on the left.
     */
    private void flip() {
        ObservableList<Node> children =
                FXCollections.observableArrayList(
                        this.getChildren());

        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for the user's message.
     *
     * @param text user's message
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);

        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.dialog.getStyleClass().add("user-dialog-label");
        dialogBox.avatarContainer.getStyleClass().add("user-avatar");
        dialogBox.avatarText.setText("YOU");

        return dialogBox;
    }

    /**
     * Creates a dialog box for Kiaw's response.
     *
     * @param text Kiaw's response
     * @return Kiaw dialog box
     */
    public static DialogBox getKiawDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);

        dialogBox.flip();
        dialogBox.getStyleClass().add("kiaw-dialog");
        dialogBox.dialog.getStyleClass().add("kiaw-dialog-label");
        dialogBox.avatarContainer.getStyleClass().add("kiaw-avatar");

        dialogBox.avatarText.setText("✦");

        return dialogBox;
    }
}
