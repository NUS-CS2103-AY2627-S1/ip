package axiom.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Represents a chat row that shows a user command, an AXIOM reply, or an error.
 * User commands are compact and right-aligned; AXIOM replies use the remaining width on the left.
 */
public class DialogBox extends HBox {
    private static final double USER_BUBBLE_WIDTH_RATIO = 0.78;
    private static final double AXIOM_BUBBLE_INSETS = 4.0;

    @FXML
    private Label dialog;

    private DialogBox(String text, String styleClass, Pos alignment, boolean isUserBubble) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DialogBox.fxml", e);
        }

        setAlignment(alignment);
        setMinWidth(0);
        setMinHeight(Region.USE_PREF_SIZE);
        setFillHeight(false);
        dialog.setText(text);
        dialog.getStyleClass().add(styleClass);
        bindBubbleWidth(isUserBubble);
    }

    /**
     * Binds the bubble's wrap width to this row so text reflows when the window is resized.
     *
     * @param isUserBubble Whether this bubble should stay compact on the right.
     */
    private void bindBubbleWidth(boolean isUserBubble) {
        if (isUserBubble) {
            dialog.maxWidthProperty().bind(widthProperty().multiply(USER_BUBBLE_WIDTH_RATIO));
            return;
        }

        dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialog, Priority.ALWAYS);
        dialog.prefWidthProperty().bind(widthProperty().subtract(AXIOM_BUBBLE_INSETS));
    }

    /**
     * Returns a compact, right-aligned dialog for a command typed by the user.
     *
     * @param text Command text.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "user-bubble", Pos.TOP_RIGHT, true);
    }

    /**
     * Returns a full-width, left-aligned dialog for a reply from AXIOM.
     *
     * @param text Reply text.
     * @return AXIOM dialog box.
     */
    public static DialogBox getAxiomDialog(String text) {
        return new DialogBox(text.strip(), "axiom-bubble", Pos.TOP_LEFT, false);
    }

    /**
     * Returns a left-aligned dialog that highlights an error.
     *
     * @param text Error description.
     * @return Error dialog box.
     */
    public static DialogBox getErrorDialog(String text) {
        return new DialogBox(text.strip(), "error-bubble", Pos.TOP_LEFT, false);
    }
}
