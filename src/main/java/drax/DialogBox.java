package drax;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
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
import javafx.scene.text.Font;

/**
 * Displays one chat message together with its speaker's profile image.
 */
public class DialogBox extends HBox {
    private static final double DEFAULT_AVATAR_SIZE = 64;
    private static final double DEFAULT_FONT_SIZE = 13;
    private static final String DIALOG_FONT_NAME = "Helvetica Neue Medium";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Loads the dialog layout and supplies its message and profile image.
     *
     * @param message message to display.
     * @param image profile image of the speaker.
     * @param contentScale scale derived from the size of the main window.
     */
    private DialogBox(String message, Image image, ObservableDoubleValue contentScale) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog-box layout", e);
        }

        dialog.setText(message);
        displayPicture.setImage(image);
        bindContentSize(contentScale);
    }

    /**
     * Keeps the message text and avatar proportional to the available window space.
     *
     * @param contentScale scale derived from the size of the main window.
     */
    private void bindContentSize(ObservableDoubleValue contentScale) {
        dialog.fontProperty().bind(Bindings.createObjectBinding(() ->
                new Font(DIALOG_FONT_NAME, DEFAULT_FONT_SIZE * contentScale.doubleValue()),
                contentScale));
        displayPicture.fitWidthProperty().bind(Bindings.multiply(DEFAULT_AVATAR_SIZE, contentScale));
        displayPicture.fitHeightProperty().bind(Bindings.multiply(DEFAULT_AVATAR_SIZE, contentScale));
    }

    /** Flips the dialog so that the profile image appears on the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }


    /**
     * Creates a dialog with the user's profile image on the right.
     *
     * @param message message entered by the user.
     * @param image user's profile image.
     * @param contentScale scale derived from the size of the main window.
     * @return dialog configured for a user message
     */
    public static DialogBox getUserDialog(String message, Image image, ObservableDoubleValue contentScale) {
        DialogBox dialogBox = new DialogBox(message, image, contentScale);
        dialogBox.dialog.getStyleClass().add("user-label");
        return dialogBox;
    }

    private void changeDialogStyle(String commandType) {
        switch(commandType) {
            case "AddCommand":
                dialog.getStyleClass().add("add-label");
                break;
            case "ChangeMarkCommand":
                dialog.getStyleClass().add("marked-label");
                break;
            case "ListCommand":
                dialog.getStyleClass().add("list-label");
                break;
            case "DeleteCommand":
                dialog.getStyleClass().add("delete-label");
                break;
            default:
        }
    }

    /**
     * Creates a dialog with Drax's profile image on the left.
     *
     * @param message response returned by Drax.
     * @param image Drax's profile image.
     * @param commandType type of command that produced the response.
     * @param contentScale scale derived from the size of the main window.
     * @return dialog configured for a Drax response
     */
    public static DialogBox getDraxDialog(
            String message, Image image, String commandType, ObservableDoubleValue contentScale) {
        DialogBox dialogBox = new DialogBox(message, image, contentScale);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }
}
