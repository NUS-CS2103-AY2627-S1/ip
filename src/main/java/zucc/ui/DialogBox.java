package zucc.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Displays a single message with a speaker label and compact avatar.
 */
public final class DialogBox extends HBox {
    /** Maximum width that keeps long messages comfortable to read. */
    private static final double MESSAGE_CARD_MAXIMUM_WIDTH = 560.0;

    /** Portion of a row that a bot response may occupy. */
    private static final double RESPONSE_CARD_WIDTH_RATIO = 0.88;

    /** Portion of a row that a compact user command may occupy. */
    private static final double USER_CARD_WIDTH_RATIO = 0.76;

    /**
     * Creates a styled message row for one speaker.
     *
     * @param message message body to display.
     * @param type visual treatment appropriate to the message's role.
     */
    private DialogBox(String message, DialogType type) {
        getStyleClass().add("dialog-row");
        setAlignment(type.isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        getStyleClass().add(type.dialogStyleClass);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMinHeight(USE_PREF_SIZE);
        messageLabel.getStyleClass().add("message-text");

        VBox messageCard = type.isUser
                ? new VBox(messageLabel)
                : new VBox(createSpeakerLabel(type.speaker), messageLabel);
        messageCard.getStyleClass().addAll("message-card", type.cardStyleClass);
        double cardWidthRatio = type.isUser ? USER_CARD_WIDTH_RATIO : RESPONSE_CARD_WIDTH_RATIO;
        messageCard.maxWidthProperty().bind(Bindings.min(
                MESSAGE_CARD_MAXIMUM_WIDTH, widthProperty().multiply(cardWidthRatio)));

        if (type.isUser) {
            getChildren().add(messageCard);
        } else {
            getChildren().addAll(createAvatar(type.avatarText, type.avatarStyleClass), messageCard);
        }
    }

    /** Creates the compact heading used only by application responses. */
    private Label createSpeakerLabel(String speaker) {
        Label speakerLabel = new Label(speaker);
        speakerLabel.getStyleClass().add("speaker-label");
        return speakerLabel;
    }

    /** Creates a small identity or warning marker beside an application response. */
    private StackPane createAvatar(String avatarText, String avatarStyleClass) {
        Label avatarLabel = new Label(avatarText);
        avatarLabel.getStyleClass().add("avatar-text");

        StackPane avatar = new StackPane(avatarLabel);
        avatar.getStyleClass().addAll("avatar", avatarStyleClass);
        return avatar;
    }

    /**
     * Creates a dialog row for a command entered by the user.
     *
     * @param message command text to display.
     * @return styled user dialog row.
     */
    public static DialogBox createUserDialog(String message) {
        return new DialogBox(message, DialogType.USER);
    }

    /**
     * Creates a dialog row for a response from Zucc.
     *
     * @param message response text to display.
     * @return styled Zucc dialog row.
     */
    public static DialogBox createZuccDialog(String message) {
        return new DialogBox(message, DialogType.ZUCC);
    }

    /**
     * Creates a dialog row for a recoverable command error.
     *
     * @param message explanation of the error and how to recover from it.
     * @return styled error dialog row.
     */
    public static DialogBox createErrorDialog(String message) {
        return new DialogBox(message, DialogType.ERROR);
    }

    /** Captures the fixed presentation details for each kind of conversation entry. */
    private enum DialogType {
        USER("", "", "user-dialog", "user-card", "", true),
        ZUCC("ZUCC", "Z", "zucc-dialog", "zucc-card", "zucc-avatar", false),
        ERROR("CHECK COMMAND", "!", "error-dialog", "error-card", "error-avatar", false);

        /** Heading shown above a response. */
        private final String speaker;

        /** Short text shown inside the response marker. */
        private final String avatarText;

        /** Style class applied to the complete dialog row. */
        private final String dialogStyleClass;

        /** Style class applied to the message card. */
        private final String cardStyleClass;

        /** Style class applied to the response marker. */
        private final String avatarStyleClass;

        /** Whether this type represents a command entered by the user. */
        private final boolean isUser;

        DialogType(String speaker, String avatarText, String dialogStyleClass,
                   String cardStyleClass, String avatarStyleClass, boolean isUser) {
            this.speaker = speaker;
            this.avatarText = avatarText;
            this.dialogStyleClass = dialogStyleClass;
            this.cardStyleClass = cardStyleClass;
            this.avatarStyleClass = avatarStyleClass;
            this.isUser = isUser;
        }
    }
}
