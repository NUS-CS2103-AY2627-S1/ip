package nori.ui;

import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import nori.Nori;

/**
 * Controls Nori's main JavaFX window.
 */
public final class MainWindow {
    private static final Duration EXIT_DELAY = Duration.millis(900);
    private static final String GREETING = "Noot noot! I'm Nori, your tiny task penguin. "
            + "Type help to see what my flippers can do.";
    /** Size of the penguin shown in the header, beside the wordmark. */
    private static final double LOGO_SIZE = 50;
    /** Style class marking the composer while the command field holds the keyboard. */
    private static final String COMPOSER_FOCUS_STYLE_CLASS = "composer-bar-focused";

    @FXML
    private HBox composerBar;
    @FXML
    private VBox dialogContainer;
    @FXML
    private StackPane logoSlot;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button sendButton;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField userInput;

    /** The commands entered this session, for recall with the arrow keys. */
    private final CommandHistory commandHistory = new CommandHistory();

    /** Coordinates commands and persistent task state. */
    private Nori nori;
    /** Supplies the text response generated for each GUI command. */
    private GuiUi guiUi;

    /** Creates the controller, as required by the FXML loader. */
    public MainWindow() {
    }

    /**
     * Initializes visual elements that are easier to express in Java.
     */
    @FXML
    public void initialize() {
        logoSlot.getChildren().add(new BotAvatar(LOGO_SIZE));
        dialogContainer.heightProperty()
                .addListener((observable, previousHeight, currentHeight) -> scrollPane.setVvalue(1.0));
        userInput.setOnKeyPressed(this::handleHistoryKey);
        userInput.focusedProperty().addListener((observable, wasFocused, isFocused) ->
                showComposerFocus(isFocused));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Injects Nori and displays the opening messages for this session.
     *
     * @param nori the application logic used to execute commands.
     * @param guiUi the response adapter used by the graphical interface.
     */
    public void setNori(Nori nori, GuiUi guiUi) {
        this.nori = nori;
        this.guiUi = guiUi;
        addDialog(DialogBox.getNoriDialog(GREETING));

        String loadingMessage = nori.getLoadingMessage();
        if (loadingMessage != null) {
            addDialog(DialogBox.getNoriDialog(loadingMessage, nori.hasLoadingError()));
        }
    }

    /** Adds the user's command and Nori's response to the conversation. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || nori == null) {
            return;
        }

        userInput.clear();
        commandHistory.add(input);
        addDialog(DialogBox.getUserDialog(input));
        boolean isExitRequested = nori.executeCommand(input);
        addDialog(DialogBox.getNoriDialog(guiUi.consumeResponse(), guiUi.isErrorResponse()));

        if (isExitRequested) {
            endSession();
        } else {
            Platform.runLater(userInput::requestFocus);
        }
    }

    /**
     * Recalls an earlier command when the user presses the up or down arrow.
     *
     * Commands here are typed in full, and the next one a user wants is often
     * the last one with a word changed, so retyping is the main cost of using
     * Nori for longer than a moment. The arrows are where anyone who has used
     * a command line already looks.
     *
     * @param event the key the user pressed in the command field.
     */
    private void handleHistoryKey(KeyEvent event) {
        Optional<String> recalledCommand;
        if (event.getCode() == KeyCode.UP) {
            recalledCommand = commandHistory.recallEarlier();
        } else if (event.getCode() == KeyCode.DOWN) {
            recalledCommand = commandHistory.recallLater();
        } else {
            return;
        }

        event.consume();
        recalledCommand.ifPresent(this::showInCommandField);
    }

    /**
     * Puts a recalled command in the command field, ready to edit.
     *
     * @param command the command to show.
     */
    private void showInCommandField(String command) {
        userInput.setText(command);
        userInput.positionCaret(command.length());
    }

    /**
     * Rings the composer while the command field holds the keyboard.
     *
     * The field's own background is transparent so that it reads as part of
     * the composer rather than as a box inside it, which leaves it nothing to
     * show focus with. Marking the composer on its behalf means a user working
     * by keyboard can always see where their typing will go.
     *
     * @param isFocused whether the command field now holds the keyboard.
     */
    private void showComposerFocus(boolean isFocused) {
        composerBar.getStyleClass().remove(COMPOSER_FOCUS_STYLE_CLASS);
        if (isFocused) {
            composerBar.getStyleClass().add(COMPOSER_FOCUS_STYLE_CLASS);
        }
    }

    /**
     * Adds one message to the conversation, keeping its width in step with the window.
     *
     * @param dialogBox the message to add.
     */
    private void addDialog(DialogBox dialogBox) {
        dialogBox.bindMessageWidthTo(scrollPane.widthProperty());
        dialogContainer.getChildren().add(dialogBox);
    }

    /** Disables further input and closes the application after the goodbye is visible. */
    private void endSession() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        statusLabel.setText("Waddling away...");

        PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
        exitPause.setOnFinished(event -> Platform.exit());
        exitPause.play();
    }
}
