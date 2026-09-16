package bobby;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Represents a user command or a Bobby response in the conversation. */
public class DialogBox extends HBox {
    /** Maximum width of a Bobby response when the application window is expanded. */
    private static final double MAX_BOBBY_DIALOG_WIDTH = 720.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Label errorStatus;
    @FXML
    private VBox messageContainer;

    /** Creates a dialog box from its FXML view. */
    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
    }

    /** Formats this box as a compact, right-aligned user command. */
    private void formatAsUserDialog() {
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
        getStyleClass().add("user-dialog");
        messageContainer.setAlignment(Pos.TOP_RIGHT);
        dialog.getStyleClass().add("user-label");
        messageContainer.maxWidthProperty().bind(widthProperty().multiply(0.75));
    }

    /** Formats this box as a wide, left-aligned Bobby response. */
    private void formatAsBobbyDialog(Image image) {
        displayPicture.setImage(image);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("bobby-dialog");
        dialog.getStyleClass().add("reply-label");
        dialog.setMaxWidth(Double.MAX_VALUE);
        messageContainer.setMaxWidth(MAX_BOBBY_DIALOG_WIDTH);
        HBox.setHgrow(messageContainer, Priority.ALWAYS);
    }

    /** Applies a command-specific style to the chatbot's response label. */
    private void changeDialogStyle(String commandType, boolean isError) {
        if (isError) {
            errorStatus.setManaged(true);
            errorStatus.setVisible(true);
            dialog.getStyleClass().add("error-label");
            return;
        }
        if (commandType == null) {
            return;
        }
        switch (commandType) {
            case "AddCommand":
                dialog.getStyleClass().add("add-label");
                break;
            case "MarkCommand":
                dialog.getStyleClass().add("marked-label");
                break;
            case "UnmarkCommand":
                dialog.getStyleClass().add("unmarked-label");
                break;
            case "DeleteCommand":
                dialog.getStyleClass().add("delete-label");
                break;
            case "PriorityCommand":
                dialog.getStyleClass().add("priority-label");
                break;
            default:
                // Do nothing
        }
    }

    /**
     * Creates a compact user dialog without a profile image.
     *
     * @param text user command to display
     * @return right-aligned user dialog
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.formatAsUserDialog();
        return dialogBox;
    }

    /**
     * Creates a wide Bobby response with its command-specific style.
     *
     * @param text response text to display
     * @param image Bobby's profile image
     * @param commandType simple class name of the executed command, or {@code null} after an error
     * @param isError whether the response reports a command error
     * @return left-aligned Bobby response dialog
     */
    public static DialogBox getBobbyDialog(String text, Image image, String commandType, boolean isError) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.formatAsBobbyDialog(image);
        dialogBox.changeDialogStyle(commandType, isError);
        return dialogBox;
    }
}
