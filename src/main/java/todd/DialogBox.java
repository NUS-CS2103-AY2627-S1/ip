package todd;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** Displays one message together with a label identifying its speaker. */
public class DialogBox extends HBox {
    @FXML
    private Label speaker;
    @FXML
    private Label dialog;
    @FXML
    private VBox messageContainer;

    private DialogBox(String speakerName, String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout", e);
        }

        speaker.setText(speakerName);
        dialog.setText(text);
        // Leave a small gutter and let long messages wrap as the window changes size.
        messageContainer.maxWidthProperty().bind(widthProperty().multiply(0.9));
        dialog.maxWidthProperty().bind(messageContainer.maxWidthProperty());
    }

    /** Creates a right-aligned dialog containing a command from the user. */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox("You", text);
        box.messageContainer.setAlignment(Pos.TOP_RIGHT);
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /** Creates a left-aligned dialog containing Todd's response. */
    public static DialogBox getToddDialog(String text) {
        DialogBox box = new DialogBox("Todd", text);
        box.setAlignment(Pos.TOP_LEFT);
        box.getStyleClass().add("todd-dialog");
        return box;
    }
    /** Styles errors and help using response metadata rather than matching message wording. */
    public static DialogBox getToddDialog(Response response) {
        DialogBox box = getToddDialog(Ui.formatForGui(response.text()));
        if (response.error()) {
            box.getStyleClass().add("error-dialog");
            box.speaker.setText("Todd · Error");
        } else if (response.help()) {
            box.showHelp(Ui.formatForGui(response.text()));
        }
        return box;
    }

    /** Gives help headings and command lines emphasis while keeping each line responsive. */
    private void showHelp(String text) {
        VBox helpContent = new VBox(5);
        helpContent.getStyleClass().add("message");
        helpContent.setMinWidth(0);
        helpContent.maxWidthProperty().bind(messageContainer.maxWidthProperty());
        for (String line : text.split("\n", -1)) {
            TextFlow helpLine = new TextFlow();
            helpLine.setMinWidth(0);
            helpLine.setMinHeight(USE_PREF_SIZE);
            helpLine.maxWidthProperty().bind(messageContainer.maxWidthProperty().subtract(34));
            boolean emphasized = line.matches("[A-Z &]+") || line.matches(
                    "^(todo|deadline|event|list|find|on|reminders|mark|unmark|delete|hi|help|bye) .+");
            // The em dash separates the command syntax from its normal-weight explanation.
            int separator = line.indexOf(" — ");
            int boldEnd = emphasized ? (separator < 0 ? line.length() : separator) : 0;
            if (boldEnd > 0) {
                Text title = new Text(line.substring(0, boldEnd));
                title.getStyleClass().add("help-emphasis");
                helpLine.getChildren().add(title);
            }
            Text explanation = new Text(line.isEmpty() ? " " : line.substring(boldEnd));
            explanation.getStyleClass().add("help-text");
            helpLine.getChildren().add(explanation);
            helpContent.getChildren().add(helpLine);
        }
        messageContainer.getChildren().set(1, helpContent);
    }
}
