package panda.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import panda.ui.Ui;

/**
 * Controls Panda's main graphical user interface.
 */
public class MainWindow extends AnchorPane {
    private static final String BACKGROUND_STYLE_BAMBOO = "background-bamboo";
    private static final String BACKGROUND_STYLE_MOUNTAIN = "background-mountain";
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_SAVE = "save";
    private static final double SCROLL_SPEED_FACTOR = 1.4;

    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image botImage = loadImage("/images/DaMaster.png");

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button goButton;

    private PrintWriter pandaCommandWriter;
    private boolean isExitPending;

    /**
     * Enables manual scrolling and scrolls to the latest message when the dialog grows.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "FXML must inject scrollPane";
        assert dialogContainer != null : "FXML must inject dialogContainer";
        assert userInput != null : "FXML must inject userInput";
        assert goButton != null : "FXML must inject goButton";
        dialogContainer.heightProperty().addListener((_, _, _) ->
                Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax())));
        scrollPane.addEventFilter(ScrollEvent.SCROLL, this::scrollConversation);
        goButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                userInput.isDisabled() || userInput.getText().isBlank(),
                userInput.disabledProperty(), userInput.textProperty()));
        selectRandomBackground();
    }

    /**
     * Connects this window to Panda's command and response streams.
     *
     * @param commandOutputStream Stream that sends commands to Panda.
     * @param responseInputStream Stream that receives responses from Panda.
     */
    public void connectToPandaStreams(
            OutputStream commandOutputStream, InputStream responseInputStream) {
        pandaCommandWriter = new PrintWriter(
                Objects.requireNonNull(commandOutputStream), true, StandardCharsets.UTF_8);
        BufferedReader pandaResponseReader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(responseInputStream), StandardCharsets.UTF_8));
        startListeningForPandaMessages(pandaResponseReader);
    }

    /**
     * Loads an image resource from the application classpath.
     *
     * @param resourcePath Absolute classpath location of the image.
     * @return Loaded image.
     */
    private static Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(MainWindow.class.getResourceAsStream(resourcePath)));
    }

    /**
     * Selects either available background with equal probability.
     */
    private void selectRandomBackground() {
        String backgroundStyle = ThreadLocalRandom.current().nextBoolean()
                ? BACKGROUND_STYLE_BAMBOO
                : BACKGROUND_STYLE_MOUNTAIN;
        scrollPane.getStyleClass().add(backgroundStyle);
    }

    /**
     * Scrolls the conversation using a slightly amplified mouse-wheel or trackpad delta.
     *
     * @param event Scroll input received over the conversation.
     */
    private void scrollConversation(ScrollEvent event) {
        double scrollableHeight = dialogContainer.getHeight()
                - scrollPane.getViewportBounds().getHeight();
        if (scrollableHeight <= 0 || event.getDeltaY() == 0) {
            return;
        }

        double valueRange = scrollPane.getVmax() - scrollPane.getVmin();
        double valueDelta = event.getDeltaY() * SCROLL_SPEED_FACTOR * valueRange
                / scrollableHeight;
        double newValue = Math.clamp(
                scrollPane.getVvalue() - valueDelta,
                scrollPane.getVmin(),
                scrollPane.getVmax());
        scrollPane.setVvalue(newValue);
        event.consume();
    }

    /**
     * Reads Panda's response stream without blocking the JavaFX application thread.
     *
     * @param pandaResponseReader Reader connected to Panda's response stream.
     */
    private void startListeningForPandaMessages(BufferedReader pandaResponseReader) {
        Thread.ofVirtual().name("panda-response-listener").start(() -> {
            try {
                Ui.UiMessage message;
                while ((message = Ui.readNextUiMessage(pandaResponseReader)) != null) {
                    Ui.UiMessage messageToDisplay = message;
                    Platform.runLater(() -> handlePandaMessage(messageToDisplay));
                }
            } catch (IOException exception) {
                Ui.UiMessage errorMessage = new Ui.UiMessage(
                        Ui.MessageType.APPLICATION_ERROR,
                        "Your master has left the chat. Probably your fault: " + exception.getMessage());
                Platform.runLater(() -> displayPandaMessage(errorMessage));
            }
        });
    }

    /**
     * Sends the text field's contents to Panda and displays the user's message.
     */
    @FXML
    private void sendUserCommand() {
        if (isExitPending || userInput.getText().isBlank()) {
            return;
        }

        String command = userInput.getText();
        dialogContainer.getChildren().add(DialogBox.createUserDialog(command, userImage));
        if (command.strip().equals(COMMAND_BYE)) {
            beginExitRequest();
            pandaCommandWriter.println(COMMAND_SAVE);
        } else {
            pandaCommandWriter.println(command);
        }
        userInput.clear();
    }

    /**
     * Sends {@code save} through the command stream when the user closes the main window.
     */
    public void requestExit() {
        if (isExitPending) {
            return;
        }
        beginExitRequest();
        pandaCommandWriter.println(COMMAND_SAVE);
    }

    /**
     * Handles a response from Panda according to its stream message type.
     *
     * @param message Typed response received from Panda.
     */
    private void handlePandaMessage(Ui.UiMessage message) {
        if (message.type() == Ui.MessageType.EXIT_CONFIRMATION) {
            showExitConfirmation(message.text());
        } else {
            displayPandaMessage(message);
        }
    }

    /**
     * Marks an exit request as pending and prevents additional typed commands.
     */
    private void beginExitRequest() {
        isExitPending = true;
        userInput.setDisable(true);
    }

    /**
     * Shows the frontend confirmation and sends a command only when exit is confirmed.
     *
     * @param message Save result and confirmation text received from Panda.
     */
    private void showExitConfirmation(String message) {
        Scene scene = getScene();
        Window owner = scene == null ? null : scene.getWindow();
        ExitConfirmationDialog confirmation = new ExitConfirmationDialog(owner, message);

        if (confirmation.showAndWaitForConfirmation()) {
            pandaCommandWriter.println(COMMAND_BYE);
            return;
        }

        isExitPending = false;
        userInput.setDisable(false);
        userInput.requestFocus();
    }

    /**
     * Adds one response from Panda to the dialog container.
     *
     * @param message Typed response to display.
     */
    private void displayPandaMessage(Ui.UiMessage message) {
        DialogBox dialogBox = DialogBox.createPandaDialog(message, botImage);
        dialogContainer.getChildren().add(dialogBox);
    }
}
