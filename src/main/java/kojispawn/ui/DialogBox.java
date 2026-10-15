package kojispawn.ui;

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
import kojispawn.command.CommandType;

/**
 * Displays one Koji's Pawn message together with its sender's image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing the supplied message and image.
     *
     * @param message Message to display.
     * @param image Image representing the sender.
     */
    private DialogBox(String message, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(message);
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog box for a user message.
     *
     * @param message User's message.
     * @param image Image representing the user.
     * @return Dialog box for the user message.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /**
     * Creates a left-aligned dialog box without command-specific styling.
     *
     * @param message Koji's Pawn message.
     * @param image Image representing Koji's Pawn.
     * @return Dialog box for the Koji's Pawn message.
     */
    public static DialogBox getKojiDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for a Koji's Pawn response.
     *
     * @param message Koji's Pawn response.
     * @param image Image representing Koji's Pawn.
     * @param commandType Type of command that generated the response.
     * @return Dialog box for the Koji's Pawn response.
     */
    public static DialogBox getKojiDialog(String message, Image image, CommandType commandType) {
        DialogBox dialogBox = getKojiDialog(message, image);
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    /**
     * Flips this dialog box so its image appears on the left.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Adds a response-specific CSS class based on the command result.
     * Commands without a special style retain the default reply appearance.
     * UNKNOWN represents an unsuccessful command and receives error styling.
     *
     * @param commandType Type of the processed command.
     */

    private void changeDialogStyle(CommandType commandType) {
        String styleClass = switch (commandType) {
            case TODO, DEADLINE, EVENT -> "add-label";
            case MARK, UNMARK -> "marked-label";
            case DELETE -> "delete-label";
            case UNKNOWN -> "error-label";
            default -> null;
        };
        if (styleClass != null) {
            dialog.getStyleClass().add(styleClass);
        }
    }
}
