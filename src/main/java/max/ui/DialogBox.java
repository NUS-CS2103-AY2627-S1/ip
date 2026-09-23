package max.ui;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
/**
 * A reusable dialog row displayed in the conversation area.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    private DialogBox(String text, boolean userMessage, boolean error) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load dialog box layout", e);
        }
        dialog.setText(text);
        getStyleClass().add("dialog-box");
        getStyleClass().add(userMessage ? "user-message" : "bot-message");
        if (error) {
            getStyleClass().add("error-message");
        }
        dialog.getStyleClass().add("message");
        dialog.setMaxWidth(Double.MAX_VALUE);
    }

    /** Creates a dialog row for user input. */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(Objects.requireNonNullElse(text, ""), true, false);
    }

    /** Creates a dialog row for Max's response. */
    public static DialogBox getMaxDialog(String text, Image image) {
        return getMaxDialog(text, image, false);
    }

    /** Creates a dialog row for Max's response, optionally styled as an error. */
    public static DialogBox getMaxDialog(String text, Image image, boolean error) {
        DialogBox dialogBox = new DialogBox(Objects.requireNonNullElse(text, ""), false, error);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        return dialogBox;
    }

}
