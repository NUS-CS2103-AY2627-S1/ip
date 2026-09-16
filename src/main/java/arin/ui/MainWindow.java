package arin.ui;

import arin.Arin;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Presents commands and responses as a resizable, keyboard-accessible conversation.
 */
public class MainWindow {
    private final Arin arin;

    @FXML
    private VBox conversation;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField commandInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button helpButton;

    /**
     * Connects this window to a shared command session.
     *
     * @param arin Session that validates commands and saves tasks.
     */
    public MainWindow(Arin arin) {
        this.arin = arin;
    }

    /**
     * Adds startup feedback once the FXML controls have been injected.
     */
    @FXML
    private void initialize() {
        addMessage(arin.getWelcome(), false);
        conversation.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollPane.setVvalue(1.0));
        Platform.runLater(commandInput::requestFocus);
    }

    @FXML
    private void sendCommand() {
        String command = commandInput.getText();
        commandInput.clear();
        addMessage(command.isBlank() ? "(empty command)" : command, true);
        addMessage(arin.getResponse(command), false);
        if (arin.isExit()) {
            commandInput.setDisable(true);
            sendButton.setDisable(true);
            helpButton.setDisable(true);
            PauseTransition goodbyeDelay = new PauseTransition(Duration.seconds(1));
            goodbyeDelay.setOnFinished(event -> Platform.exit());
            goodbyeDelay.play();
        } else {
            commandInput.requestFocus();
        }
    }

    @FXML
    private void showHelp() {
        commandInput.setText("help");
        sendCommand();
    }

    /**
     * Uses asymmetric alignment and explicit error labels, with text that wraps on resize.
     */
    private void addMessage(String text, boolean isUser) {
        boolean isError = !isUser && text.startsWith("Oops!");
        Label sender = new Label(isUser ? "YOU" : isError ? "ARIN · CHECK YOUR COMMAND" : "ARIN");
        sender.getStyleClass().add("message-sender");
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMinWidth(0);
        message.setMinHeight(Region.USE_PREF_SIZE);
        message.setMaxWidth(Double.MAX_VALUE);
        message.setAccessibleText(text);
        message.getStyleClass().add("message-text");
        VBox bubble = new VBox(6, sender, message);
        bubble.setMinWidth(0);
        bubble.maxWidthProperty().bind(scrollPane.widthProperty().subtract(isUser ? 110 : 56));
        bubble.getStyleClass().addAll("message", isUser ? "user-message" : "bot-message");
        if (isError) {
            bubble.getStyleClass().add("error-message");
        }
        HBox row = new HBox(bubble);
        row.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        conversation.getChildren().add(row);
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
