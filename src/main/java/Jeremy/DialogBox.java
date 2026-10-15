package jeremy;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** A chat message displayed in the conversation area. */
public class DialogBox extends HBox {

    private static final String USER_MESSAGE_STYLE = "-fx-background-color: " + RetroTheme.OLIVE + ";"
            + " -fx-text-fill: " + RetroTheme.PAPER + "; -fx-font-family: " + RetroTheme.FONT + ";"
            + " -fx-font-size: 13px; -fx-padding: 9px; -fx-border-color: " + RetroTheme.BORDER + ";"
            + " -fx-border-width: 0 0 1px 1px;";
    private static final String JEREMY_MESSAGE_STYLE = "-fx-background-color: " + RetroTheme.BURGUNDY + ";"
            + " -fx-text-fill: " + RetroTheme.PAPER + "; -fx-font-family: " + RetroTheme.FONT + ";"
            + " -fx-font-size: 13px; -fx-padding: 9px; -fx-border-color: " + RetroTheme.BORDER + ";"
            + " -fx-border-width: 0 1px 1px 0;";
    private static final String ERROR_MESSAGE_STYLE = "-fx-background-color: " + RetroTheme.ERROR + ";"
            + " -fx-text-fill: #fff4f0; -fx-font-family: " + RetroTheme.FONT + ";"
            + " -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 9px;"
            + " -fx-border-color: " + RetroTheme.ERROR_BORDER + "; -fx-border-width: 1px;";

    private final Label text;

    /** Creates a right-aligned dialog box for the user. */
    private DialogBox(String message) {
        assert message != null : "Dialog message must not be null";
        text = new Label(message);
        text.setWrapText(true);
        text.setMaxWidth(320.0);
        text.setStyle(USER_MESSAGE_STYLE);
        setAlignment(Pos.TOP_RIGHT);
        setPadding(new Insets(4.0, 8.0, 4.0, 8.0));
        getChildren().add(text);
    }

    /** Flips the dialog box to the left for Jeremy's response. */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        text.setStyle(JEREMY_MESSAGE_STYLE);
    }

    /** Changes this dialog box to the warning style used for errors. */
    private void markAsError() {
        setAlignment(Pos.TOP_LEFT);
        text.setText("⚠ " + text.getText());
        text.setStyle(ERROR_MESSAGE_STYLE);
    }

    /** Creates a dialog box representing the user. */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox(message);
    }

    /** Creates a dialog box representing Jeremy. */
    public static DialogBox getJeremyDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.flip();
        return dialogBox;
    }

    /** Creates a visually distinct dialog box for an error response. */
    public static DialogBox getErrorDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.markAsError();
        return dialogBox;
    }
}
