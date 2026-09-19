package yawned.gui;

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
import javafx.scene.layout.Priority;
import yawned.parser.CommandType;

/**
 * Represents a dialog box containing a speaker image and message.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Configures Yawned's reply with its image on the left and message on the right.
     */
    private void configureYawnedReply() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(dialog, Priority.ALWAYS);
        dialog.setMaxWidth(Double.MAX_VALUE);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Configures the compact layout used for a user message.
     */
    private void configureUserMessage() {
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
        getStyleClass().add("user-dialog");
        dialog.getStyleClass().add("user-label");
    }

    /**
     * Applies the response color associated with the supplied command type.
     *
     * @param commandType Type of command that produced this response.
     */
    private void changeDialogStyle(CommandType commandType) {
        switch (commandType) {
            case TODO:
            case DEADLINE:
            case EVENT:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK:
            case UNMARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            case UNKNOWN:
                dialog.getStyleClass().add("error-label");
                break;
            default:
                break;
        }
    }

    /**
     * Returns a dialog box for a user message.
     *
     * @param text Message to display.
     * @return A right-aligned user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.configureUserMessage();
        return dialogBox;
    }

    /**
     * Returns a dialog box for a Yawned message.
     *
     * @param text Message to display.
     * @param image Image representing Yawned.
     * @param commandType Type of command that produced the response.
     * @return A left-aligned Yawned dialog box.
     */
    public static DialogBox getYawnedDialog(String text, Image image, CommandType commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.configureYawnedReply();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }
}
