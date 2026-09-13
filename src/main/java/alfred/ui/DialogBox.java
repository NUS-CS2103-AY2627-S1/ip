package alfred.ui;

import java.io.IOException;

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
 * Displays one chat message and its corresponding avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog containing a message and an avatar.
     *
     * @param message message displayed in the dialog.
     * @param avatar image displayed beside the message.
     */
    private DialogBox(String message, Image avatar) {
        assert message != null : "Dialog messages must not be null";
        assert avatar != null : "Dialog avatars must not be null";
        loadFxml();
        dialog.setText(message);
        displayPicture.setImage(avatar);
    }

    /**
     * Creates a right-aligned dialog for a user message.
     *
     * @param message message displayed in the dialog.
     * @param avatar image displayed beside the message.
     * @return user dialog.
     */
    public static DialogBox getUserDialog(String message, Image avatar) {
        DialogBox dialogBox = new DialogBox(message, avatar);
        dialogBox.hideAvatar();
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for an Alfred response.
     *
     * @param message message displayed in the dialog.
     * @param avatar image displayed beside the message.
     * @return Alfred dialog.
     */
    public static DialogBox getAlfredDialog(String message, Image avatar) {
        return getAlfredDialog(message, avatar, null);
    }

    /**
     * Creates a left-aligned dialog for an Alfred response with command-specific styling.
     *
     * @param message message displayed in the dialog.
     * @param avatar image displayed beside the message.
     * @param commandType type of command that produced the response.
     * @return Alfred dialog.
     */
    public static DialogBox getAlfredDialog(String message, Image avatar, String commandType) {
        DialogBox dialogBox = new DialogBox(message, avatar);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    /**
     * Loads the dialog's FXML view into this custom control.
     */
    private void loadFxml() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Alfred's dialog box.", e);
        }
    }

    /**
     * Positions the avatar on the left and aligns the dialog to the left edge.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(children);
        getChildren().setAll(children);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Removes the user's fixed avatar so their short commands use less chat space.
     */
    private void hideAvatar() {
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
    }

    /**
     * Adds a visual cue for responses that change task state or reject input.
     *
     * @param commandType type of command that produced the response.
     */
    private void changeDialogStyle(String commandType) {
        if ("AddCommand".equals(commandType)) {
            dialog.getStyleClass().add("add-label");
        } else if ("MarkCommand".equals(commandType) || "UnmarkCommand".equals(commandType)) {
            dialog.getStyleClass().add("marked-label");
        } else if ("DeleteCommand".equals(commandType)) {
            dialog.getStyleClass().add("delete-label");
        } else if ("Error".equals(commandType)) {
            dialog.getStyleClass().add("error-label");
        }
    }
}
