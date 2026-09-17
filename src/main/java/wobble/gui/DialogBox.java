package wobble.gui;

import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** Represents one user or Wobble message in the conversation. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 32;
    private static final double AVATAR_FRAME_SIZE = 36;
    private static final double MESSAGE_GAP = 8;
    private static final double MESSAGE_HORIZONTAL_SPACE = 32;
    private static final String REGULAR_TEXT_STYLE = "-fx-font-style: normal;";
    private static final Pattern TASK_STATUS_TAG_PATTERN = Pattern.compile("\\[(OVERDUE|PAST)\\]");
    private static final Pattern TASK_LINE_PATTERN = Pattern.compile("^\\d+\\. .+");
    private static final Image WOBBLE_AVATAR = loadImage("/images/wobble-avatar.png");
    private static final Image USER_AVATAR = loadImage("/images/user-avatar.png");
    private final Label messageLabel;

    private DialogBox(String text, String styleClass, Pos alignment, Image avatar,
            String avatarStyleClass) {
        this(createMessageLabel(text), styleClass, alignment, avatar, avatarStyleClass);
    }

    private DialogBox(Region message, String styleClass, Pos alignment, Image avatar,
            String avatarStyleClass) {
        messageLabel = message instanceof Label ? (Label) message : null;
        message.getStyleClass().add(styleClass);
        message.setStyle(REGULAR_TEXT_STYLE);
        StackPane avatarFrame = createAvatarFrame(avatar, avatarStyleClass);
        setAlignment(alignment);
        setSpacing(MESSAGE_GAP);
        setMaxWidth(Double.MAX_VALUE);
        message.maxWidthProperty().bind(Bindings.max(1,
                widthProperty().subtract(MESSAGE_HORIZONTAL_SPACE + AVATAR_FRAME_SIZE + MESSAGE_GAP)));
        HBox.setHgrow(message, Priority.NEVER);
        getStyleClass().add("dialog-box");
        if (alignment == Pos.TOP_LEFT) {
            getChildren().addAll(avatarFrame, message);
        } else {
            getChildren().addAll(message, avatarFrame);
        }
    }

    /** Creates a message displayed as sent by the user. */
    public static DialogBox userMessage(String text) {
        return new DialogBox(text, "user-message", Pos.TOP_RIGHT, USER_AVATAR, "user-avatar");
    }

    /** Creates a message displayed as sent by Wobble. */
    public static DialogBox botMessage(String text) {
        return new DialogBox(createStyledMessageContent(text), "bot-message", Pos.TOP_LEFT,
                WOBBLE_AVATAR, "wobble-avatar");
    }

    /** Creates a Wobble error message with styling that draws attention. */
    public static DialogBox errorMessage(String text) {
        return new DialogBox(text, "error-message", Pos.TOP_LEFT, WOBBLE_AVATAR, "wobble-avatar");
    }

    /** Creates a robot-console message shown while Wobble powers down. */
    public static DialogBox shutdownMessage(String text) {
        return new DialogBox(text, "shutdown-message", Pos.TOP_LEFT, WOBBLE_AVATAR, "wobble-avatar");
    }

    /** Updates the text in a shutdown card while Wobble's power-down sequence is running. */
    void updateShutdownMessage(String text) {
        if (messageLabel != null) {
            messageLabel.setText(text);
        }
    }

    /** Creates one bot card with individually styled help headings and command formats. */
    public static DialogBox helpMessage(String text) {
        return new DialogBox(createHelpContent(text), "bot-message", Pos.TOP_LEFT,
                WOBBLE_AVATAR, "wobble-avatar");
    }

    /** Creates one bot card with separated, lightly padded task rows. */
    public static DialogBox taskListMessage(String text) {
        return new DialogBox(createTaskListContent(text), "bot-message", Pos.TOP_LEFT,
                WOBBLE_AVATAR, "wobble-avatar");
    }

    /** Creates the structured content used inside the single help response card. */
    private static VBox createHelpContent(String text) {
        VBox content = new VBox(4);
        for (String line : text.split("\\R")) {
            if (line.isBlank()) {
                continue;
            }
            Label lineLabel = new Label(line.trim());
            lineLabel.setWrapText(true);
            lineLabel.setStyle(REGULAR_TEXT_STYLE);
            if (isHeading(line)) {
                lineLabel.getStyleClass().add("help-heading");
                VBox.setMargin(lineLabel, new Insets(8, 0, 0, 0));
            } else if (line.startsWith("  ")) {
                lineLabel.getStyleClass().add("help-command");
            } else {
                lineLabel.getStyleClass().add("help-description");
            }
            content.getChildren().add(lineLabel);
        }
        return content;
    }

    /** Converts a list response into readable headings, task rows, and detail lines. */
    private static VBox createTaskListContent(String text) {
        VBox content = new VBox(6);
        content.getStyleClass().add("task-list-content");
        VBox currentTaskRow = null;
        for (String line : text.split("\\R")) {
            if (line.isBlank()) {
                continue;
            }
            if (TASK_LINE_PATTERN.matcher(line).matches()) {
                currentTaskRow = new VBox(2);
                currentTaskRow.getStyleClass().add("task-list-row");
                TextFlow taskLine = createStyledMessageContent(line);
                taskLine.getStyleClass().add("task-line");
                currentTaskRow.getChildren().add(taskLine);
                content.getChildren().add(currentTaskRow);
            } else if (line.startsWith("    ") && currentTaskRow != null) {
                Label details = new Label(line.trim());
                details.setWrapText(true);
                details.setStyle(REGULAR_TEXT_STYLE);
                details.getStyleClass().add("task-details");
                currentTaskRow.getChildren().add(details);
            } else {
                Label heading = new Label(line.trim());
                heading.setWrapText(true);
                heading.setStyle(REGULAR_TEXT_STYLE);
                heading.getStyleClass().add(line.startsWith("Nothing")
                        ? "task-list-empty" : "task-list-heading");
                content.getChildren().add(heading);
            }
        }
        return content;
    }

    /** Returns whether a help line is an uppercase section heading. */
    private static boolean isHeading(String line) {
        String trimmedLine = line.trim();
        return !trimmedLine.isEmpty() && trimmedLine.equals(trimmedLine.toUpperCase(Locale.ROOT));
    }

    /** Creates a regular message label with wrapping enabled. */
    private static Label createMessageLabel(String text) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setStyle(REGULAR_TEXT_STYLE);
        return message;
    }

    /** Creates bot content with red styling for overdue and past task markers. */
    private static TextFlow createStyledMessageContent(String text) {
        TextFlow content = new TextFlow();
        Matcher matcher = TASK_STATUS_TAG_PATTERN.matcher(text);
        int previousEnd = 0;
        while (matcher.find()) {
            addMessageText(content, text.substring(previousEnd, matcher.start()), "message-text");
            Text statusTag = new Text(matcher.group());
            statusTag.getStyleClass().add("task-status-tag");
            content.getChildren().add(statusTag);
            previousEnd = matcher.end();
        }
        addMessageText(content, text.substring(previousEnd), "message-text");
        return content;
    }

    /** Adds a text segment to a styled bot message when the segment is non-empty. */
    private static void addMessageText(TextFlow content, String text, String styleClass) {
        if (text.isEmpty()) {
            return;
        }
        Text messageText = new Text(text);
        messageText.getStyleClass().add(styleClass);
        messageText.setStyle(REGULAR_TEXT_STYLE);
        content.getChildren().add(messageText);
    }

    /** Creates a compact circular avatar frame for a conversation participant. */
    private static StackPane createAvatarFrame(Image avatar, String styleClass) {
        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(AVATAR_SIZE);
        avatarView.setFitHeight(AVATAR_SIZE);
        avatarView.setPreserveRatio(true);
        avatarView.setSmooth(true);
        avatarView.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));

        StackPane avatarFrame = new StackPane(avatarView);
        avatarFrame.setMinSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.setPrefSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.setMaxSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.getStyleClass().add(styleClass);
        return avatarFrame;
    }

    /** Loads a bundled avatar and fails early if the project resource is missing. */
    private static Image loadImage(String resourcePath) {
        URL imageUrl = DialogBox.class.getResource(resourcePath);
        if (imageUrl == null) {
            throw new IllegalStateException("Missing Wobble avatar resource: " + resourcePath);
        }
        return new Image(imageUrl.toExternalForm());
    }
}
