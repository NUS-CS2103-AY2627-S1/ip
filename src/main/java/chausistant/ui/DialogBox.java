package chausistant.ui;

import java.io.IOException;
import java.net.URL;

import chausistant.ChatResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Displays one compact, asymmetric message in the session activity feed. */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML = "/view/DialogBox.fxml";

    @FXML
    private Label dialog;
    @FXML
    private Label senderLabel;
    @FXML
    private VBox messageContent;

    private boolean isUserMessage;

    /**
     * Creates a dialog box containing the supplied message from FXML.
     *
     * @param text message to display
     */
    private DialogBox(String text) {
        URL fxmlLocation = DialogBox.class.getResource(DIALOG_BOX_FXML);
        if (fxmlLocation == null) {
            throw new IllegalStateException("Missing FXML resource: " + DIALOG_BOX_FXML);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load a dialog box.", error);
        }

        dialog.setText(text);
    }

    /** Creates a right-aligned dialog box for a user message. */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.isUserMessage = true;
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.senderLabel.setText("You");
        dialogBox.getStyleClass().add("user-message");
        return dialogBox;
    }

    /** Creates a left-aligned dialog box for a Chausistant response. */
    public static DialogBox getChausistantDialog(ChatResponse response) {
        DialogBox dialogBox = new DialogBox(response.text());
        dialogBox.senderLabel.setText("Chausistant");
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.getStyleClass().add("assistant-message");
        if (response.isError()) {
            dialogBox.getStyleClass().add("error-message");
        }
        return dialogBox;
    }

    /** Updates the largest useful width for this message's readable content. */
    public void setMaximumMessageWidth(double maximumMessageWidth) {
        messageContent.setMaxWidth(maximumMessageWidth);
        dialog.setMaxWidth(maximumMessageWidth);
    }

    /** Returns whether this dialog represents a message written by the user. */
    public boolean isUserMessage() {
        return isUserMessage;
    }
}
