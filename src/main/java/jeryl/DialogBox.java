package jeryl;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * A single chat bubble containing just a message Label. There are no
 * profile pictures: the conversation only ever has two fixed
 * participants (the user and Jeryl), so a repeated avatar next to every
 * line would just eat into the limited screen space without adding
 * information. Instead, user and Jeryl bubbles are told apart by
 * alignment and color alone: user bubbles are right-aligned, and
 * Jeryl's replies are left-aligned and flagged with an "error-label"
 * style when they're an error message, mirroring how the two sides of
 * the conversation are not actually symmetric.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load /view/DialogBox.fxml", e);
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.78));
    }

    /**
     * Creates a right-aligned dialog box representing user input.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.dialog.getStyleClass().add("user-label");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box representing one of Jeryl's
     * replies. Error replies get an extra "error-label" style so they
     * visually stand out from a normal reply.
     */
    public static DialogBox getJerylDialog(Jeryl.Response response) {
        DialogBox dialogBox = new DialogBox(response.text());
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.dialog.getStyleClass().add(response.isError() ? "error-label" : "reply-label");
        return dialogBox;
    }

    /**
     * Creates a left-aligned, non-error dialog box representing Jeryl's
     * reply, e.g. the welcome message shown before any user input.
     */
    public static DialogBox getJerylDialog(String text) {
        return getJerylDialog(new Jeryl.Response(text, false));
    }
}
