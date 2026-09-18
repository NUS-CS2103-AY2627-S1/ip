package herta;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main GUI window for Herta.
 */
public class MainWindow extends AnchorPane {
    /** Resource path shared by the avatar and application icon. */
    static final String HERTA_IMAGE_RESOURCE = "/images/herta.png";
    private static final int EXIT_DELAY_SECONDS = 2;
    private static final int DEFAULT_ZOOM_LEVEL = 0;
    private static final int MINIMUM_ZOOM_LEVEL = -2;
    private static final int MAXIMUM_ZOOM_LEVEL = 4;
    private static final int MAXIMUM_HISTORY_ENTRIES = 200;
    private static final int MAXIMUM_DIALOGS = 500;
    private static final double ZOOM_STEP = 0.1;
    private static final double DIALOG_BASE_FONT_SIZE = 16.0;
    private static final double AVATAR_BASE_SIZE = 64.0;
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Label userInputPrompt;
    @FXML
    private Button sendButton;

    private Herta herta;
    /** Stores commands submitted through the GUI for arrow-key navigation. */
    private final CommandHistory commandHistory = new CommandHistory(MAXIMUM_HISTORY_ENTRIES);
    private int zoomLevel = DEFAULT_ZOOM_LEVEL;
    private final Image hertaImage = loadHertaImage();

    /**
     * Binds the scroll position to the dialog container and displays Herta's opening messages.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.widthProperty().addListener((observable, oldWidth, newWidth) ->
                applyMessageWidths(newWidth.doubleValue()));
        scrollPane.addEventFilter(ScrollEvent.SCROLL, this::handleZoomScroll);
        Parent windowRoot = Objects.requireNonNull(scrollPane.getParent());
        windowRoot.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyboardZoom);
        userInput.setOnKeyPressed(this::handleCommandHistory);
        addInitialDialog();
        userInputPrompt.visibleProperty().bind(
                userInput.textProperty().isEmpty()
                        .and(userInput.disabledProperty().not()));
        applyZoom();
    }

    /**
     * Supplies the Herta instance used to process GUI commands.
     *
     * @param herta the Herta instance to use.
     */
    public void setHerta(Herta herta) {
        this.herta = Objects.requireNonNull(herta, "The main window needs a Herta instance.");
        if (!herta.isReady()) {
            dialogContainer.getChildren().add(
                    DialogBox.getHertaDialog(
                            herta.getLoadingError(), hertaImage, ResponseCategory.ERROR));
            userInput.setDisable(true);
            sendButton.setDisable(true);
            applyZoom();
        }
    }

    /**
     * Displays the user's command and Herta's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        if (herta == null || !herta.isReady()) {
            return;
        }
        String userText = userInput.getText();
        recordCommand(userText);
        HertaResponse hertaResponse = herta.getResponse(userText);
        addCommandDialogs(userText, hertaResponse);
        trimDialogHistory();
        applyZoom();
        userInput.clear();

        if (hertaResponse.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);

            PauseTransition exitDelay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /** Handles navigation through previously submitted commands in the input field. */
    private void handleCommandHistory(KeyEvent event) {
        if (event.isControlDown() || event.isAltDown() || event.isMetaDown()) {
            return;
        }

        if (event.getCode() == KeyCode.UP) {
            commandHistory.moveToPreviousCommand(userInput.getText()).ifPresent(this::showCommand);
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            commandHistory.moveToNextCommand().ifPresent(this::showCommand);
            event.consume();
        }
    }

    /** Displays a history entry while placing the caret at the end of the text. */
    private void showCommand(String command) {
        userInput.setText(command);
        userInput.positionCaret(command.length());
    }

    /** Records a non-blank command unless it duplicates the latest history entry. */
    private void recordCommand(String command) {
        commandHistory.record(command);
    }

    /** Adds the opening message and keeps the window usable if its bubble FXML is malformed. */
    private void addInitialDialog() {
        try {
            dialogContainer.getChildren().add(DialogBox.getHertaDialog(
                    "Oh, you're here. I'm Herta.\nWell? What do you want?", hertaImage));
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unable to load the opening dialog bubble.", e);
            dialogContainer.getChildren().add(createFallbackMessage(
                    "Application files are incomplete; reinstall Herta."));
        }
    }

    /** Adds command bubbles or a minimal fallback when a bubble cannot be constructed. */
    private void addCommandDialogs(String userText, HertaResponse response) {
        try {
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(userText),
                    DialogBox.getHertaDialog(response.getMessage(), hertaImage,
                            response.getResponseCategory()));
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Unable to render a dialog bubble.", e);
            dialogContainer.getChildren().add(createFallbackMessage(response.getMessage()));
        }
    }

    /** Creates a plain wrapped label for use when styled dialog resources fail. */
    private Label createFallbackMessage(String message) {
        Label fallbackMessage = new Label(message);
        fallbackMessage.setWrapText(true);
        return fallbackMessage;
    }

    /** Keeps long GUI sessions bounded while retaining the newest conversation bubbles. */
    private void trimDialogHistory() {
        int excessDialogCount = dialogContainer.getChildren().size() - MAXIMUM_DIALOGS;
        if (excessDialogCount > 0) {
            dialogContainer.getChildren().remove(0, excessDialogCount);
        }
    }

    /** Loads the avatar after checking the resource explicitly for a stable startup error. */
    private static Image loadHertaImage() {
        var imageStream = MainWindow.class.getResourceAsStream(HERTA_IMAGE_RESOURCE);
        if (imageStream == null) {
            throw new IllegalStateException("Application files are incomplete; reinstall Herta.");
        }
        return new Image(imageStream);
    }

    /** Adjusts the chat text size when the user holds Ctrl while scrolling. */
    private void handleZoomScroll(ScrollEvent event) {
        if (!event.isControlDown() || event.getDeltaY() == 0) {
            return;
        }

        int zoomDirection = event.getDeltaY() > 0 ? 1 : -1;
        setZoomLevel(zoomLevel + zoomDirection);
        event.consume();
    }

    /** Applies the current zoom level to the conversation and avatars. */
    private void applyZoom() {
        double zoomScale = 1 + zoomLevel * ZOOM_STEP;
        double dialogFontSize = DIALOG_BASE_FONT_SIZE * zoomScale;
        String dialogFontSizeStyle = createFontSizeStyle(dialogFontSize);
        double avatarSize = AVATAR_BASE_SIZE * zoomScale;

        for (var child : dialogContainer.getChildren()) {
            if (child instanceof DialogBox dialogBox) {
                dialogBox.setFontSizeStyle(dialogFontSizeStyle);
                dialogBox.setAvatarSize(avatarSize);
            }
        }
        applyMessageWidths(dialogContainer.getWidth());
    }

    /** Updates message widths when the chat container or zoom level changes. */
    private void applyMessageWidths(double availableWidth) {
        if (availableWidth <= 0) {
            return;
        }

        for (var child : dialogContainer.getChildren()) {
            if (child instanceof DialogBox dialogBox) {
                dialogBox.setMessageMaxWidth(availableWidth);
            }
        }
    }

    /** Adjusts the chat text size with keyboard shortcuts while Ctrl is held. */
    private void handleKeyboardZoom(KeyEvent event) {
        if (!event.isControlDown()) {
            return;
        }

        if (event.getCode() == KeyCode.DIGIT0) {
            setZoomLevel(DEFAULT_ZOOM_LEVEL);
            event.consume();
            return;
        }

        int zoomDirection;
        if (event.getCode() == KeyCode.PLUS || event.getCode() == KeyCode.EQUALS) {
            zoomDirection = 1;
        } else if (event.getCode() == KeyCode.MINUS) {
            zoomDirection = -1;
        } else {
            return;
        }

        setZoomLevel(zoomLevel + zoomDirection);
        event.consume();
    }

    /** Restricts the zoom level to the supported range before applying it. */
    private void setZoomLevel(int requestedZoomLevel) {
        int boundedZoomLevel = Math.clamp(requestedZoomLevel,
                MINIMUM_ZOOM_LEVEL, MAXIMUM_ZOOM_LEVEL);
        if (boundedZoomLevel == zoomLevel) {
            return;
        }

        zoomLevel = boundedZoomLevel;
        applyZoom();
    }

    /** Creates an inline JavaFX font-size declaration for a control. */
    private static String createFontSizeStyle(double fontSize) {
        return String.format(Locale.ROOT, "-fx-font-size: %.1fpx;", fontSize);
    }
}
