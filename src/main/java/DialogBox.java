import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * A reusable conversation message displayed in the StanVard window.
 */
public class DialogBox extends HBox {
    private static final String ERROR_PREFIX = "OOPS!!!";

    @FXML
    private Label dialog;

    /**
     * Creates a message row containing the supplied text.
     *
     * @param text message to display
     */
    public DialogBox(String text) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog layout.", exception);
        }
        dialog.setText(text);
        getStyleClass().add(text.startsWith(ERROR_PREFIX) ? "error-message" : "normal-message");
    }
}
