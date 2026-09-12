package bob.gui;

import java.io.IOException;

import bob.ResponseType;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * Displays one chatbot message together with the speaker's avatar.
 */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML = "/view/DialogBox.fxml";
    private static final double MAX_DIALOG_WIDTH_RATIO = 0.84;
    private static final Duration ENTRANCE_DURATION = Duration.millis(180);

    @FXML
    private Label dialog;

    private DialogBox(String text) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource(DIALOG_BOX_FXML));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
        dialog.getStyleClass().add("dialog-label");
        dialog.getStyleClass().add("user-label");
        dialog.maxWidthProperty().bind(widthProperty().multiply(MAX_DIALOG_WIDTH_RATIO));
    }

    /**
     * Creates a dialog displayed on the user's side.
     *
     * @param text message to display.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text);
    }

    /**
     * Creates a dialog displayed on Bob's side.
     *
     * @param text message to display.
     * @param responseType semantic type used to style the response.
     * @return Bob dialog box.
     */
    public static DialogBox getBobDialog(String text, ResponseType responseType) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.flip();
        dialogBox.applyResponseStyle(responseType);
        return dialogBox;
    }

    /**
     * Creates the temporary dialog displayed while Bob prepares a response.
     *
     * @param text initial typing-indicator text.
     * @return Bob typing dialog box.
     */
    public static DialogBox getTypingDialog(String text) {
        DialogBox dialogBox = getBobDialog(text, ResponseType.INFO);
        dialogBox.dialog.getStyleClass().add("typing-label");
        return dialogBox;
    }

    void setDialogText(String text) {
        dialog.setText(text);
    }

    void playEntranceAnimation() {
        double startingOffset = getAlignment() == Pos.TOP_LEFT ? -12.0 : 12.0;
        setOpacity(0.0);
        setTranslateX(startingOffset);

        FadeTransition fade = new FadeTransition(ENTRANCE_DURATION, this);
        fade.setToValue(1.0);
        TranslateTransition slide = new TranslateTransition(ENTRANCE_DURATION, this);
        slide.setToX(0.0);

        new ParallelTransition(fade, slide).play();
    }

    private void applyResponseStyle(ResponseType responseType) {
        dialog.getStyleClass().add(responseType.getStyleClass());
    }

    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().remove("user-label");
        dialog.getStyleClass().add("reply-label");
    }
}
