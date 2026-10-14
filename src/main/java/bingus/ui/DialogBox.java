// Adapted from the SE-EDU JavaFX Tutorial, Parts 4 and 5:
// https://se-education.org/guides/tutorials/javaFxPart4.html
// https://se-education.org/guides/tutorials/javaFxPart5.html

package bingus.ui;

import java.io.IOException;
import java.util.Collections;

import bingus.command.CommandType;
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
 * Represents a conversational dialog containing message text and, for Bingus replies, an image.
 */
public class DialogBox extends HBox {
    private static final double MAXIMUM_BUBBLE_WIDTH_RATIO = 0.82;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load the dialog box layout.", e);
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(MAXIMUM_BUBBLE_WIDTH_RATIO));
    }

    private DialogBox(String text, Image image) {
        this(text);
        displayPicture.setImage(image);
    }

    /**
     * Configures the dialog as a compact user command without a profile picture.
     */
    private void configureAsUserDialog() {
        getStyleClass().add("user-dialog");
        dialog.getStyleClass().add("user-label");
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
    }

    /**
     * Configures the dialog with Bingus's image on the left and its reply on the right.
     */
    private void configureAsBingusDialog() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.BOTTOM_LEFT);
        getStyleClass().add("bingus-dialog");
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for a user message.
     *
     * @param text message text.
     * @return dialog box for the user message.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox userDialog = new DialogBox(text);
        userDialog.configureAsUserDialog();
        return userDialog;
    }

    /**
     * Creates a left-aligned dialog box for a Bingus response.
     *
     * @param text response text.
     * @param image the image representing Bingus.
     * @param commandType type of command that produced the response.
     * @return dialog box for the Bingus response.
     */
    public static DialogBox getBingusDialog(String text, Image image, CommandType commandType) {
        DialogBox bingusDialog = new DialogBox(text, image);
        bingusDialog.configureAsBingusDialog();
        bingusDialog.applyResponseStyle(commandType);
        return bingusDialog;
    }

    /** Applies a neutral, success, or error style based on the command type. */
    private void applyResponseStyle(CommandType commandType) {
        switch (commandType) {
            case ADD:
            case DELETE:
            case MARK:
            case UPDATE:
                dialog.getStyleClass().add("success-label");
                break;
            case INVALID:
                dialog.getStyleClass().add("error-label");
                break;
            default:
                dialog.getStyleClass().add("neutral-label");
        }
    }
}
