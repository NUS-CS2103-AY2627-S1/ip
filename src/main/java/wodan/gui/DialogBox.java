package wodan.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A chat row for a user command, a Wodan reply, or an error.
 * User rows are compact command chips; Wodan rows use the full width so long replies stay readable.
 */
public class DialogBox extends HBox {
    private static final double MIN_WRAP_WIDTH = 140;
    private static final double BUBBLE_HORIZONTAL_INSET = 28;
    private static final double USER_WIDTH_SHARE = 0.78;
    private static final double REPLY_WIDTH_SHARE = 1.0;
    private static final String ERROR_CAPTION = "The ravens protest";

    @FXML
    private VBox bubble;
    @FXML
    private Label caption;
    @FXML
    private Label dialog;

    private double widthShare = REPLY_WIDTH_SHARE;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load a dialog box.", e);
        }

        dialog.setText(text);
        dialog.setMinHeight(Region.USE_PREF_SIZE);
        caption.setMinHeight(Region.USE_PREF_SIZE);
        setMaxWidth(Double.MAX_VALUE);
        widthProperty().addListener((obs, oldWidth, newWidth) -> {
            applyWrapWidth(newWidth.doubleValue());
        });
    }

    /**
     * Returns a compact, right-aligned chip for the command the user typed.
     *
     * @param text Command typed by the user.
     * @return Dialog row aligned to the right.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.widthShare = USER_WIDTH_SHARE;
        dialogBox.applyWrapWidth(dialogBox.getWidth());
        return dialogBox;
    }

    /**
     * Returns a full-width card for Wodan's reply.
     *
     * @param text Reply from Wodan.
     * @return Dialog row aligned to the left.
     */
    public static DialogBox getWodanDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.getStyleClass().add("wodan-dialog");
        dialogBox.styleAsReplyCard();
        return dialogBox;
    }

    /**
     * Returns a full-width card styled to draw attention to an error.
     *
     * @param text Error text from Wodan.
     * @return Dialog row aligned to the left, with an error caption.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.getStyleClass().add("error-dialog");
        dialogBox.caption.setText(ERROR_CAPTION);
        dialogBox.caption.setVisible(true);
        dialogBox.caption.setManaged(true);
        dialogBox.styleAsReplyCard();
        return dialogBox;
    }

    private void styleAsReplyCard() {
        setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(bubble, Priority.ALWAYS);
        bubble.setMaxWidth(Double.MAX_VALUE);
        applyWrapWidth(getWidth());
    }

    private void applyWrapWidth(double rowWidth) {
        if (rowWidth <= 0) {
            return;
        }
        double wrapWidth = Math.max(MIN_WRAP_WIDTH, rowWidth * widthShare - BUBBLE_HORIZONTAL_INSET);
        dialog.setMaxWidth(wrapWidth);
        caption.setMaxWidth(wrapWidth);
    }
}
