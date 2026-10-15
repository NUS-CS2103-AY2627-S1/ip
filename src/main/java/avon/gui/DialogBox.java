package avon.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Displays one speaker icon and one message in the conversation.
 */
public class DialogBox extends HBox {
    private static final String AVON_ICON_PATH = "/images/quill-pen.svg";
    private static final String USER_ICON_PATH = "/images/user-profile.svg";
    private static final double AVON_DIALOG_MAX_WIDTH = 760.0;
    private static final double USER_DIALOG_MAX_WIDTH = 360.0;
    private static final Pattern NUMBERED_ITEM_PATTERN = Pattern.compile("^(\\d+\\.)(.*)$");

    @FXML
    private TextFlow dialog;

    @FXML
    private StackPane speakerBadge;

    private DialogBox(String text, String accessibleSpeakerName, String iconPath,
            boolean isNumberFormattingEnabled, boolean isAvonDialog) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box view.", exception);
        }
        setDialogText(text, isNumberFormattingEnabled);
        setDialogSizing(isAvonDialog);
        speakerBadge.setAccessibleText(accessibleSpeakerName);
        speakerBadge.getChildren().add(0, new SvgIcon(iconPath));
    }

    /**
     * Limits long messages while allowing short messages to use their preferred width.
     *
     * @param isAvonDialog whether this dialog contains an Avon response.
     */
    private void setDialogSizing(boolean isAvonDialog) {
        if (isAvonDialog) {
            dialog.setMaxWidth(AVON_DIALOG_MAX_WIDTH);
        } else {
            dialog.setMaxWidth(USER_DIALOG_MAX_WIDTH);
        }
    }

    /**
     * Creates a right-aligned dialog for the user.
     *
     * @param text the user's command.
     * @return the user dialog.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "You", USER_ICON_PATH, false, false);
    }

    /**
     * Creates a left-aligned dialog for Avon.
     *
     * @param text Avon's response.
     * @return Avon's dialog.
     */
    public static DialogBox getAvonDialog(String text) {
        return getAvonDialog(text, false);
    }

    /**
     * Creates a left-aligned dialog for Avon and highlights it when it reports an error.
     *
     * @param text Avon's response.
     * @param isError whether the response reports an error.
     * @return Avon's dialog.
     */
    public static DialogBox getAvonDialog(String text, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, "Avon", AVON_ICON_PATH, true, true);
        dialogBox.flip();
        dialogBox.getStyleClass().add("avon-dialog");
        if (isError) {
            dialogBox.getStyleClass().add("error-dialog");
        }
        return dialogBox;
    }

    /**
     * Populates the message while optionally styling numbered-list prefixes separately.
     *
     * @param text the complete message text.
     * @param isNumberFormattingEnabled whether numbered prefixes should be emphasized.
     */
    private void setDialogText(String text, boolean isNumberFormattingEnabled) {
        String[] lines = text.split("\\n", -1);
        for (int index = 0; index < lines.length; index++) {
            addDialogLine(lines[index], isNumberFormattingEnabled);
            if (index < lines.length - 1) {
                dialog.getChildren().add(new Text("\n"));
            }
        }
    }

    /**
     * Adds one message line, using a bold text node for a leading list number when applicable.
     *
     * @param line the message line to add.
     * @param isNumberFormattingEnabled whether numbered prefixes should be emphasized.
     */
    private void addDialogLine(String line, boolean isNumberFormattingEnabled) {
        Matcher numberedItemMatcher = NUMBERED_ITEM_PATTERN.matcher(line);
        if (!isNumberFormattingEnabled || !numberedItemMatcher.matches()) {
            dialog.getChildren().add(new Text(line));
            return;
        }

        Text number = new Text(numberedItemMatcher.group(1));
        number.getStyleClass().add("list-number");
        dialog.getChildren().addAll(number, new Text(numberedItemMatcher.group(2)));
    }

    /**
     * Places the speaker icon before the response and aligns it to the left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
