package niulai.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import niulai.NiuLai;

/** Controller for the main NiuLai JavaFX window. */
public class MainWindow extends BorderPane {
    /** The scrollable area containing the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** The container holding all conversation dialog boxes. */
    @FXML
    private VBox dialogContainer;

    /** The text field where the user types a command. */
    @FXML
    private TextField userInput;

    /** The button that submits the current command. */
    @FXML
    private Button sendButton;

    /** The chatbot whose commands are handled by this window. */
    private NiuLai chatbot;

    /** Captures responses produced by the chatbot for display. */
    private GuiUi ui;

    /** Binds the scroll position to the height of the conversation. */
    @FXML
    public void initialize() {
        // The FXML loader must inject every control used by this controller.
        assert scrollPane != null && dialogContainer != null
                && userInput != null && sendButton != null
                : "MainWindow.fxml must inject every required control.";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot and output collector used by this controller.
     *
     * @param chatbot the chatbot that processes user commands
     * @param ui the output collector connected to the chatbot
     */
    public void setChatbot(NiuLai chatbot, GuiUi ui) {
        this.chatbot = chatbot;
        this.ui = ui;
    }

    /** Adds a chatbot response to the conversation. */
    public void showBotMessage(String message) {
        if (!message.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getBotDialog(message));
        }
    }

    /** Handles both the Send button and Enter key in the input field. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (!input.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        }

        boolean shouldExit = chatbot.processCommand(input);
        showBotMessage(ui.consumeOutput());
        userInput.clear();

        if (shouldExit) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
