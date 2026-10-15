package luckynoslacky.luckyui.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.TaskContent;
import luckynoslacky.TextContent;

/**
 * Displays one speaker's message in the conversation.
 */
public class DialogueBox extends HBox {
    /** CSS style applied to chatbot dialogue rows. */
    public static final String CHATBOT_DIALOGUE_STYLE = "chatbot-dialogue";

    /** CSS style applied to user dialogue rows. */
    public static final String USER_DIALOGUE_STYLE = "user-dialogue";

    /** CSS style applied to warning dialogue rows. */
    public static final String WARNING_DIALOGUE_STYLE = "warning-dialogue";

    /** CSS style applied to successful chatbot dialogue rows. */
    public static final String SUCCESS_DIALOGUE_STYLE = "success-dialogue";

    /** CSS style applied to informational chatbot dialogue rows. */
    public static final String INFO_DIALOGUE_STYLE = "info-dialogue";

    /** CSS style applied to system-error dialogue rows. */
    public static final String SYSTEM_ERROR_DIALOGUE_STYLE =
            "system-error-dialogue";

    private static final double AVATAR_FRAME_SIZE = 48.0;
    private static final double AVATAR_IMAGE_SIZE = 40.0;
    private static final double CHATBOT_BUBBLE_WIDTH_FRACTION = 0.86;
    private static final double USER_BUBBLE_WIDTH_FRACTION = 0.70;
    private static final double DIALOGUE_SPACING = 8.0;
    private static final double TONE_MARKER_ICON_SIZE = 18.0;
    private static final String USER_ACCESSIBLE_PREFIX = "You: ";
    private static final String CHATBOT_ACCESSIBLE_PREFIX = "LuckyNoSlacky: ";
    private static final String WARNING_ACCESSIBLE_PREFIX =
            "Bodoh sia like that also can kena warning ";
    private static final Image SUCCESS_MARKER_ICON = loadMarkerIcon(
            "/images/icons/response-success.png");
    private static final Image WARNING_MARKER_ICON = loadMarkerIcon(
            "/images/icons/response-warning.png");
    private static final Image SYSTEM_ERROR_MARKER_ICON = loadMarkerIcon(
            "/images/icons/response-system-error.png");
    /** Identifies the speaker and visual treatment of a dialogue row. */
    public enum DialogueType {
        /** A LuckyNoSlacky response. */
        CHATBOT,
        /** A command entered by the user. */
        USER
    }

    /**
     * Creates a responsive dialogue row with a circular avatar.
     *
     * @param message message content
     * @param avatar speaker profile image
     * @param dialogueType speaker and visual role of the message
     */
    public DialogueBox(
            String message,
            Image avatar,
            DialogueType dialogueType) {
        this(avatar, dialogueType, ResponseTone.NEUTRAL,
                new TextContent(message));
    }

    /**
     * Creates a responsive dialogue row with a circular avatar and response
     * tone.
     *
     * @param message message content
     * @param avatar speaker profile image
     * @param dialogueType speaker role of the message
     * @param responseTone semantic tone of a chatbot response
     */
    public DialogueBox(
            String message,
            Image avatar,
            DialogueType dialogueType,
            ResponseTone responseTone) {
        this(avatar, dialogueType, responseTone,
                new TextContent(message));
    }

    /**
     * Creates a dialogue row with a semantic tone and structured content.
     *
     * @param avatar speaker profile image
     * @param dialogueType speaker role
     * @param responseTone semantic tone of a chatbot response
     * @param responseContent structured chatbot response content
     */
    public DialogueBox(
            Image avatar,
            DialogueType dialogueType,
            ResponseTone responseTone,
            ResponseContent responseContent) {
        VBox messageContainer = createMessageContainer(
                responseContent, dialogueType, responseTone);
        messageContainer.setMinWidth(0.0);

        StackPane avatarFrame = createAvatarFrame(
                avatar, dialogueType);

        setMaxWidth(Double.MAX_VALUE);
        setAccessibleRole(AccessibleRole.TEXT);
        setAccessibleText(getAccessibleText(
                responseContent, dialogueType, responseTone));
        setFocusTraversable(false);
        setAlignment(dialogueType == DialogueType.USER
                ? Pos.CENTER_RIGHT
                : Pos.CENTER_LEFT);
        setSpacing(DIALOGUE_SPACING);
        getStyleClass().addAll(
                "dialogue-box",
                getDialogueStyle(dialogueType),
                getToneStyle(dialogueType, responseTone));

        double bubbleWidthFraction = dialogueType == DialogueType.USER
                ? USER_BUBBLE_WIDTH_FRACTION
                : CHATBOT_BUBBLE_WIDTH_FRACTION;
        messageContainer.maxWidthProperty().bind(
                Bindings.createDoubleBinding(() -> {
                    double availableWidth = getWidth()
                            - AVATAR_FRAME_SIZE - DIALOGUE_SPACING;
                    return Math.max(
                            0.0,
                            availableWidth * bubbleWidthFraction);
                },
                        widthProperty()));
        if (dialogueType == DialogueType.USER) {
            getChildren().addAll(messageContainer, avatarFrame);
        } else {
            getChildren().addAll(avatarFrame, messageContainer);
        }
    }

    /** Creates the styled outer message bubble and its content children. */
    private static VBox createMessageContainer(
            ResponseContent responseContent,
            DialogueType dialogueType,
            ResponseTone responseTone) {
        VBox messageContainer = new VBox();
        messageContainer.getStyleClass().add("message-content");
        if (dialogueType == DialogueType.CHATBOT) {
            messageContainer.getStyleClass().add(
                    getPersonalityToneStyle(responseTone));
            String toneLabelStyle = getToneLabelStyle(responseTone);
            if (!toneLabelStyle.isEmpty()) {
                messageContainer.getStyleClass().add(toneLabelStyle);
            }
            if (responseContent instanceof TaskContent taskContent) {
                addTaskContent(messageContainer, taskContent, responseTone);
            } else {
                addProseContent(
                        messageContainer, responseContent.message(), responseTone);
            }
        } else {
            messageContainer.getStyleClass().add("personality-user-message");
            Label messageLabel = createTextLabel(
                    responseContent.message(), "personality-user-label");
            messageContainer.getChildren().add(messageLabel);
        }
        return messageContainer;
    }

    /** Adds task lines as individual colour-coded cards while preserving order. */
    private static void addTaskContent(
            VBox messageContainer,
            TaskContent taskContent,
            ResponseTone responseTone) {
        addToneMarker(messageContainer, responseTone);
        VBox taskList = new VBox();
        taskList.getStyleClass().add("personality-task-list");
        addProseLines(taskList, taskContent.leadingLines(), responseTone);
        taskContent.taskViews().forEach(task -> taskList.getChildren().add(
                TaskCardRenderer.createTaskCard(task)));
        addProseLines(taskList, taskContent.trailingLines(), responseTone);
        messageContainer.getChildren().add(taskList);
    }

    /** Adds non-empty prose lines surrounding structured task cards. */
    private static void addProseLines(
            VBox taskList,
            List<String> lines,
            ResponseTone responseTone) {
        lines.stream()
                .filter(line -> !line.isBlank())
                .map(line -> createTextLabel(
                        line,
                        "personality-prose",
                        getToneLabelStyle(responseTone)))
                .forEach(taskList.getChildren()::add);
    }

    /** Adds ordinary response text to a single visible prose label. */
    private static void addProseContent(
            VBox messageContainer,
            String message,
            ResponseTone responseTone) {
        addToneMarker(messageContainer, responseTone);
        messageContainer.getChildren().add(
                createTextLabel(message,
                        "personality-prose",
                        getToneLabelStyle(responseTone)));
    }

    /** Creates a non-interactive circular avatar frame. */
    private static StackPane createAvatarFrame(
            Image avatar,
            DialogueType dialogueType) {
        StackPane avatarFrame = new StackPane();
        avatarFrame.getStyleClass().addAll(
                "avatar-frame",
                "personality-avatar-frame",
                dialogueType == DialogueType.USER
                        ? "personality-user-avatar-frame"
                        : "personality-chatbot-avatar-frame");
        avatarFrame.setMinSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.setPrefSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.setMaxSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.setMouseTransparent(true);
        avatarFrame.setFocusTraversable(false);
        avatarFrame.setAccessibleRole(AccessibleRole.NODE);
        avatarFrame.setAccessibleText("");

        ImageView imageView = new ImageView(avatar);
        imageView.setFitWidth(AVATAR_IMAGE_SIZE);
        imageView.setFitHeight(AVATAR_IMAGE_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setClip(new Circle(
                AVATAR_IMAGE_SIZE / 2.0,
                AVATAR_IMAGE_SIZE / 2.0,
                AVATAR_IMAGE_SIZE / 2.0));
        imageView.setAccessibleRole(AccessibleRole.NODE);
        imageView.setAccessibleText("");
        imageView.setFocusTraversable(false);
        imageView.setMouseTransparent(true);
        imageView.getStyleClass().addAll("avatar", "personality-avatar-image");
        avatarFrame.getChildren().add(imageView);

        if (dialogueType == DialogueType.CHATBOT) {
            StackPane accent = new StackPane();
            accent.getStyleClass().add("personality-chatbot-avatar-accent");
            accent.setMouseTransparent(true);
            accent.setFocusTraversable(false);
            accent.setAccessibleRole(AccessibleRole.NODE);
            accent.setAccessibleText("");
            avatarFrame.getChildren().add(accent);
        }
        return avatarFrame;
    }

    /** Creates a wrapped text node with hidden child semantics. */
    private static Label createTextLabel(String text, String... styles) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setAccessibleRole(AccessibleRole.NODE);
        label.setAccessibleText("");
        label.setFocusTraversable(false);
        label.getStyleClass().addAll(styles);
        return label;
    }

    /**
     * Returns the CSS style for a dialogue role.
     *
     * @param dialogueType dialogue role
     * @return CSS style class for the role
     */
    private static String getDialogueStyle(DialogueType dialogueType) {
        return dialogueType == DialogueType.USER
                ? USER_DIALOGUE_STYLE
                : CHATBOT_DIALOGUE_STYLE;
    }

    /**
     * Returns the CSS style for a chatbot response tone.
     *
     * @param dialogueType dialogue role
     * @param responseTone response tone
     * @return CSS style class for the tone, or an empty string for user rows
     */
    private static String getToneStyle(
            DialogueType dialogueType,
            ResponseTone responseTone) {
        if (dialogueType == DialogueType.USER) {
            return "";
        }
        return switch (responseTone) {
            case NEUTRAL -> "";
            case SUCCESS -> SUCCESS_DIALOGUE_STYLE;
            case INFO -> INFO_DIALOGUE_STYLE;
            case WARNING -> WARNING_DIALOGUE_STYLE;
            case SYSTEM_ERROR -> SYSTEM_ERROR_DIALOGUE_STYLE;
        };
    }

    /** Adds a non-colour marker for tones that require extra emphasis. */
    private static void addToneMarker(
            VBox messageContainer,
            ResponseTone responseTone) {
        switch (responseTone) {
            case SUCCESS -> addToneMarker(
                    messageContainer, SUCCESS_MARKER_ICON);
            case WARNING -> addToneMarker(
                    messageContainer, WARNING_MARKER_ICON);
            case SYSTEM_ERROR -> addToneMarker(
                    messageContainer, SYSTEM_ERROR_MARKER_ICON);
            case NEUTRAL, INFO -> {
                // These tones do not need an additional non-colour marker.
            }
            default -> throw new IllegalStateException(
                    "Unexpected response tone: " + responseTone);
        }
    }

    /** Adds a fixed-size decorative image for an emphasized response tone. */
    private static void addToneMarker(
            VBox messageContainer,
            Image markerIcon) {
        ImageView marker = new ImageView(markerIcon);
        marker.setFitWidth(TONE_MARKER_ICON_SIZE);
        marker.setFitHeight(TONE_MARKER_ICON_SIZE);
        marker.setPreserveRatio(true);
        marker.setSmooth(true);
        marker.setAccessibleRole(AccessibleRole.NODE);
        marker.setAccessibleText("");
        marker.setFocusTraversable(false);
        marker.setMouseTransparent(true);
        messageContainer.getChildren().add(marker);
    }

    /** Loads one bundled response-tone marker image. */
    private static Image loadMarkerIcon(String resourcePath) {
        return new Image(Objects.requireNonNull(
                DialogueBox.class.getResource(resourcePath),
                "Missing response marker image: " + resourcePath).toExternalForm());
    }

    /** Returns the personality stylesheet class for a response tone. */
    private static String getPersonalityToneStyle(ResponseTone responseTone) {
        return switch (responseTone) {
            case NEUTRAL -> "personality-response-neutral";
            case SUCCESS -> "personality-response-success";
            case INFO -> "personality-response-information";
            case WARNING -> "personality-response-warning";
            case SYSTEM_ERROR -> "personality-response-system-error";
        };
    }

    /** Returns the personality label class for a response tone. */
    private static String getToneLabelStyle(ResponseTone responseTone) {
        return switch (responseTone) {
            case NEUTRAL -> "personality-response-neutral-label";
            case SUCCESS -> "personality-response-success-label";
            case INFO -> "personality-response-information-label";
            case WARNING -> "personality-response-warning-label";
            case SYSTEM_ERROR -> "personality-response-system-error-label";
        };
    }

    /**
     * Returns the screen-reader text for a dialogue role and response tone.
     *
     * @param responseContent response content
     * @param dialogueType dialogue role
     * @param responseTone response tone
     * @return role-aware accessible message
     */
    private static String getAccessibleText(
            ResponseContent responseContent,
            DialogueType dialogueType,
            ResponseTone responseTone) {
        if (dialogueType == DialogueType.USER) {
            return USER_ACCESSIBLE_PREFIX + responseContent.message();
        }
        String accessibleMessage = responseContent instanceof TaskContent taskContent
                ? getAccessibleTaskMessage(taskContent)
                : responseContent.message();
        return responseTone == ResponseTone.WARNING
                ? WARNING_ACCESSIBLE_PREFIX + accessibleMessage
                : CHATBOT_ACCESSIBLE_PREFIX + accessibleMessage;
    }

    /** Returns task content without decorative markers for screen readers. */
    private static String getAccessibleTaskMessage(TaskContent taskContent) {
        List<String> accessibleLines = new ArrayList<>();
        addAccessibleProseLines(accessibleLines, taskContent.leadingLines());
        taskContent.taskViews().stream()
                .map(TaskCardRenderer::getAccessibleDescription)
                .forEach(accessibleLines::add);
        addAccessibleProseLines(accessibleLines, taskContent.trailingLines());
        return String.join(". ", accessibleLines);
    }

    /** Adds non-empty prose lines to a screen-reader response. */
    private static void addAccessibleProseLines(
            List<String> accessibleLines,
            List<String> proseLines) {
        proseLines.stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .forEach(accessibleLines::add);
    }
}
