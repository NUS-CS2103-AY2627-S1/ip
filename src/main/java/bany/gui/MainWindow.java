package bany.gui;

import java.util.List;
import java.util.Objects;

import bany.BanyService;
import bany.commands.CommandResult;
import bany.commands.ResponseMessage;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * JavaFX controller for Bany's main chat window.
 *
 * <p>The controller accepts user input, delegates command processing to
 * {@link BanyService}, and appends both sides of the conversation to the
 * dialog container.</p>
 */
public class MainWindow extends AnchorPane {
    /** Scrollable viewport that displays the conversation history. */
    @FXML
    private ScrollPane scrollPane;
    /** Container to which user and Bany dialog boxes are appended. */
    @FXML
    private VBox dialogContainer;
    /** Button that submits the current text-field value. */
    @FXML
    private Button sendButton;
    /** Text field in which the user enters a command. */
    @FXML
    private TextField userInput;

    /** Service that executes commands for this window. */
    private BanyService banyService;

    /** Avatar used for dialog boxes containing user messages. */
    private Image userImage;
    /** Avatar used for dialog boxes containing Bany responses. */
    private Image banyImage;

    /** Creates the controller whose controls are subsequently injected by the FXML loader. */
    public MainWindow() {
    }

    /**
     * Loads dialog images and keeps the conversation scrolled to its newest entry.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/bany/gui/images/DaUser.png")));
        banyImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/bany/gui/images/BanyLogo.png")));

    }

    /**
     * Supplies the service that this window uses to execute commands.
     *
     * @param banyService configured command-execution service.
     */
    public void setBanyService(BanyService banyService) {
        this.banyService = banyService;
    }

    /**
     * Sends the current input to Bany and adds the input and response to the chat.
     */
    @FXML
    public void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        dialogContainer.getChildren().add(
                DialogBox.getUserDialog(input, userImage)
        );

        CommandResult commandResult = banyService.executeCommand(userInput.getText());
        List<ResponseMessage> responses = commandResult.messages();

        for (ResponseMessage response : responses) {

            dialogContainer.getChildren().add(
                    DialogBox.getBotDialog(response, banyImage)
            );

        }

        boolean isExit = commandResult.shouldExit();

        if (isExit) {
            sendButton.setDisable(true);
            userInput.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }

        userInput.clear();
    }

    /** Adds Bany's initial greeting to the conversation history. */
    public void showWelcomeMessage() {
        ResponseMessage welcomeResponse = ResponseMessage.info(
                banyService.getWelcomeMessage().text());

        dialogContainer.getChildren().addAll(
                DialogBox.getBotDialog(welcomeResponse, banyImage)
        );
    }

    /**
     * Displays a startup warning or error using the message's existing GUI style.
     *
     * @param response startup message and its presentation level.
     */
    public void showStartupMessage(ResponseMessage response) {
        dialogContainer.getChildren().add(DialogBox.getBotDialog(response, banyImage));
    }

    /** Prevents commands from overwriting startup data when recovery has failed. */
    public void disableInput() {
        sendButton.setDisable(true);
        userInput.setDisable(true);
    }
}
