package duck.ui;

import java.io.IOException;
import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays one wrapped message and an optional avatar aligned according to its speaker.
 */
public class DialogBox extends HBox {
    /** Largest width allowed for a message bubble, in pixels. */
    private static final double MESSAGE_WIDTH_MAXIMUM = 560.0;

    /** Fraction of the available dialog width that a message may occupy. */
    private static final double MESSAGE_WIDTH_RATIO = 0.72;

    /** Label identifying the speaker for this message. */
    @FXML
    private Label speaker;

    /** Text shown in this dialog box. */
    @FXML
    private Label text;

    /** Vertical container holding the speaker label and message bubble. */
    @FXML
    private VBox messageContainer;

    /** Avatar shown beside the message. */
    @FXML
    private ImageView displayPicture;

    /** Creates a dialog box from FXML containing the supplied message and avatar. */
    private DialogBox(String message, Image image) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "Dialog box FXML resource is missing."));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        this.text.setText(message);
        this.text.maxWidthProperty().bind(Bindings.min(
                this.widthProperty().multiply(MESSAGE_WIDTH_RATIO), MESSAGE_WIDTH_MAXIMUM));
        this.displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog box for a user command.
     *
     * @param message User command to display.
     * @param image User avatar to display.
     * @return right-aligned user dialog box
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.messageContainer.setAlignment(Pos.TOP_RIGHT);
        dialogBox.speaker.setText("YOU");
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.speaker.getStyleClass().add("user-speaker");
        dialogBox.text.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for a Duck response.
     *
     * @param message Duck response to display.
     * @param image Duck avatar to display.
     * @return left-aligned Duck dialog box
     */
    public static DialogBox getDuckDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.messageContainer.setAlignment(Pos.TOP_LEFT);
        dialogBox.speaker.setText("DUCK");
        dialogBox.getStyleClass().add("duck-dialog");
        dialogBox.speaker.getStyleClass().add("duck-speaker");
        dialogBox.text.getStyleClass().add("duck-bubble");
        dialogBox.getChildren().setAll(dialogBox.displayPicture, dialogBox.messageContainer);
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box that draws attention to an error response.
     *
     * @param message Error response to display.
     * @param image Duck avatar to display.
     * @return left-aligned error dialog box
     */
    public static DialogBox getErrorDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.messageContainer.setAlignment(Pos.TOP_LEFT);
        dialogBox.speaker.setText("⚠ ERROR");
        dialogBox.getStyleClass().add("error-dialog");
        dialogBox.speaker.getStyleClass().add("error-speaker");
        dialogBox.text.getStyleClass().add("error-bubble");
        dialogBox.getChildren().setAll(dialogBox.displayPicture, dialogBox.messageContainer);
        return dialogBox;
    }
}
