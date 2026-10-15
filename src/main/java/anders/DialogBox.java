package anders;

import java.io.IOException;
import java.util.List;

import anders.ui.Ui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** Represents a wrapping, copyable message in the Anders conversation. */
public class DialogBox extends HBox {
    @FXML
    private Label speaker;
    @FXML
    private Label message;
    @FXML
    private ProfileIcon avatar;
    @FXML
    private VBox bubble;

    private DialogBox(String speakerText, String messageText, boolean isUser) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog layout", exception);
        }
        speaker.setText(speakerText);
        // Console indentation wastes space in a narrow message bubble.
        message.setText(isUser ? messageText : messageText.replaceAll("(?m)^ +", ""));
        avatar.setUser(isUser);
        getStyleClass().add(isUser ? "user-dialog" : "bot-dialog");
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        bubble.maxWidthProperty().bind(widthProperty().subtract(40).multiply(isUser ? 0.9 : 1));
        if (isUser) {
            getChildren().setAll(bubble, avatar);
        }

        MenuItem copyMessage = new MenuItem("Copy message");
        copyMessage.setOnAction(event -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(message.getText());
            Clipboard.getSystemClipboard().setContent(content);
        });
        message.setContextMenu(new ContextMenu(copyMessage));
    }

    /**
     * Creates a right-aligned message written by the user.
     *
     * @param message the submitted command
     * @return a compact user message
     */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox("You", message, true);
    }

    /**
     * Creates a left-aligned message written by Anders.
     *
     * @param message the reply to display
     * @return a wrapping reply with console indentation removed
     */
    public static DialogBox getBotDialog(String message) {
        return new DialogBox(Ui.NAME, message, false);
    }

    /**
     * Creates a reply with bold section headings and plain-text copying.
     *
     * @param message the complete reply, including headings
     * @param headings the complete lines to emphasize
     * @return a reply with only the supplied headings in bold
     */
    public static DialogBox getBotDialog(String message, List<String> headings) {
        DialogBox dialog = getBotDialog(message);
        dialog.emphasizeHeadings(headings);
        return dialog;
    }

    /** Styles heading lines while retaining the label text for copying and accessibility. */
    private void emphasizeHeadings(List<String> headings) {
        TextFlow formattedMessage = new TextFlow();
        formattedMessage.getStyleClass().add("formatted-message");
        String[] lines = message.getText().split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            Text line = new Text(lines[i] + (i < lines.length - 1 ? "\n" : ""));
            if (headings.contains(lines[i])) {
                line.getStyleClass().add("command-heading");
            }
            formattedMessage.getChildren().add(line);
        }
        formattedMessage.prefWidthProperty().bind(message.widthProperty());
        message.setGraphic(formattedMessage);
        message.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

}
