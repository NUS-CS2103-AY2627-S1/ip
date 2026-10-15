package zinc.javafx;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import zinc.Zinc;
import zinc.ui.BackgroundType;
import zinc.ui.CommandHistory;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    /** The proportion of the application width allocated to the sidebar. */
    private static final double SIDEBAR_WIDTH_RATIO = 0.20;

    /** The minimum width reserved for the chat pane. */
    private static final double MINIMUM_CHAT_WIDTH = 600;

    /** The CSS classes managed when the background changes. */
    private static final List<String> BACKGROUND_STYLE_CLASSES = BackgroundStyle.getBackgroundStyleClasses();

    /** The sidebar style classes managed when the background changes. */
    private static final List<String> SIDEBAR_STYLE_CLASSES = BackgroundStyle.getSidebarStyleClasses();

    /** The delay before Zinc exits after displaying its goodbye message. */
    private static final Duration EXIT_DELAY = Duration.seconds(2);

    /** The command that exits Zinc. */
    private static final String EXIT_COMMAND = "bye";

    /** The command that displays Zinc's help message. */
    private static final String HELP_COMMAND = "help";

    /** The command that displays every saved contact. */
    private static final String LIST_CONTACTS_COMMAND = "ct ls";

    /** The command that displays every saved task. */
    private static final String LIST_TASK_COMMAND = "ls";

    /** The scroll position representing the bottom of the conversation. */
    private static final double BOTTOM_SCROLL_POSITION = 1.0;

    /** The image displayed beside user messages. */
    private final Image userImage = loadImage("/images/user-avatar.png");

    /** The image displayed beside Zinc messages. */
    private final Image zincImage = loadImage("/images/zinc-avatar.png");

    /** The root layout whose width drives the responsive sidebar. */
    @FXML
    private BorderPane appShell;

    /** The scrollable region containing the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** The container holding dialog boxes in display order. */
    @FXML
    private VBox dialogContainer;

    /** The navigation panel whose colour follows the active background. */
    @FXML
    private VBox sidebar;

    /** The pane behind the conversation and its controls. */
    @FXML
    private BorderPane chatPane;

    /** The field in which the user enters commands. */
    @FXML
    private TextField userInput;

    /** The application instance used to process commands. */
    private Zinc zinc;

    /** The commands previously submitted through this window. */
    private final CommandHistory commandHistory = new CommandHistory();

    /**
     * Initializes the center pane.
     */
    @FXML
    public void initialize() {
        sidebar.prefWidthProperty().bind(Bindings.min(
                appShell.widthProperty().multiply(SIDEBAR_WIDTH_RATIO),
                Bindings.max(0, appShell.widthProperty().subtract(MINIMUM_CHAT_WIDTH))));
        dialogContainer.heightProperty().addListener((ignoredObservable, ignoredOldHeight, ignoredNewHeight) -> {
            Platform.runLater(() -> scrollPane.setVvalue(BOTTOM_SCROLL_POSITION));
        });
        userInput.addEventFilter(KeyEvent.KEY_PRESSED, this::handleCommandHistoryKeyPress);
        applyCurrentBackground();
        chatPane.sceneProperty().addListener((ignoredObservable, ignoredOldScene, newScene) ->
                observeWindowFocus(newScene));
    }

    /** Refreshes the background selection using the current local time. */
    private void applyCurrentBackground() {
        LocalTime currentTime = LocalTime.now();
        BackgroundType selectedBackgroundType = zinc == null ? BackgroundType.AUTO : zinc.getBackgroundType();
        BackgroundStyle selectedStyle = BackgroundStyle.fromSelection(currentTime, selectedBackgroundType);
        chatPane.getStyleClass().removeAll(BACKGROUND_STYLE_CLASSES);
        chatPane.getStyleClass().add(selectedStyle.getBackgroundStyleClass());
        sidebar.getStyleClass().removeAll(SIDEBAR_STYLE_CLASSES);
        sidebar.getStyleClass().add(selectedStyle.getSidebarStyleClass());
    }

    /** Re-evaluates the background whenever the main window regains focus. */
    private void observeWindowFocus(Scene scene) {
        if (scene == null) {
            return;
        }

        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleGlobalKeyPress);

        scene.windowProperty().addListener((ignoredObservable, ignoredOldWindow, newWindow) -> {
            if (newWindow != null) {
                newWindow.focusedProperty().addListener((ignoredFocusObservable, ignoredWasFocused, isFocused) -> {
                    if (isFocused) {
                        applyCurrentBackground();
                    }
                });
            }
        });

        if (scene.getWindow() != null) {
            scene.getWindow().focusedProperty().addListener((ignoredObservable, ignoredWasFocused, isFocused) -> {
                if (isFocused) {
                    applyCurrentBackground();
                }
            });
        }
    }

    /** Restores command-entry focus when Enter is pressed outside the input field. */
    private void handleGlobalKeyPress(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER || userInput.isFocused()) {
            return;
        }

        userInput.requestFocus();
        event.consume();
    }

    /** Replaces the input with a neighbouring command when an arrow key is pressed. */
    private void handleCommandHistoryKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            userInput.setText(commandHistory.navigateUp(userInput.getText()));
            userInput.positionCaret(userInput.getText().length());
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            userInput.setText(commandHistory.navigateDown(userInput.getText()));
            userInput.positionCaret(userInput.getText().length());
            event.consume();
        }
    }

    /**
     * Injects the Zinc instance and displays its startup message.
     *
     * @param zincInstance The application instance used to process commands.
     */
    public void setZinc(Zinc zincInstance) {
        zinc = zincInstance;
        appendZincDialog(zinc.getGreetingResponse());
    }

    /**
     * Displays the user's command and Zinc's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            commandHistory.resetNavigation();
            userInput.clear();
            appendZincDialog(zinc.processCommand(input == null ? "" : input));
            return;
        }

        commandHistory.addCommand(input);
        appendUserDialog(input);
        userInput.clear();

        if (isHelpCommand(input)) {
            showHelp();
            return;
        }
        if (isExitCommand(input)) {
            exitProgram();
            return;
        }

        String response = zinc.processCommand(input);
        appendZincDialog(response);
        applyCurrentBackground();
    }

    /** Displays Zinc's command reference. */
    @FXML
    private void showHelp() {
        new HelpWindow().show(userInput.getScene().getWindow());
    }

    /** Displays every saved contact. */
    @FXML
    private void showContactList() {
        appendZincDialog(zinc.processCommand(LIST_CONTACTS_COMMAND));
    }

    /** Displays every saved task. */
    @FXML
    private void showTaskList() {
        appendZincDialog(zinc.processCommand(LIST_TASK_COMMAND));
    }

    /** Clears the conversation and displays a fresh prompt. */
    @FXML
    private void startNewConversation() {
        dialogContainer.getChildren().clear();
        appendZincDialog(zinc.getConversationPromptResponse());
    }

    /** Displays a goodbye message and exits after a short delay. */
    @FXML
    private void exitProgram() {
        appendZincDialog(zinc.getGoodbyeResponse());

        PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
        exitPause.setOnFinished(ignoredEvent -> Platform.exit());
        exitPause.play();
    }

    /** Adds a Zinc-authored message to the conversation. */
    private void appendZincDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.createZincDialog(message, zincImage));
    }

    /** Adds a user-authored message to the conversation. */
    private void appendUserDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.createUserDialog(message, userImage));
    }

    /** Returns the CSS background class for a supplied local time. */
    static String getBackgroundStyleClass(LocalTime time) {
        assert time != null : "Time must not be null";
        return BackgroundStyle.fromTime(time).getBackgroundStyleClass();
    }

    /** Returns the background CSS class for the selected mode and current time. */
    static String getBackgroundStyleClass(LocalTime time, BackgroundType selectedBackgroundType) {
        assert time != null && selectedBackgroundType != null : "Background inputs must not be null";
        return BackgroundStyle.fromSelection(time, selectedBackgroundType).getBackgroundStyleClass();
    }

    /** Returns the sidebar CSS class for a supplied local time. */
    static String getSidebarStyleClass(LocalTime time) {
        assert time != null : "Time must not be null";
        return BackgroundStyle.fromTime(time).getSidebarStyleClass();
    }

    /** Returns whether the supplied input uses the help command word. */
    static boolean isHelpCommand(String input) {
        assert input != null : "Command input must not be null";
        String trimmedInput = input.strip();
        if (trimmedInput.isEmpty()) {
            return false;
        }
        return trimmedInput.split("\\s+", 2)[0].equalsIgnoreCase(HELP_COMMAND);
    }

    /** Returns whether the supplied input is an exit command without arguments. */
    static boolean isExitCommand(String input) {
        assert input != null : "Command input must not be null";
        return input.strip().equalsIgnoreCase(EXIT_COMMAND);
    }

    /** Loads an image resource required by the main window. */
    private static Image loadImage(String resourcePath) {
        InputStream imageStream = Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath), "Missing image resource: " + resourcePath);

        try (imageStream) {
            return new Image(imageStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load image resource: " + resourcePath, exception);
        }
    }
}
