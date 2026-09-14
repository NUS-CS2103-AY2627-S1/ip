package kaya.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays one chat message together with a label identifying its speaker.
 */
public class DialogBox extends HBox {
    /**
     * Keeps user messages narrower so the two sides are easy to distinguish.
     */
    private static final double USER_WIDTH_RATIO = 0.8;

    @FXML
    private Label dialog;

    @FXML
    private Label speakerLabel;

    @FXML
    private VBox messageContainer;

    /**
     * Loads the reusable dialog layout and fills it with one message.
     *
     * @param text the message to display
     * @param speaker the speaker label
     */
    private DialogBox(String text, String speaker) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box layout", exception);
        }
        dialog.setText(text);
        speakerLabel.setText(speaker);
    }

    /**
     * Creates a right-aligned dialog for the user.
     *
     * @param text the user's message
     * @return the user dialog
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "YOU");
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.messageContainer.setAlignment(Pos.TOP_RIGHT);
        dialogBox.messageContainer.maxWidthProperty().bind(dialogBox.widthProperty().multiply(USER_WIDTH_RATIO));
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Kaya.
     *
     * @param text Kaya's response
     * @return Kaya's dialog
     */
    public static DialogBox getKayaDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "KAYA");
        dialogBox.getStyleClass().add("kaya-dialog");
        dialogBox.messageContainer.maxWidthProperty().bind(dialogBox.widthProperty());
        return dialogBox;
    }
}
