package zucc.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Displays a single conversation entry using the layout appropriate to its source.
 */
public abstract class DialogBox extends HBox {
    /** Maximum width that keeps long messages comfortable to read. */
    private static final double MESSAGE_CARD_MAXIMUM_WIDTH = 560.0;

    /** Portion of a row that a bot response may occupy. */
    private static final double RESPONSE_CARD_WIDTH_RATIO = 0.88;

    /** Portion of a row that a compact user command may occupy. */
    private static final double USER_CARD_WIDTH_RATIO = 0.76;

    /**
     * Creates the common structure of a conversation row.
     *
     * @param alignment position of the row's content.
     * @param dialogStyleClass style class applied to the complete row.
     */
    private DialogBox(Pos alignment, String dialogStyleClass) {
        getStyleClass().add("dialog-row");
        getStyleClass().add(dialogStyleClass);
        setAlignment(alignment);
    }

    /** Creates a responsive card containing the given message. */
    protected final VBox createMessageCard(String message, String cardStyleClass,
                                           double cardWidthRatio) {
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMinHeight(USE_PREF_SIZE);
        messageLabel.getStyleClass().add("message-text");

        VBox messageCard = new VBox(messageLabel);
        messageCard.getStyleClass().addAll("message-card", cardStyleClass);
        messageCard.maxWidthProperty().bind(Bindings.min(
                MESSAGE_CARD_MAXIMUM_WIDTH, widthProperty().multiply(cardWidthRatio)));
        return messageCard;
    }

    /** Creates the compact heading used only by application responses. */
    private static Label createSpeakerLabel(String speaker) {
        Label speakerLabel = new Label(speaker);
        speakerLabel.getStyleClass().add("speaker-label");
        return speakerLabel;
    }

    /** Creates a small identity or warning marker beside an application response. */
    private static StackPane createAvatar(String avatarText, String avatarStyleClass) {
        Label avatarLabel = new Label(avatarText);
        avatarLabel.getStyleClass().add("avatar-text");

        StackPane avatar = new StackPane(avatarLabel);
        avatar.getStyleClass().addAll("avatar", avatarStyleClass);
        return avatar;
    }

    /** Displays a compact command without redundant identity decoration. */
    static final class UserDialogBox extends DialogBox {
        UserDialogBox(String message) {
            super(Pos.TOP_RIGHT, "user-dialog");
            getChildren().add(createMessageCard(message, "user-card", USER_CARD_WIDTH_RATIO));
        }
    }

    /** Provides the shared structure of application responses. */
    private abstract static class ApplicationDialogBox extends DialogBox {
        private ApplicationDialogBox(String message, String speaker, String avatarText,
                                     String dialogStyleClass, String cardStyleClass,
                                     String avatarStyleClass) {
            super(Pos.TOP_LEFT, dialogStyleClass);

            VBox messageCard = createMessageCard(
                    message, cardStyleClass, RESPONSE_CARD_WIDTH_RATIO);
            messageCard.getChildren().addFirst(createSpeakerLabel(speaker));
            getChildren().addAll(createAvatar(avatarText, avatarStyleClass), messageCard);
        }
    }

    /** Displays a regular response from Zucc. */
    static final class ZuccDialogBox extends ApplicationDialogBox {
        ZuccDialogBox(String message) {
            super(message, "ZUCC", "Z", "zucc-dialog", "zucc-card", "zucc-avatar");
        }
    }

    /** Displays a recoverable error from Zucc. */
    static final class ErrorDialogBox extends ApplicationDialogBox {
        ErrorDialogBox(String message) {
            super(message, "CHECK COMMAND", "!", "error-dialog", "error-card", "error-avatar");
        }
    }
}
