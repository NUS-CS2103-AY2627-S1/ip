package waddles;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * A single row in the chat history: a speech-bubble style {@link Label}
 * paired with a circular emoji avatar, defined in
 * {@code /view/DialogBox.fxml}. User messages are shown as-is; Waddles'
 * messages are produced via {@link #getWaddlesDialog} which mirrors the
 * layout so the avatar appears on the opposite side.
 */
public class DialogBox extends HBox {
    private static final String USER_EMOJI = "👤";
    private static final String WADDLES_EMOJI = "🐷";

    /** How much horizontal space (avatar + spacing + padding) to leave outside the bubble when sizing it. */
    private static final double NON_BUBBLE_WIDTH = 100.0;
    private static final double MIN_BUBBLE_WIDTH = 140.0;

    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    private DialogBox(String text, String emoji) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // The FXML layout is a bundled resource, not user input, so failing to load it
            // is a packaging bug rather than something the app can recover from at runtime.
            // Swallowing it here (as the original code did with e.printStackTrace()) would
            // leave `dialog`/`avatar` unset and continue with a half-built dialog box instead
            // of surfacing the real problem.
            throw new IllegalStateException("Failed to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        avatar.setText(emoji);
    }

    /**
     * Mirrors this dialog box so the avatar is on the left instead of the
     * right, used to visually distinguish Waddles' messages from the
     * user's.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
    }

    /**
     * Marks this dialog box as showing an error message (an invalid
     * command), applying a visually distinct style so it catches the user's
     * eye immediately instead of blending in with normal replies.
     */
    public void markAsError() {
        getStyleClass().add("error-dialog");
    }

    /**
     * Keeps the speech bubble's width in sync with the available window
     * width, so a wider window lets long replies use the extra space instead
     * of wrapping unnecessarily, while a narrower one still wraps cleanly
     * rather than overflowing or leaving the bubble stuck at a fixed size.
     *
     * @param containerWidth The width of the scrollable area the chat is shown in.
     */
    public void bindBubbleWidth(ObservableValue<Number> containerWidth) {
        dialog.maxWidthProperty().bind(Bindings.max(
                MIN_BUBBLE_WIDTH,
                Bindings.createDoubleBinding(
                        () -> containerWidth.getValue().doubleValue() - NON_BUBBLE_WIDTH, containerWidth)));
    }

    public static DialogBox getUserDialog(String text) {
        DialogBox db = new DialogBox(text, USER_EMOJI);
        db.getStyleClass().add("user-dialog");
        return db;
    }

    public static DialogBox getWaddlesDialog(String text) {
        DialogBox db = new DialogBox(text, WADDLES_EMOJI);
        db.flip();
        db.getStyleClass().add("waddles-dialog");
        return db;
    }
}
