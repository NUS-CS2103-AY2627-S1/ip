package pixel.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents one FXML-backed message in the Pixel conversation.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private Label speaker;

    private DialogBox(String text, String speakerName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a Pixel dialog box.", exception);
        }

        dialog.setText(text);
        speaker.setText(speakerName);
    }

    /**
     * Creates a right-aligned dialog for user input.
     *
     * @param text User message.
     * @return Dialog box representing the user's message.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "PLAYER 1");
        dialogBox.getStyleClass().add("user-message");
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.speaker.setManaged(false);
        dialogBox.speaker.setVisible(false);
        dialogBox.dialog.setMaxWidth(360);
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Pixel's response.
     *
     * @param text Pixel response.
     * @return Dialog box representing Pixel's response.
     */
    public static DialogBox getPixelDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "PIXEL // GUIDE");
        dialogBox.getStyleClass().add("pixel-message");
        dialogBox.dialog.setMaxWidth(Double.MAX_VALUE);
        return dialogBox;
    }

    /**
     * Creates a prominent dialog for an invalid command or argument.
     *
     * @param text Error response from Pixel.
     * @return Dialog box using Pixel's error presentation.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "PIXEL // ALERT");
        dialogBox.getStyleClass().addAll("pixel-message", "error-message");
        dialogBox.dialog.setMaxWidth(Double.MAX_VALUE);
        return dialogBox;
    }
}
