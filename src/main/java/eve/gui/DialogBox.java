package eve.gui;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * A single chat bubble: a message label next to a round, colored avatar
 * with a small glyph -- either something the user typed, Eve's normal
 * response to it, or an error message from Eve. Each speaker keeps the
 * same randomly-chosen avatar color for the whole session (see
 * {@link MainWindow}), since real avatar images aren't available; error
 * messages instead use a fixed alert color so they stand out regardless of
 * that session's colors. Built from DialogBox.fxml; use
 * {@link #getUserDialog}, {@link #getEveDialog}, or {@link #getErrorDialog}
 * rather than the constructor directly, since they also set the
 * alignment/avatar position/style that distinguishes the three.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_DIAMETER = 36;
    /** Bubble width as a fraction of the window's width, capped so it doesn't stretch full-width on wide windows. */
    private static final double BUBBLE_WIDTH_FRACTION = 0.72;
    private static final double MAX_BUBBLE_WIDTH = 480;
    private static final Color ERROR_AVATAR_COLOR = Color.web("#d32f2f");

    @FXML
    private Label dialog;
    @FXML
    private StackPane avatarPane;

    private DialogBox(String text, Color avatarColor, String avatarGlyph) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load DialogBox.fxml", e);
        }
        // FXMLLoader wires @FXML fields by matching fx:id in DialogBox.fxml; a typo'd
        // or removed fx:id would leave a field null here instead of failing the load,
        // surfacing later as a confusing NullPointerException. Assert it immediately.
        assert dialog != null && avatarPane != null : "DialogBox.fxml did not inject dialog/avatarPane";
        dialog.setText(text);
        avatarPane.getChildren().add(createAvatar(avatarColor, avatarGlyph));

        // Let this HBox fill the width the VBox conversation view gives it, and keep the
        // bubble's wrap width in proportion to that so bubbles reflow as the window is resized.
        setMaxWidth(Double.MAX_VALUE);
        dialog.maxWidthProperty().bind(
                Bindings.min(widthProperty().multiply(BUBBLE_WIDTH_FRACTION), MAX_BUBBLE_WIDTH));
    }

    /**
     * Returns a dialog box for something the user typed, with the avatar on
     * the outer (right) edge of the conversation.
     *
     * @param text the user's input.
     * @param avatarColor this session's color for the user's avatar.
     */
    public static DialogBox getUserDialog(String text, Color avatarColor) {
        DialogBox box = new DialogBox(text, avatarColor, "Y");
        box.setAlignment(Pos.CENTER_RIGHT);
        box.dialog.getStyleClass().add("user-dialog");
        box.flip();
        return box;
    }

    /**
     * Returns a dialog box for Eve's response, with the avatar on the
     * outer (left) edge of the conversation.
     *
     * @param text Eve's response.
     * @param avatarColor this session's color for Eve's avatar.
     */
    public static DialogBox getEveDialog(String text, Color avatarColor) {
        DialogBox box = new DialogBox(text, avatarColor, "⚡");
        box.setAlignment(Pos.CENTER_LEFT);
        box.dialog.getStyleClass().add("eve-dialog");
        return box;
    }

    /**
     * Returns a dialog box for an error message from Eve (e.g. an invalid
     * command), styled distinctly from a normal reply so it catches the
     * user's attention. Always uses a fixed alert color rather than this
     * session's Eve avatar color, so it reads as an error regardless of
     * which color Eve was randomly assigned.
     *
     * @param text the error message.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox box = new DialogBox(text, ERROR_AVATAR_COLOR, "!");
        box.setAlignment(Pos.CENTER_LEFT);
        box.dialog.getStyleClass().add("error-dialog");
        return box;
    }

    /** Swaps the avatar and message order, so the avatar ends up on the outer edge of a right-aligned dialog. */
    private void flip() {
        getChildren().setAll(dialog, avatarPane);
    }

    /** Builds a round, colored avatar with a single glyph centered in it. */
    private static StackPane createAvatar(Color color, String glyph) {
        Circle circle = new Circle(AVATAR_DIAMETER / 2, color);
        Text initial = new Text(glyph);
        initial.setFill(Color.WHITE);
        initial.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        StackPane pane = new StackPane(circle, initial);
        pane.setPrefSize(AVATAR_DIAMETER, AVATAR_DIAMETER);
        pane.setMinSize(AVATAR_DIAMETER, AVATAR_DIAMETER);
        pane.setMaxSize(AVATAR_DIAMETER, AVATAR_DIAMETER);
        return pane;
    }
}
