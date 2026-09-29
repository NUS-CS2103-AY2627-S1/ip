package tasktracker;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import tasktracker.command.CommandType;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text.trim());
        displayPicture.setImage(img);

        // Ensures bubble height calculates dynamically to fit all text rows
        dialog.setMinHeight(Region.USE_PREF_SIZE);

        // Binds label max width to the DialogBox container width minus avatar and padding
        dialog.maxWidthProperty().bind(this.widthProperty().subtract(120.0));

        setupContextMenu();
    }

    /**
     * Extracts only the bulleted corrupted entries from the dialogue text.
     *
     * @param fullText The full dialogue string.
     * @return Formatted string containing only the corrupted lines, or the full text if none found.
     */
    private String extractCorruptedLines(String fullText) {
        String[] lines = fullText.split("\\R");
        StringBuilder errorLines = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("• Line") || trimmed.startsWith("- Line")) {
                errorLines.append(trimmed).append("\n");
            }
        }

        return errorLines.length() > 0 ? errorLines.toString().trim() : fullText;
    }

    /**
     * Attaches a right-click context menu allowing users to copy the dialog message text.
     */
    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        // If the box is an error dialogue containing lines, add specific copy option
        if (dialog.getText().contains("Line ")) {
            MenuItem copyErrorsItem = new MenuItem("Copy Corrupted Lines");
            copyErrorsItem.setOnAction(event -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(extractCorruptedLines(dialog.getText()));
                clipboard.setContent(content);
            });
            contextMenu.getItems().add(copyErrorsItem);
        }

        MenuItem copyAllItem = new MenuItem("Copy Full Message");
        copyAllItem.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(dialog.getText());
            clipboard.setContent(content);
        });
        contextMenu.getItems().add(copyAllItem);

        this.setOnContextMenuRequested(event ->
                contextMenu.show(this, event.getScreenX(), event.getScreenY())
        );
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates and returns a dialog box representing user input.
     *
     * @param text The input command string entered by the user.
     * @param img The profile picture representing the user.
     * @return A styled user {@code DialogBox}.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-label");
        return db;
    }

    /**
     * Applies custom CSS styling to the dialog bubble depending on command category and error state.
     *
     * @param commandType The parsed command type that generated this message.
     * @param isError True if the dialog represents an error or warning message; false otherwise.
     */
    private void changeDialogStyle(CommandType commandType, boolean isError) {
        if (isError) {
            dialog.getStyleClass().add("error-label");
            return;
        }

        if (commandType == null) {
            dialog.getStyleClass().add("info-label");
            return;
        }

        switch (commandType) {
            case TODO:
            case DEADLINE:
            case EVENT:
            case FIXED:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case UNMARK:
                dialog.getStyleClass().add("unmarked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            case UNDO:
                dialog.getStyleClass().add("restored-label");
                break;
            case HELP:
                dialog.getStyleClass().add("help-label");
                break;
            case LIST:
            case FIND:
            case BYE:
            default:
                dialog.getStyleClass().add("info-label");
                break;
        }
    }

    /**
     * Creates and returns a flipped dialog box representing TaskTracker's response.
     *
     * @param text The response string to display.
     * @param img The profile picture representing TaskTracker.
     * @param commandType The type of command that triggered this response.
     * @param isError True if the response is an error or startup warning; false otherwise.
     * @return A flipped and styled {@code DialogBox} for the chatbot.
     */
    public static DialogBox getTaskTrackerDialog(String text, Image img,
                                                 CommandType commandType, boolean isError) {
        var db = new DialogBox(text, img);
        db.flip();
        db.changeDialogStyle(commandType, isError);
        return db;
    }
}
