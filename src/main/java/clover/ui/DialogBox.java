package clover.ui;

import java.io.IOException;
import java.util.Collections;

import clover.command.CommandResponseStyle;
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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box containing a speaker's image and message.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private StackPane avatarContainer;

    /**
     * Creates a dialog box with the supplied text and avatar image.
     *
     * @param dialogText the message to display
     * @param avatarImage the avatar image to display beside the message
     */
    private DialogBox(String dialogText, Image avatarImage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Clover's dialog box.", exception);
        }

        dialog.setText(dialogText);
        displayPicture.setImage(avatarImage);
        displayPicture.setClip(new Circle(17, 17, 17));
        avatarContainer.setClip(new Circle(19, 19, 19));
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.72));
    }

    /**
     * Flips the dialog box so its image is on the left and text is on the right.
     */
    private void flip() {
        ObservableList<Node> temporaryChildren = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(temporaryChildren);
        getChildren().setAll(temporaryChildren);
        setAlignment(Pos.BOTTOM_LEFT);
        getStyleClass().add("clover-dialog");
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a right-aligned dialog box for a user message.
     *
     * @param dialogText the user's message
     * @param avatarImage the user's avatar image
     * @return the dialog box to display
     */
    public static DialogBox getUserDialog(String dialogText, Image avatarImage) {
        DialogBox dialogBox = new DialogBox(dialogText, avatarImage);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for a Clover response.
     *
     * @param dialogText Clover's response
     * @param avatarImage Clover's avatar image
     * @return the flipped dialog box to display
     */
    public static DialogBox getCloverDialog(String dialogText, Image avatarImage) {
        return getCloverDialog(dialogText, avatarImage, CommandResponseStyle.STANDARD);
    }

    /**
     * Creates a left-aligned Clover dialog with the supplied response styling.
     *
     * @param dialogText Clover's response
     * @param avatarImage Clover's avatar image
     * @param responseStyle the visual style for the response
     * @return the styled, flipped dialog box to display
     */
    public static DialogBox getCloverDialog(String dialogText, Image avatarImage, CommandResponseStyle responseStyle) {
        var dialogBox = new DialogBox(dialogText, avatarImage);
        dialogBox.flip();
        dialogBox.changeDialogStyle(responseStyle);
        return dialogBox;
    }

    /**
     * Applies a CSS class for response styles that have a dedicated reply style.
     */
    private void changeDialogStyle(CommandResponseStyle responseStyle) {
        switch (responseStyle) {
            case ERROR:
                dialog.getStyleClass().add("error-label");
                break;
            case TASK_ADDED:
                dialog.getStyleClass().add("success-label");
                break;
            case TUTOREE_ADDED:
                dialog.getStyleClass().add("tutoree-label");
                break;
            case TASK_MARKED:
                dialog.getStyleClass().add("success-label");
                break;
            case TASK_DELETED:
                dialog.getStyleClass().add("delete-label");
                break;
            case STANDARD:
                break;
            default:
                throw new IllegalArgumentException("Unsupported response style: " + responseStyle);
        }
    }
}
