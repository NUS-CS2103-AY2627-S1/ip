package bob.gui;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Displays one user or Bob message in the conversation.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_RADIUS = 19;
    private static final double USER_BUBBLE_WIDTH_RATIO = 0.72;
    private static final double BOB_BUBBLE_WIDTH_RATIO = 0.84;
    private static final double ENTRANCE_ANIMATION_DURATION_MILLISECONDS = 140;
    private static final double ENTRANCE_ANIMATION_START_OPACITY = 0.25;

    @FXML
    private Label messageLabel;

    @FXML
    private ImageView avatarImageView;

    /**
     * Creates a dialog box and loads its FXML layout.
     *
     * @param messageText Message to display.
     * @throws IllegalStateException If the dialog-box FXML cannot be loaded.
     */
    private DialogBox(String messageText) {
        loadFxmlLayout();
        assertFxmlFieldsAreInjected();
        messageLabel.setText(messageText);
        playEntranceAnimation();
    }

    /**
     * Creates a right-aligned dialog containing a user command.
     *
     * @param messageText User command to display.
     * @return Dialog box for the user.
     */
    public static DialogBox createUserDialog(String messageText) {
        DialogBox dialogBox = new DialogBox(messageText);
        dialogBox.configureAsUserDialog();
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog containing Bob's response and avatar.
     *
     * @param messageText Bob's response to display.
     * @param bobAvatar Bob's avatar.
     * @return Dialog box for Bob.
     */
    public static DialogBox createBobDialog(String messageText, Image bobAvatar) {
        return createBobDialog(messageText, bobAvatar, false);
    }

    /**
     * Creates a left-aligned dialog containing Bob's response and optional error styling.
     *
     * @param messageText Bob's response to display.
     * @param bobAvatar Bob's avatar.
     * @param isError Whether the response describes an error.
     * @return Dialog box for Bob.
     */
    public static DialogBox createBobDialog(String messageText, Image bobAvatar, boolean isError) {
        DialogBox dialogBox = new DialogBox(messageText);
        dialogBox.configureAsBobDialog(bobAvatar);
        if (isError) {
            dialogBox.messageLabel.getStyleClass().add("error-bubble");
        }
        return dialogBox;
    }

    /**
     * Loads this dialog box's FXML layout.
     *
     * @throws IllegalStateException If the dialog-box FXML cannot be loaded.
     */
    private void loadFxmlLayout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box.", exception);
        }
    }

    /**
     * Documents the fields that the FXML loader must inject.
     */
    private void assertFxmlFieldsAreInjected() {
        assert messageLabel != null : "messageLabel must be injected by FXMLLoader";
        assert avatarImageView != null : "avatarImageView must be injected by FXMLLoader";
    }

    /**
     * Fades this dialog into view without delaying user interaction.
     */
    private void playEntranceAnimation() {
        FadeTransition fadeTransition = new FadeTransition(
                Duration.millis(ENTRANCE_ANIMATION_DURATION_MILLISECONDS), this);
        fadeTransition.setFromValue(ENTRANCE_ANIMATION_START_OPACITY);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    /**
     * Configures this dialog to display a user command.
     */
    private void configureAsUserDialog() {
        getStyleClass().add("user-dialog");
        messageLabel.getStyleClass().add("user-bubble");
        messageLabel.maxWidthProperty().bind(
                widthProperty().multiply(USER_BUBBLE_WIDTH_RATIO));
        avatarImageView.setManaged(false);
        avatarImageView.setVisible(false);
    }

    /**
     * Configures this dialog to display a response from Bob.
     *
     * @param bobAvatar Bob's avatar.
     */
    private void configureAsBobDialog(Image bobAvatar) {
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("bob-dialog");
        messageLabel.getStyleClass().add("bob-bubble");
        messageLabel.maxWidthProperty().bind(
                widthProperty().multiply(BOB_BUBBLE_WIDTH_RATIO));
        avatarImageView.setImage(bobAvatar);
        avatarImageView.setClip(
                new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
        getChildren().setAll(avatarImageView, messageLabel);
    }
}
