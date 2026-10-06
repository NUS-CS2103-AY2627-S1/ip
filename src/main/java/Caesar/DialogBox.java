package caesar;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** Represents one conversation message and its speaker avatar. */
public class DialogBox extends HBox {
    private static final Pattern TASK_LINE_PATTERN = Pattern.compile("\\s*(?:- |\\d+\\. ).+");

    @FXML
    private TextFlow dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Label commandBadge;

    /** Creates a dialog box by loading the reusable FXML view. */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", exception);
        }

        setDialogText(text);
        displayPicture.setImage(image);
    }

    /** Sets the dialog text and emphasizes lines that represent tasks. */
    private void setDialogText(String text) {
        String[] lines = text.split("\\n", -1);
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            Text line = new Text(lines[lineIndex]);
            line.getStyleClass().add("dialog-text");
            if (TASK_LINE_PATTERN.matcher(lines[lineIndex]).matches()) {
                line.getStyleClass().add("task-line");
            }
            dialog.getChildren().add(line);
            if (lineIndex < lines.length - 1) {
                Text lineBreak = new Text("\n");
                lineBreak.getStyleClass().add("dialog-text");
                dialog.getChildren().add(lineBreak);
            }
        }
    }

    /** Flips this dialog so Caesar's avatar is displayed on the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /** Creates a dialog box for a message sent by the user. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        String commandWord = text.trim().split("\\s+", 2)[0];
        if (Caesar.CommandType.fromString(commandWord) != Caesar.CommandType.UNKNOWN) {
            dialogBox.setCommandBadge(commandWord.toLowerCase(Locale.ENGLISH));
        }
        return dialogBox;
    }

    /** Creates a dialog box for a message sent by Caesar. */
    public static DialogBox getCaesarDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /** Displays the recognized command keyword in this dialog. */
    private void setCommandBadge(String commandWord) {
        commandBadge.setText(commandWord);
        commandBadge.setManaged(true);
        commandBadge.setVisible(true);
    }
}
