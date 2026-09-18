package anby;

import java.io.InputStream;
import java.util.regex.Pattern;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * A dialog box containing one chat message.
 */
public class DialogBox extends HBox {
    private static final String USER_AVATAR_PATH = "/images/user.jpg";
    private static final String ANBY_AVATAR_PATH = "/images/anby.jpg";
    private static final double AVATAR_SIZE = 88.0;
    private static final double MESSAGE_MAX_WIDTH = 260.0;
    private static final double SUGGESTION_COMMAND_MAX_WIDTH = 205.0;
    private static final String TRY_PREFIX = "try: ";
    private static final Pattern TASK_LINE_PATTERN = Pattern.compile("^(\\d+\\. )?\\[[TDE]]\\[[ X]] .+$");

    private final Region dialog;
    private final StackPane avatar;

    /**
     * Creates a dialog box.
     *
     * @param text message text
     * @param avatarPath image path for the avatar
     * @param fallbackText text to show if the image does not exist
     */
    public DialogBox(String text, String avatarPath, String fallbackText) {
        dialog = createMessageContent(text);
        avatar = createAvatar(avatarPath, fallbackText);

        dialog.setMaxWidth(MESSAGE_MAX_WIDTH);
        dialog.setMinHeight(Region.USE_PREF_SIZE);
        avatar.setAlignment(Pos.CENTER);

        getStyleClass().add("dialog-box");
        setFillHeight(false);
        dialog.getStyleClass().add("dialog-label");
        avatar.getStyleClass().add("avatar-label");
        getChildren().addAll(dialog, avatar);
    }

    /**
     * Returns a user dialog box.
     *
     * @param text user input
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, USER_AVATAR_PATH, "You");
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.dialog.getStyleClass().add("user-label");
        dialogBox.avatar.getStyleClass().add("user-avatar");
        return dialogBox;
    }

    /**
     * Returns an Anby dialog box.
     *
     * @param text Anby's response
     * @return Anby dialog box
     */
    public static DialogBox getAnbyDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, ANBY_AVATAR_PATH, "A");
        dialogBox.moveAvatarToLeft();
        dialogBox.dialog.getStyleClass().add("anby-label");
        dialogBox.avatar.getStyleClass().add("anby-avatar");
        return dialogBox;
    }

    private static Region createMessageContent(String text) {
        if (!hasFormattedLine(text)) {
            Label label = new Label(text);
            label.setWrapText(true);
            return label;
        }

        VBox message = new VBox(4);
        message.getStyleClass().add("formatted-message");
        String[] lines = text.split("\\R", -1);

        for (String line : lines) {
            message.getChildren().add(createLineContent(line));
        }

        return message;
    }

    private static boolean hasFormattedLine(String text) {
        for (String line : text.split("\\R")) {
            if (line.startsWith(TRY_PREFIX) || TASK_LINE_PATTERN.matcher(line).matches()) {
                return true;
            }
        }

        return false;
    }

    private static Region createLineContent(String line) {
        if (line.startsWith(TRY_PREFIX)) {
            return createSuggestionLine(line);
        } else if (TASK_LINE_PATTERN.matcher(line).matches()) {
            return createCodeBlockLine(line);
        }

        Label label = new Label(line);
        label.setWrapText(true);
        return label;
    }

    private static HBox createSuggestionLine(String line) {
        HBox suggestionLine = new HBox(3);
        suggestionLine.setAlignment(Pos.CENTER_LEFT);
        suggestionLine.getStyleClass().add("suggestion-line");

        Label prefix = new Label("try");
        prefix.getStyleClass().add("suggestion-prefix");

        Label separator = new Label(": ");

        Label command = new Label(line.substring(TRY_PREFIX.length()));
        command.setMaxWidth(SUGGESTION_COMMAND_MAX_WIDTH);
        command.setWrapText(true);
        command.getStyleClass().add("suggestion-code");

        suggestionLine.getChildren().addAll(prefix, separator, command);
        return suggestionLine;
    }

    private static Label createCodeBlockLine(String line) {
        Label label = new Label(line);
        label.setMaxWidth(MESSAGE_MAX_WIDTH);
        label.setWrapText(true);
        label.getStyleClass().add("task-code-line");
        return label;
    }

    private static StackPane createAvatar(String imagePath, String fallbackText) {
        StackPane avatarContainer = new StackPane();
        avatarContainer.setMinSize(AVATAR_SIZE, AVATAR_SIZE);
        avatarContainer.setPrefSize(AVATAR_SIZE, AVATAR_SIZE);
        avatarContainer.setMaxSize(AVATAR_SIZE, AVATAR_SIZE);
        avatarContainer.getStyleClass().add("avatar-label");
        avatarContainer.getChildren().add(loadAvatarContent(imagePath, fallbackText));
        return avatarContainer;
    }

    private static Node loadAvatarContent(String imagePath, String fallbackText) {
        InputStream imageStream = DialogBox.class.getResourceAsStream(imagePath);

        if (imageStream == null) {
            Label fallbackAvatar = new Label(fallbackText);
            fallbackAvatar.getStyleClass().add("fallback-avatar-text");
            return fallbackAvatar;
        }

        Image image = new Image(imageStream);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(AVATAR_SIZE);
        imageView.setFitWidth(AVATAR_SIZE);
        imageView.setPreserveRatio(false);
        imageView.setViewport(getCenteredSquareViewport(image));
        imageView.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        imageView.setSmooth(true);
        return imageView;
    }

    private static Rectangle2D getCenteredSquareViewport(Image image) {
        double size = Math.min(image.getWidth(), image.getHeight());
        double x = (image.getWidth() - size) / 2;
        double y = (image.getHeight() - size) / 2;
        return new Rectangle2D(x, y, size, size);
    }

    private void moveAvatarToLeft() {
        getChildren().remove(avatar);
        getChildren().add(0, avatar);
        setAlignment(Pos.TOP_LEFT);
    }
}
