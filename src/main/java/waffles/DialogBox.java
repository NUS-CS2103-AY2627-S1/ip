package waffles;

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

/**
 * Represents one chat message and its speaker avatar.
 */
public class DialogBox extends HBox {
    /** The message text injected from the FXML view. */
    @FXML
    private Label dialog;

    /** The speaker avatar injected from the FXML view. */
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box from the reusable FXML view.
     *
     * @param text the message to display
     * @param image the speaker avatar
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the dialog box view.", exception);
        }
        assert dialog != null : "DialogBox.fxml must inject the dialog label";
        assert displayPicture != null : "DialogBox.fxml must inject the speaker image";
        dialog.setText(text);
        displayPicture.setImage(image);
        HBox.setHgrow(dialog, Priority.ALWAYS);
        dialog.setMaxWidth(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
    }

    /** Moves the avatar to the left for a chatbot response. */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
    }

    /** Applies the visual treatment used for a user message. */
    private void styleAsUserMessage() {
        setStyle("-fx-background-color: #dbeafe; -fx-background-radius: 14; "
                + "-fx-border-color: #bfdbfe; -fx-border-radius: 14;");
        dialog.setStyle("-fx-text-fill: #1e3a5f; -fx-font-size: 14px;");
    }

    /** Applies the visual treatment used for a Waffles response. */
    private void styleAsBotMessage() {
        setStyle("-fx-background-color: #ffffff; -fx-background-radius: 14; "
                + "-fx-border-color: #e5e7eb; -fx-border-radius: 14;");
        dialog.setStyle("-fx-text-fill: #332b22; -fx-font-size: 14px;");
    }

    /** Applies the attention-grabbing visual treatment used for an error. */
    private void styleAsErrorMessage() {
        setStyle("-fx-background-color: #fef2f2; -fx-background-radius: 14; "
                + "-fx-border-color: #fca5a5; -fx-border-radius: 14;");
        dialog.setStyle("-fx-text-fill: #991b1b; -fx-font-size: 14px; -fx-font-weight: bold;");
    }

    /** Creates a dialog box aligned as a user message. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.styleAsUserMessage();
        return dialogBox;
    }

    /** Creates a dialog box aligned as a Waffles response. */
    public static DialogBox getWafflesDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.styleAsBotMessage();
        return dialogBox;
    }

    /** Creates a Waffles response styled as an error message. */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.styleAsErrorMessage();
        return dialogBox;
    }
}
