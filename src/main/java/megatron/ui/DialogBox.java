package megatron.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Displays one chat message with a simple avatar and message bubble.
 */
public final class DialogBox extends HBox {
    private static final String AVATAR_PATH_USER = "/images/user-avatar.png";
    private static final String AVATAR_PATH_MEGATRON = "/images/megatron-avatar.png";
    private static final double AVATAR_SIZE_MIN = 40;
    private static final double AVATAR_SIZE_MAX = 160;
    private static final double AVATAR_WIDTH_RATIO = 0.1;
    private static final double MESSAGE_HORIZONTAL_SPACE = 24;

    /** Keeps the error animation active for the lifetime of the dialog. */
    private ScaleTransition errorPulse;

    @FXML
    private Label messageLabel;

    @FXML
    private ImageView avatar;

    @FXML
    private StackPane avatarContainer;

    /**
     * Creates a dialog box for the given message.
     *
     * @param message The message text.
     * @param isUserMessage Whether the message was entered by the user.
     */
    public DialogBox(String message, boolean isUserMessage) {
        this(message, isUserMessage, DialogType.DEFAULT);
    }

    /**
     * Creates a styled dialog box for the given message.
     *
     * @param message The message text.
     * @param isUserMessage Whether the message was entered by the user.
     * @param dialogType The semantic style for the message.
     */
    public DialogBox(String message, boolean isUserMessage, DialogType dialogType) {
        loadLayout();

        messageLabel.setText(message);
        avatar.setImage(loadAvatar(isUserMessage ? AVATAR_PATH_USER : AVATAR_PATH_MEGATRON));
        avatar.setPreserveRatio(true);
        avatar.getStyleClass().add(isUserMessage ? "user-avatar" : "megatron-avatar");
        avatarContainer.getStyleClass().add(isUserMessage
                ? "user-avatar-container" : "megatron-avatar-container");

        setMaxWidth(Double.MAX_VALUE);
        setSpacing(10);
        setAlignment(isUserMessage ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        HBox.setHgrow(messageLabel, Priority.NEVER);
        getStyleClass().add(isUserMessage ? "user-dialog" : "megatron-dialog");
        getStyleClass().add(dialogType.getStyleClass());

        widthProperty().addListener((observable, oldWidth, newWidth) ->
                updateResponsiveLayout(newWidth.doubleValue()));
        updateResponsiveLayout(getWidth());

        if (isUserMessage) {
            flip();
        }

        if (dialogType == DialogType.ERROR) {
            startErrorPulse();
        }
    }

    /**
     * Represents the semantic styles available for response dialogs.
     */
    public enum DialogType {
        DEFAULT("default-dialog"),
        WELCOME("welcome-dialog"),
        ADD("add-dialog"),
        MARK("mark-dialog"),
        UNMARK("unmark-dialog"),
        FIND("find-dialog"),
        LIST("list-dialog"),
        DELETE("delete-dialog"),
        HELP("help-dialog"),
        BYE("bye-dialog"),
        ERROR("error-dialog");

        private final String styleClass;

        DialogType(String styleClass) {
            this.styleClass = styleClass;
        }

        /**
         * Returns the CSS class associated with this dialog type.
         *
         * @return The CSS class name.
         */
        public String getStyleClass() {
            return styleClass;
        }
    }

    /**
     * Loads the reusable dialog box layout from FXML.
     */
    private void loadLayout() {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }
    }

    /**
     * Reverses the child order for a user message.
     */
    private void flip() {
        var children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
    }

    /**
     * Starts a subtle red pulse for an error dialog.
     */
    private void startErrorPulse() {
        errorPulse = new ScaleTransition(Duration.seconds(1.2), messageLabel);
        errorPulse.setFromX(1.0);
        errorPulse.setFromY(1.0);
        errorPulse.setToX(1.02);
        errorPulse.setToY(1.02);
        errorPulse.setAutoReverse(true);
        errorPulse.setCycleCount(ScaleTransition.INDEFINITE);
        errorPulse.setInterpolator(Interpolator.EASE_BOTH);
        errorPulse.play();
    }

    /**
     * Updates the avatar and message widths to fit the current dialog width.
     *
     * @param dialogWidth The current width of this dialog.
     */
    private void updateResponsiveLayout(double dialogWidth) {
        double availableWidth = dialogWidth > 0 ? dialogWidth : 700;
        double avatarSize = Math.clamp(availableWidth * AVATAR_WIDTH_RATIO,
                AVATAR_SIZE_MIN, AVATAR_SIZE_MAX);
        double messageWidth = Math.max(AVATAR_SIZE_MIN,
                availableWidth - avatarSize - getSpacing() - MESSAGE_HORIZONTAL_SPACE);

        avatar.setFitWidth(avatarSize);
        avatar.setFitHeight(avatarSize);
        avatarContainer.setMinSize(avatarSize, avatarSize);
        avatarContainer.setPrefSize(avatarSize, avatarSize);
        avatarContainer.setMaxSize(avatarSize, avatarSize);
        messageLabel.setMaxWidth(messageWidth);
    }

    /**
     * Loads an avatar image from the application resources.
     *
     * @param resourcePath The avatar resource path.
     * @return The loaded avatar image.
     */
    private static Image loadAvatar(String resourcePath) {
        InputStream imageStream = Objects.requireNonNull(DialogBox.class.getResourceAsStream(resourcePath));
        return new Image(imageStream);
    }
}
