package judey.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a text flow containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final String CONSOLE_DIVIDER = "----------------------------------------";
    @FXML
    private StackPane dialogContainer;
    @FXML
    private Label plainDialog;
    @FXML
    private TextFlow richDialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img, boolean judeyMessage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            getChildren().add(new Label(fallbackMessage()));
            return;
        }

        String displayedText = judeyMessage ? frameWithDividers(text) : text;
        if (displayedText.contains("[[highlight]]")) {
            setRichDialogText(displayedText);
            plainDialog.setVisible(false);
            plainDialog.setManaged(false);
        } else {
            plainDialog.setText(displayedText);
            richDialog.setVisible(false);
            richDialog.setManaged(false);
        }
        displayPicture.setImage(img);
    }

    /** Gives every Judey GUI response one fixed divider line above and below its text. */
    static String frameWithDividers(String text) {
        String result = stripOuterDividers(text);
        return CONSOLE_DIVIDER + "\n" + result + "\n" + CONSOLE_DIVIDER;
    }

    /** Checks an unstyled GUI response without being affected by its divider wrapper. */
    static boolean isErrorResponse(String text) {
        return stripOuterDividers(text).startsWith("Oopsie!");
    }

    private static String stripOuterDividers(String text) {
        String result = text.trim();
        if (result.startsWith(CONSOLE_DIVIDER)) {
            result = result.substring(CONSOLE_DIVIDER.length()).stripLeading();
        }
        if (result.endsWith(CONSOLE_DIVIDER)) {
            result = result.substring(0, result.length() - CONSOLE_DIVIDER.length()).stripTrailing();
        }
        return result;
    }

    private void setRichDialogText(String text) {
        String start = "[[highlight]]";
        String end = "[[/highlight]]";
        int cursor = 0;
        while (cursor < text.length()) {
            int startIndex = text.indexOf(start, cursor);
            if (startIndex < 0) {
                addText(text.substring(cursor), false);
                return;
            }
            addText(text.substring(cursor, startIndex), false);
            int endIndex = text.indexOf(end, startIndex + start.length());
            if (endIndex < 0) {
                addText(text.substring(startIndex + start.length()), true);
                return;
            }
            addText(text.substring(startIndex + start.length(), endIndex), true);
            cursor = endIndex + end.length();
        }
    }

    private void addText(String value, boolean highlighted) {
        if (value.isEmpty()) {
            return;
        }
        Text text = new Text(value);
        text.getStyleClass().add("dialog-text");
        if (highlighted) {
            text.getStyleClass().add("error-highlight");
        }
        richDialog.getChildren().add(text);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        getActiveDialog().getStyleClass().add("reply-label");
    }

    /** Creates a dialog displaying text entered by the user. */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img, false);
    }

    /** Creates a dialog displaying a response from Judey. */
    public static DialogBox getJudeyDialog(String text, Image img) {
        var db = new DialogBox(text, img, true);
        db.flip();
        return db;
    }

    /** Creates a Judey response styled as an error. */
    public static DialogBox getErrorDialog(String text, Image img) {
        var db = getJudeyDialog(text, img);
        db.getActiveDialog().getStyleClass().add("error-label");
        return db;
    }

    /** Creates a Judey response styled as the welcome banner. */
    public static DialogBox getBannerDialog(String text, Image img) {
        var db = getJudeyDialog(text, img);
        db.getActiveDialog().getStyleClass().add("banner-label");
        return db;
    }

    /** Creates a Judey response styled as help content. */
    public static DialogBox getHelpDialog(String text, Image img) {
        var db = getJudeyDialog(text, img);
        db.getActiveDialog().getStyleClass().add("help-label");
        return db;
    }

    private javafx.scene.Node getActiveDialog() {
        return plainDialog.isManaged() ? plainDialog : richDialog;
    }

    /** Returns the user-facing message used when a dialog cannot be loaded. */
    static String fallbackMessage() {
        return "Judey could not display this message. Please restart the application.";
    }
}
