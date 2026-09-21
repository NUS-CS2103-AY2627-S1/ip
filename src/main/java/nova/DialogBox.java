package nova;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one user or Nova message in the conversation.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.78));
    }

    /**
     * Creates a right-aligned user message.
     *
     * @param text Message text.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.dialog.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned Nova message.
     *
     * @param text Message text.
     * @return Nova dialog box.
     */
    public static DialogBox getNovaDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.dialog.getStyleClass().add("nova-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned Nova error message.
     *
     * @param text Error text.
     * @return Error dialog box.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getNovaDialog(text);
        dialogBox.dialog.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
