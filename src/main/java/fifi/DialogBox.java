package fifi;

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
import javafx.scene.layout.VBox;

/**
 * Displays one chat message together with an avatar image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label text;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing a message and avatar image.
     *
     * @param message text to show in the dialog box
     * @param image avatar image to show beside the message
     */
    private DialogBox(String message, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load Fifi's message layout.", e);
        }
        text.setText(message);
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned user dialog.
     *
     * @param message user's message
     * @param image user's avatar image
     * @return a dialog box for the user's message
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.text.getStyleClass().add("user-message");
        return dialogBox;
    }

    /**
     * Creates a left-aligned Fifi dialog.
     *
     * @param message Fifi's message
     * @param image Fifi's avatar image
     * @return a dialog box for Fifi's message
     */
    public static DialogBox getFifiDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a Fifi dialog with a distinct appearance when the command failed.
     *
     * @param response message and error status supplied by Fifi
     * @param image Fifi's avatar image
     * @return a dialog box styled according to the response status
     */
    public static DialogBox getFifiDialog(ChatResponse response, Image image) {
        DialogBox dialogBox = getFifiDialog(response.getMessage(), image);
        if (response.isError()) {
            Label heading = new Label("Error");
            heading.getStyleClass().add("error-heading");
            dialogBox.text.getStyleClass().remove("chat-message");
            dialogBox.text.getStyleClass().add("error-message");
            // The container's width includes its padding, so long messages still fit the chat area.
            dialogBox.text.setMaxWidth(Double.MAX_VALUE);
            dialogBox.getChildren().remove(dialogBox.text);
            VBox errorBox = new VBox(6.0, heading, dialogBox.text);
            errorBox.getStyleClass().add("error-box");
            errorBox.setMinWidth(0.0);
            errorBox.setMaxWidth(250.0);
            dialogBox.getChildren().add(errorBox);
        }
        return dialogBox;
    }

    /**
     * Flips the dialog box so the avatar appears on the left.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> nodes = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(nodes);
        getChildren().setAll(nodes);
    }
}
