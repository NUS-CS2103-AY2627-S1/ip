package bobby.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays a user command or a response from Bobby.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    @FXML
    private Label speakerLabel;

    @FXML
    private VBox messageContainer;

    /**
     * Creates a dialog box backed by its FXML view.
     *
     * @param message the message to display.
     * @param image Bobby's profile image, or {@code null} for a user message.
     */
    private DialogBox(String message, Image image) {
        URL dialogBoxView = Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "Missing dialog-box FXML resource");
        FXMLLoader fxmlLoader = new FXMLLoader(dialogBoxView);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        dialog.setText(formatForDisplay(message));
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog box for a user message.
     *
     * @param message the user's message.
     * @return the user dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message, null);
        dialogBox.configureAsUserDialog();
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for a Bobby response.
     *
     * @param message Bobby's response.
     * @param image Bobby's profile image.
     * @param isError whether the response describes an invalid command.
     * @return the Bobby dialog box.
     */
    public static DialogBox getBobbyDialog(String message, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.dialog.getStyleClass().add(isError ? "error-message" : "bobby-message");
        if (isError) {
            dialogBox.getStyleClass().add("error-row");
            dialogBox.speakerLabel.setText("LORD BOBBY  ·  IMPROPER DECREE");
        }
        return dialogBox;
    }

    /**
     * Configures the compact, right-aligned appearance used for user commands.
     */
    private void configureAsUserDialog() {
        setAlignment(Pos.TOP_RIGHT);
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
        speakerLabel.setManaged(false);
        speakerLabel.setVisible(false);
        messageContainer.setAlignment(Pos.TOP_RIGHT);
        dialog.getStyleClass().add("user-message");
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.78));
    }

    /**
     * Removes console-only leading indentation while preserving meaningful line breaks.
     *
     * @param message the response formatted for the console.
     * @return the response formatted for the graphical interface.
     */
    private static String formatForDisplay(String message) {
        return message.replaceAll("(?m)^ {5}", "");
    }
}
