package henry.gui;

import henry.Henry;
import henry.parser.CommandType;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls Henry's main chat window.
 */
public class MainWindow {
    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image henryImage = new Image(
            this.getClass().getResourceAsStream("/images/HenryCow.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    private Henry henry;

    /**
     * Keeps the newest dialog visible as messages are added.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                userInput.getText().trim().isEmpty(), userInput.textProperty()));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Connects this window to Henry and displays his greeting.
     *
     * @param henry chatbot that processes user commands.
     */
    public void setHenry(Henry henry) {
        this.henry = henry;
        dialogContainer.getChildren().add(DialogBox.createHenryDialog(
                createGreeting(), henryImage, CommandType.UNKNOWN));
    }

    /**
     * Sends the entered command to Henry and displays both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = henry.getResponse(input);
        CommandType commandType = henry.getLastCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(input, userImage),
                DialogBox.createHenryDialog(response, henryImage, commandType));
        userInput.clear();

        if (commandType == CommandType.BYE) {
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
            return;
        }
        userInput.requestFocus();
    }

    private String createGreeting() {
        String greeting = "Hey, I'm Henry.\nWhat are we tackling today?";
        if (henry.getStartupMessage().isEmpty()) {
            return greeting;
        }
        return greeting + "\n\n" + henry.getStartupMessage();
    }
}
