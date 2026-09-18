package rene.gui;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays one user or Rene message in the conversation.
 *
 * <p>The two sides of the conversation are intentionally asymmetric. The user's
 * command is rendered as a compact, right-aligned chip, while Rene's response is
 * a full card with an avatar. This mirrors the user/chatbot dynamic (one side
 * is an application, not a second human) and keeps the layout from wasting
 * vertical space on a redundant "You" avatar.
 */
public class DialogBox extends HBox {
    private static final String RENE_AVATAR = "R";
    private static final String RENE_NAME = "Rene";

    @FXML
    private Label messageLabel;
    @FXML
    private Label avatarLabel;
    @FXML
    private Label senderLabel;

    /** The card holding the sender name and message; the main window clamps its width. */
    private final VBox bubbleCard;

    /**
     * Loads a reusable dialog box from FXML and supplies its text.
     *
     * @param message the message displayed in the dialog box.
     */
    private DialogBox(String message) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box view.", exception);
        }
        messageLabel.setText(message);
        bubbleCard = (VBox) messageLabel.getParent();
    }

    /**
     * Creates a right-aligned chip for a command entered by the user.
     * The user side omits the avatar and sender name: Rene knows who is typing,
     * so showing a "You" avatar would only spend space the response could use.
     *
     * @param message the user's command.
     * @return the user dialog.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.senderLabel.setVisible(false);
        dialogBox.senderLabel.setManaged(false);
        // Remove the avatar from the layout entirely so it takes no space.
        ObservableList<Node> children = FXCollections.observableArrayList(dialogBox.getChildren());
        children.removeIf(node -> node.getStyleClass().contains("avatar"));
        dialogBox.getChildren().setAll(children);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned card for Rene's response.
     *
     * @param message Rene's response.
     * @return Rene's dialog.
     */
    public static DialogBox getReneDialog(String message) {
        return createReneDialog(message, false);
    }

    /**
     * Creates a left-aligned card for a Rene error response, visually
     * highlighted so the error stands out from a normal reply.
     *
     * @param message Rene's error response.
     * @return the highlighted error dialog.
     */
    public static DialogBox getErrorDialog(String message) {
        return createReneDialog(message, true);
    }

    /**
     * Builds a left-aligned Rene card, optionally marked as an error.
     */
    private static DialogBox createReneDialog(String message, boolean isError) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.avatarLabel.setText(RENE_AVATAR);
        dialogBox.senderLabel.setText(RENE_NAME);
        dialogBox.getStyleClass().add("rene-dialog");
        if (isError) {
            dialogBox.getStyleClass().add("error-dialog");
        }
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Places the avatar on the left for Rene's messages.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(children);
        getChildren().setAll(children);
    }

    /**
     * Clamps the width of this dialog's card so long responses wrap instead of
     * stretching the window. Called by the main window whenever the conversation
     * area is (re)laid out.
     *
     * @param maxWidth the maximum width for the card, in pixels.
     */
    public void setCardMaxWidth(double maxWidth) {
        bubbleCard.setMaxWidth(maxWidth);
    }
}
