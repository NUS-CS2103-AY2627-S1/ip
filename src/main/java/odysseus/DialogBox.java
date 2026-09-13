package odysseus;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Represents one styled conversational entry in Odysseus's ship log.
 */
public class DialogBox extends HBox {
    @FXML
    private VBox messageBubble;
    @FXML
    private Label speaker;
    @FXML
    private Label message;
    @FXML
    private Label initial;

    private DialogBox(String text, boolean isUser) {
        loadView();
        message.setText(text);
        if (isUser) {
            configureTravelerEntry();
        } else {
            configureOdysseusEntry();
        }
    }

    /**
     * Creates a right-aligned entry for a traveler command.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, true);
    }

    /**
     * Creates a left-aligned entry for Odysseus's response.
     */
    public static DialogBox getOdysseusDialog(String text) {
        return new DialogBox(text, false);
    }

    /**
     * Creates a left-aligned correction entry for an invalid command.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialog = new DialogBox(text, false);
        dialog.configureErrorEntry();
        return dialog;
    }

    /**
     * Loads this reusable control from its FXML view.
     */
    private void loadView() {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Odysseus could not load a ship-log entry.", exception);
        }
    }

    /**
     * Applies the restrained editorial treatment used for Odysseus's entries.
     */
    private void configureOdysseusEntry() {
        speaker.setText("ODYSSEUS");
        initial.setText("O");
        setMaxWidth(Double.MAX_VALUE);
        messageBubble.setMaxWidth(Double.MAX_VALUE);
        message.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(messageBubble, Priority.ALWAYS);
        getStyleClass().add("odysseus-dialog");
        messageBubble.getStyleClass().add("odysseus-bubble");
    }

    /**
     * Applies the pottery-note treatment used for the traveler's entries.
     */
    private void configureTravelerEntry() {
        assert getChildren().size() == 2 : "The dialog FXML must provide a monogram and message bubble";
        speaker.setText("TRAVELER");
        getChildren().setAll(messageBubble);
        setAlignment(Pos.TOP_RIGHT);
        getStyleClass().add("user-dialog");
        messageBubble.getStyleClass().add("user-bubble");
    }

    /**
     * Emphasizes the correction while preserving Odysseus's side of the conversation.
     */
    private void configureErrorEntry() {
        speaker.setText("COURSE CORRECTION");
        initial.setText("!");
        getStyleClass().add("error-dialog");
        messageBubble.getStyleClass().add("error-bubble");
    }
}
