package drax;

import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Controls the main JavaFX window defined in {@code MainWindow.fxml}.
 */
public class MainWindow {
    private static final double CONTROL_GROWTH_RATE = 0.5;
    private static final double DEFAULT_CONVERSATION_BOTTOM_INSET = 43;
    private static final double DEFAULT_INPUT_FONT_SIZE = 16;
    private static final double DEFAULT_INPUT_RIGHT_INSET = 38;
    private static final double DEFAULT_SEND_BUTTON_WIDTH = 30;
    private static final double DEFAULT_SEND_ICON_SIZE = 35;
    private static final double DEFAULT_WINDOW_HEIGHT = 600;
    private static final double DEFAULT_WINDOW_WIDTH = 400;
    private static final double MINIMUM_CONTENT_SCALE = 1;
    private static final String INPUT_FONT_NAME = "Inter";
    private static final Duration MESSAGE_ANIMATION_DURATION = Duration.millis(175);
    private static final double MESSAGE_SLIDE_DISTANCE = 6;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image draxImage = new Image(getClass().getResourceAsStream("/images/DaDrax.png"));

    @FXML
    private AnchorPane rootPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ImageView sendButtonImage;

    private Drax drax;
    private DoubleBinding contentScale;
    private DoubleBinding controlScale;

    /**
     * Calculates proportional growth while keeping the default sizes at or below the initial window size.
     * The smaller dimension ratio prevents content from outgrowing a window stretched in only one direction.
     *
     * @param width current content width.
     * @param height current content height.
     * @return scale of at least {@code 1.0}
     */
    static double calculateContentScale(double width, double height) {
        double widthScale = width / DEFAULT_WINDOW_WIDTH;
        double heightScale = height / DEFAULT_WINDOW_HEIGHT;
        double constrainedScale = Math.min(widthScale, heightScale);
        return Math.max(MINIMUM_CONTENT_SCALE, constrainedScale);
    }

    /**
     * Calculates gentler growth for input controls than for conversation content.
     *
     * @param contentScale scale used by message text and avatars.
     * @return scale that applies half of the additional conversation-content growth
     */
    static double calculateControlScale(double contentScale) {
        double additionalGrowth = contentScale - MINIMUM_CONTENT_SCALE;
        return MINIMUM_CONTENT_SCALE + additionalGrowth * CONTROL_GROWTH_RATE;
    }

    /**
     * Connects behavior that depends on controls injected from the FXML view.
     */
    @FXML
    private void initialize() {
        contentScale = Bindings.createDoubleBinding(() ->
                calculateContentScale(rootPane.getWidth(), rootPane.getHeight()),
                rootPane.widthProperty(), rootPane.heightProperty());
        controlScale = Bindings.createDoubleBinding(() ->
                calculateControlScale(contentScale.doubleValue()), contentScale);
        bindControlSize();
        dialogContainer.heightProperty().addListener((
                observable, oldHeight, newHeight) -> scrollToBottom());
    }

    /** Keeps the command field, send button, and surrounding space aligned as their size changes. */
    private void bindControlSize() {
        userInput.fontProperty().bind(Bindings.createObjectBinding(() ->
                new Font(INPUT_FONT_NAME, DEFAULT_INPUT_FONT_SIZE * controlScale.doubleValue()),
                controlScale));
        sendButton.prefWidthProperty().bind(Bindings.multiply(DEFAULT_SEND_BUTTON_WIDTH, controlScale));
        sendButtonImage.fitWidthProperty().bind(Bindings.multiply(DEFAULT_SEND_ICON_SIZE, controlScale));
        sendButtonImage.fitHeightProperty().bind(Bindings.multiply(DEFAULT_SEND_ICON_SIZE, controlScale));

        controlScale.addListener((observable, oldScale, newScale) ->
                updateControlInsets(newScale.doubleValue()));
        updateControlInsets(controlScale.doubleValue());
    }

    /**
     * Reserves enough room for the scaled input controls without allowing them to overlap nearby content.
     *
     * @param scale current input-control scale.
     */
    private void updateControlInsets(double scale) {
        AnchorPane.setRightAnchor(userInput, DEFAULT_INPUT_RIGHT_INSET * scale);
        AnchorPane.setBottomAnchor(scrollPane, DEFAULT_CONVERSATION_BOTTOM_INSET * scale);
    }

    /**
     * Scrolls the conversation to its newest message.
     */
    private void scrollToBottom() {
        scrollPane.setVvalue(scrollPane.getVmax());
    }

    /**
     * Adds a message to the conversation and plays its entrance animation.
     *
     * @param dialogBox message bubble to display.
     */
    private void addDialog(DialogBox dialogBox) {
        dialogBox.setOpacity(0);
        dialogBox.setTranslateY(MESSAGE_SLIDE_DISTANCE);
        dialogContainer.getChildren().add(dialogBox);

        FadeTransition fadeAnimation = new FadeTransition(MESSAGE_ANIMATION_DURATION, dialogBox);
        fadeAnimation.setToValue(1);
        TranslateTransition slideAnimation = new TranslateTransition(MESSAGE_ANIMATION_DURATION, dialogBox);
        slideAnimation.setToY(0);
        new ParallelTransition(fadeAnimation, slideAnimation).play();
    }

    /**
     * Injects the shared Drax instance and displays its startup greeting.
     *
     * @param drax Drax instance that owns the application's state and logic.
     */
    public void setDrax(Drax drax) {
        this.drax = drax;
        addDialog(DialogBox.getDraxDialog(drax.greet(), draxImage, "", contentScale));
    }

    /** Passes user input to Drax and adds both sides of the conversation to the view. */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        String draxText = drax.getResponse(userText);
        DialogBox userDialogBox = DialogBox.getUserDialog(userText, userImage, contentScale);
        DialogBox draxDialogBox = DialogBox.getDraxDialog(
                draxText, draxImage, drax.getCommandType(userText), contentScale);

        addDialog(userDialogBox);
        addDialog(draxDialogBox);

        userInput.clear();

        if (userText.equals("bye")) {
            scheduleExit();
        }
    }

    /** Preserves the short delay that allows the farewell dialog to appear before closing the program. */
    // @@author draxche-reused
    // Reused from https://stackoverflow.com/a/56225206
    private void scheduleExit() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 500);
    }
}
