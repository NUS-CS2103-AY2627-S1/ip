package turtley.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Represents a dialog row containing a speaker image and message text.
 */
public class DialogBox extends HBox {

    private static final double TYPING_INTERVAL_MILLIS = 20.0;
    private static final double BOB_DISTANCE = 6.0;
    private static final double BOB_DURATION_MILLIS = 300.0;
    private static final double ROTATION_ANGLE = 12.0;
    private static final double MAX_BUBBLE_WIDTH = 700.0;
    private static final double MIN_BUBBLE_WIDTH = 180.0;
    private static final double BUBBLE_HORIZONTAL_SPACE = 88.0;
    private static final String VOICE_RESOURCE = "/images/TurtleyVoice.mp4";

    private static Media voiceMedia;
    private static MediaPlayer activeVoicePlayer;
    private static DialogBox activeVoiceOwner;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private Timeline typingAnimation;
    private TranslateTransition bobAnimation;
    private RotateTransition rotationAnimation;
    private MediaPlayer voicePlayer;
    private boolean isPlaybackStarted;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box interface.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        bindResponsiveBubbleWidth();
    }

    /**
     * Creates a dialog row aligned as a user message.
     *
     * @param text the user message.
     * @param image the user avatar.
     * @return the user dialog row.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a dialog row aligned as a Turtley response and types it out.
     *
     * @param text the Turtley response.
     * @param image the Turtley avatar.
     * @return the animated Turtley dialog row.
     */
    public static DialogBox getTurtleyDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox("", image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("turtley-dialog");
        dialogBox.startTyping(text);
        return dialogBox;
    }

    /**
     * Creates an animated Turtley error row with attention-grabbing error styling.
     *
     * @param text the error response.
     * @param image the Turtley avatar.
     * @return the animated, styled Turtley error row.
     */
    public static DialogBox getTurtleyErrorDialog(String text, Image image) {
        DialogBox dialogBox = getTurtleyDialog(text, image);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    /**
     * Creates a Turtley welcome row with fixed-width text for aligned ASCII art.
     *
     * @param text the Turtley welcome message.
     * @param image the Turtley avatar.
     * @return the Turtley welcome row.
     */
    public static DialogBox getTurtleyWelcomeDialog(String text, Image image) {
        DialogBox dialogBox = getTurtleyDialog(text, image);
        dialogBox.getStyleClass().add("welcome-dialog");
        dialogBox.dialog.setFont(Font.font("Monospaced"));
        dialogBox.dialog.setWrapText(false);
        return dialogBox;
    }

    /**
     * Keeps the message bubble within the space available beside its avatar.
     */
    private void bindResponsiveBubbleWidth() {
        dialog.maxWidthProperty().bind(Bindings.createDoubleBinding(() -> Math.min(
                        MAX_BUBBLE_WIDTH,
                        Math.max(MIN_BUBBLE_WIDTH, getWidth() - BUBBLE_HORIZONTAL_SPACE)),
                widthProperty()));
        HBox.setHgrow(dialog, Priority.NEVER);
    }

    /**
     * Starts revealing the dialog text immediately and loads optional voice playback.
     *
     * @param text the complete dialog text.
     */
    private void startTyping(String text) {
        stopAnimations();
        stopActiveVoicePlayer();
        isPlaybackStarted = false;

        dialog.setText("");
        if (text.isEmpty()) {
            return;
        }

        createAvatarAnimations();
        createTypingAnimation(text);
        typingAnimation.play();
        startVoicePlayback();
    }

    /**
     * Creates the bobbing and rotating animations for the avatar.
     */
    private void createAvatarAnimations() {
        bobAnimation = new TranslateTransition(Duration.millis(BOB_DURATION_MILLIS), displayPicture);
        bobAnimation.setByY(BOB_DISTANCE);
        bobAnimation.setAutoReverse(true);
        bobAnimation.setCycleCount(Animation.INDEFINITE);
        bobAnimation.setInterpolator(Interpolator.EASE_BOTH);

        rotationAnimation = new RotateTransition(Duration.millis(BOB_DURATION_MILLIS), displayPicture);
        rotationAnimation.setByAngle(ROTATION_ANGLE);
        rotationAnimation.setAutoReverse(true);
        rotationAnimation.setCycleCount(Animation.INDEFINITE);
        rotationAnimation.setInterpolator(Interpolator.EASE_BOTH);
    }

    /**
     * Creates the timeline that reveals the response text one character at a time.
     *
     * @param text the complete dialog text.
     */
    private void createTypingAnimation(String text) {
        typingAnimation = new Timeline(new KeyFrame(
                Duration.millis(TYPING_INTERVAL_MILLIS),
                event -> {
                    int nextLength = dialog.getText().length() + 1;
                    dialog.setText(text.substring(0, nextLength));
                }));
        typingAnimation.setCycleCount(text.length());
        typingAnimation.setOnFinished(event -> stopAnimations());
    }

    /**
     * Creates and starts synchronized voice playback when the media is ready.
     */
    private void startVoicePlayback() {
        MediaPlayer currentVoicePlayer = loadVoicePlayer();
        voicePlayer = currentVoicePlayer;
        activeVoicePlayer = currentVoicePlayer;
        activeVoiceOwner = this;
        currentVoicePlayer.setCycleCount(MediaPlayer.INDEFINITE);
        currentVoicePlayer.setOnReady(() -> startSynchronizedPlayback(currentVoicePlayer));
        if (currentVoicePlayer.getStatus() == MediaPlayer.Status.READY) {
            startSynchronizedPlayback(currentVoicePlayer);
        }
    }

    /**
     * Starts the media and avatar animations once the voice resource is ready.
     *
     * @param player the media player that has finished loading.
     */
    private void startSynchronizedPlayback(MediaPlayer player) {
        if (isPlaybackStarted || activeVoiceOwner != this || activeVoicePlayer != player) {
            return;
        }

        isPlaybackStarted = true;
        player.play();
        bobAnimation.play();
        rotationAnimation.play();
    }

    /**
     * Stops all currently playing Turtley voice audio.
     */
    private static void stopActiveVoicePlayer() {
        if (activeVoicePlayer != null) {
            stopAndDispose(activeVoicePlayer);
            activeVoicePlayer = null;
            activeVoiceOwner = null;
        }
    }

    /**
     * Stops the avatar and voice animations and restores the avatar's original transform.
     */
    private void stopAnimations() {
        if (typingAnimation != null) {
            typingAnimation.stop();
            typingAnimation = null;
        }
        if (bobAnimation != null) {
            bobAnimation.stop();
            displayPicture.setTranslateY(0.0);
            bobAnimation = null;
        }
        if (rotationAnimation != null) {
            rotationAnimation.stop();
            displayPicture.setRotate(0.0);
            rotationAnimation = null;
        }
        if (voicePlayer != null) {
            MediaPlayer currentVoicePlayer = voicePlayer;
            voicePlayer = null;
            if (activeVoiceOwner == this && currentVoicePlayer == activeVoicePlayer) {
                stopActiveVoicePlayer();
            } else {
                stopAndDispose(currentVoicePlayer);
            }
        }
    }

    /**
     * Stops, rewinds, and releases a voice player.
     *
     * @param player the voice player to release.
     */
    private static void stopAndDispose(MediaPlayer player) {
        if (player.getStatus() == MediaPlayer.Status.DISPOSED) {
            return;
        }
        player.pause();
        player.stop();
        player.seek(Duration.ZERO);
        player.dispose();
    }

    /**
     * Creates a voice player for the cached media.
     *
     * @return a new voice player.
     */
    private static MediaPlayer loadVoicePlayer() {
        return new MediaPlayer(loadVoiceMedia());
    }

    /**
     * Returns the cached voice media, loading the resource only on first use.
     *
     * @return the voice media.
     */
    private static Media loadVoiceMedia() {
        if (voiceMedia != null) {
            return voiceMedia;
        }

        URL voiceUrl = DialogBox.class.getResource(VOICE_RESOURCE);
        if (voiceUrl == null) {
            throw new IllegalStateException("Missing Turtley voice resource: " + VOICE_RESOURCE);
        }
        voiceMedia = new Media(voiceUrl.toExternalForm());
        return voiceMedia;
    }

    /**
     * Flips the row so that the avatar appears before the response text.
     */
    private void flip() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(getChildren());
        Collections.reverse(nodes);
        getChildren().setAll(nodes);
        setAlignment(Pos.TOP_LEFT);
    }
}
