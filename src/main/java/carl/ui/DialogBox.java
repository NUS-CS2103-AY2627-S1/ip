package carl.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Represents a responsive chat bubble for the user, Carl, or an error message.
 */
public class DialogBox extends HBox {
    private static final Pattern INLINE_COMMAND_PATTERN = Pattern.compile("`([^`]+)`");
    private static final Pattern DATE_DETAIL_PATTERN = Pattern.compile(
            "(?i)\\[D]|\\b(?:by|from|to):\\s*[^\\n)]+");
    private static final Image USER_IMAGE = new Image(
            DialogBox.class.getResourceAsStream("/images/user.jpg"));
    private static final double AVATAR_RADIUS = 16;

    @FXML
    private TextFlow dialog;
    @FXML
    private Label speaker;
    @FXML
    private StackPane avatar;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Label avatarInitial;
    @FXML
    private VBox bubble;

    private DialogBox(String text, DialogType type) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Ui.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Carl could not load the dialog layout.", e);
        }

        configureAvatar(type);
        configureBubbleDirection(type);
        setDialogText(text, type);
        getStyleClass().add(type.styleClass);
        setAlignment(type.alignment);
        setMaxWidth(Double.MAX_VALUE);

        speaker.setText(type.heading);
        speaker.setManaged(type.showsHeading);
        speaker.setVisible(type.showsHeading);

        // Keep messages readable on wide windows without overflowing narrow ones.
        dialog.maxWidthProperty().bind(Bindings.min(560,
                Bindings.max(180, widthProperty().multiply(type.widthRatio).subtract(36))));
    }

    /** Shows the user's image or Carl's initial in a compact circular avatar. */
    private void configureAvatar(DialogType type) {
        boolean isUser = type == DialogType.USER;
        avatar.getStyleClass().add(isUser ? "user-avatar" : "bot-avatar");
        displayPicture.setManaged(isUser);
        displayPicture.setVisible(isUser);
        avatarInitial.setManaged(!isUser);
        avatarInitial.setVisible(!isUser);

        if (isUser) {
            displayPicture.setImage(USER_IMAGE);
            displayPicture.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
            HBox.setMargin(avatar, new Insets(0, 0, 0, 6));
        } else {
            HBox.setMargin(avatar, new Insets(0, 6, 0, 0));
        }
    }

    /** Places the avatar on the side from which the message originates. */
    private void configureBubbleDirection(DialogType type) {
        if (type == DialogType.USER) {
            getChildren().setAll(bubble, avatar);
        }
    }

    /**
     * Splits a message into styled text runs so commands and scheduling details can be accented
     * without reducing the readability of the rest of the message.
     *
     * @param message message to display.
     * @param type category of dialog being rendered.
     */
    private void setDialogText(String message, DialogType type) {
        TextStyle[] styles = new TextStyle[message.length()];
        Arrays.fill(styles, TextStyle.NORMAL);

        if (type == DialogType.USER) {
            markFirstCommand(message, styles);
        }
        markMatches(message, styles, INLINE_COMMAND_PATTERN, 1, TextStyle.COMMAND);
        markMatches(message, styles, DATE_DETAIL_PATTERN, 0, TextStyle.DATE);

        int runStart = 0;
        while (runStart < message.length()) {
            TextStyle style = styles[runStart];
            int runEnd = runStart + 1;
            while (runEnd < message.length() && styles[runEnd] == style) {
                runEnd++;
            }

            Text textRun = new Text(message.substring(runStart, runEnd));
            textRun.getStyleClass().addAll("dialog-text-run", style.styleClass);
            dialog.getChildren().add(textRun);
            runStart = runEnd;
        }
    }

    /** Marks the command word at the beginning of a user message. */
    private void markFirstCommand(String message, TextStyle[] styles) {
        int commandStart = 0;
        while (commandStart < message.length() && Character.isWhitespace(message.charAt(commandStart))) {
            commandStart++;
        }

        int commandEnd = commandStart;
        while (commandEnd < message.length() && !Character.isWhitespace(message.charAt(commandEnd))) {
            commandEnd++;
        }
        Arrays.fill(styles, commandStart, commandEnd, TextStyle.COMMAND);
    }

    /** Marks every captured region from a pattern with the requested text style. */
    private void markMatches(String message, TextStyle[] styles, Pattern pattern,
                             int group, TextStyle style) {
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            Arrays.fill(styles, matcher.start(group), matcher.end(group), style);
        }
    }

    /**
     * Creates a compact green bubble for text entered by the user.
     *
     * @param text text entered by the user.
     * @return a user-styled dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, DialogType.USER);
    }

    /**
     * Creates a neutral response bubble for Carl.
     *
     * @param text response generated by Carl.
     * @return a bot-styled dialog box.
     */
    public static DialogBox getBotDialog(String text) {
        return new DialogBox(text, DialogType.BOT);
    }

    /**
     * Creates a high-contrast red bubble for invalid commands and other command errors.
     *
     * @param text error message generated by Carl.
     * @return an error-styled dialog box.
     */
    public static DialogBox getErrorDialog(String text) {
        return new DialogBox(text, DialogType.ERROR);
    }

    /**
     * Defines the visual treatment and responsive width of each message category.
     */
    private enum DialogType {
        USER("user-dialog", "", Pos.TOP_RIGHT, false, 0.72),
        BOT("bot-dialog", "CARL", Pos.TOP_LEFT, true, 0.82),
        ERROR("error-dialog", "COMMAND ERROR", Pos.TOP_LEFT, true, 0.86);

        private final String styleClass;
        private final String heading;
        private final Pos alignment;
        private final boolean showsHeading;
        private final double widthRatio;

        DialogType(String styleClass, String heading, Pos alignment,
                   boolean showsHeading, double widthRatio) {
            this.styleClass = styleClass;
            this.heading = heading;
            this.alignment = alignment;
            this.showsHeading = showsHeading;
            this.widthRatio = widthRatio;
        }
    }

    /** Visual styles that may be applied to individual portions of a message. */
    private enum TextStyle {
        NORMAL("normal-text"),
        COMMAND("command-text"),
        DATE("date-accent");

        private final String styleClass;

        TextStyle(String styleClass) {
            this.styleClass = styleClass;
        }
    }
}
