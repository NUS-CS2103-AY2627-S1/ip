package eden.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents one chat message together with a text avatar for its speaker.
 */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_RESOURCE = "/view/DialogBox.fxml";

    @FXML
    private Label dialog;

    @FXML
    private Label avatar;

    /**
     * Creates a dialog box containing a message and its speaker's avatar text.
     */
    private DialogBox(String text, String avatarText) {
        URL dialogBoxUrl = Objects.requireNonNull(
                DialogBox.class.getResource(DIALOG_BOX_RESOURCE),
                "Missing GUI resource: " + DIALOG_BOX_RESOURCE);
        FXMLLoader fxmlLoader = new FXMLLoader(dialogBoxUrl);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        assert dialog != null && avatar != null
                : "The dialog box layout must inject both labels";
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.72));
        dialog.setText(text);
        avatar.setText(avatarText);
    }

    /**
     * Creates a right-aligned dialog for text entered by the user.
     *
     * @param text user-entered text.
     * @return dialog representing the user's message.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "You");
        dialogBox.getStyleClass().add("user-dialog-box");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a response from Eden.
     *
     * @param text response text.
     * @return dialog representing Eden's response.
     */
    public static DialogBox getEdenDialog(String text, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, "Eden");
        dialogBox.flip();
        dialogBox.getStyleClass().add("eden-dialog-box");
        if (isError) {
            dialogBox.getStyleClass().add("error-dialog-box");
        }
        return dialogBox;
    }

    /**
     * Moves the avatar to the left and aligns the dialog with the left edge.
     */
    private void flip() {
        ObservableList<Node> reversedChildren =
                FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.TOP_LEFT);
    }
}
