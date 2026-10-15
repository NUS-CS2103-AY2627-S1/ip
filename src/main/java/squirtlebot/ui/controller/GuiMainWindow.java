package squirtlebot.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import squirtlebot.CommandResult;
import squirtlebot.SquirtleBot;
import squirtlebot.ui.view.DialogBox;

/**
 * Controls user interactions within SquirtleBot's GUI.
 * Accepts user input and sends it to SquirtleBot for processing.
 * Displays both user input and SquirtleBot's response.
 */
public class GuiMainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private SquirtleBot squirtleBot;
    private boolean isAwaitingStorageDecision = false;

    private final Image botImage = new Image(this.getClass().getResourceAsStream("/images/squirtle.jpg"));
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));


    /**
     * Initializes GUI by binding scroll position to dialog container's height.
     * Ensures scroll pane automatically shows the latest message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }


    /**
     * Sets instance of {@link SquirtleBot} to be used for handling logic of the bot.
     *
     * @param squirtleBot SquirtleBot instance to be used.
     */
    public void setSquirtleBot(SquirtleBot squirtleBot) {
        this.squirtleBot = squirtleBot;
    }

    /**
     * Extracts user input from input field.
     * If the application is expecting a storage decision, user input is used to process the user's decision.
     * Otherwise, user input is sent to {@link SquirtleBot} to retrieve a response
     */
    @FXML
    private void handleUserInput() {
        if (isAwaitingStorageDecision) {
            handleStorageDecision();
            return;
        }
        String userInputText = userInput.getText();
        CommandResult commandResult = squirtleBot.getResponse(userInputText);
        if (commandResult.shouldExit()) {
            System.exit(0);
        }

        addUserMessageToDisplay(userInputText);
        addBotMessageToDisplay(commandResult.message());
        userInput.clear();
    }

    /**
     * Checks if user wants to continue without storage features.
     */
    private void handleStorageDecision() {
        String storageDecision = userInput.getText();
        if (storageDecision.equals("Y")) {
            squirtleBot.disableStorage();
            isAwaitingStorageDecision = false;

            addUserMessageToDisplay(storageDecision);
            addBotMessageToDisplay(SquirtleBot.CONTINUE_WITHOUT_STORAGE_RESPONSE);
            userInput.clear();
        } else if (storageDecision.equals("N")) {
            System.exit(0);
        } else {
            promptOnStorageIssue();
        }
    }

    /**
     * Retrieves SquirtleBot's welcome message and displays message in a {@link DialogBox}.
     */
    public void displayWelcomeMessage() {
        addBotMessageToDisplay(squirtleBot.getWelcomeMessage());
    }

    /**
     * Adds message to prompt user for decision after encountering storage issues.
     */
    public void promptOnStorageIssue() {
        isAwaitingStorageDecision = true;
        addBotMessageToDisplay(SquirtleBot.STORAGE_ISSUE_PROMPT);
    }

    /**
     * Adds bot's message as dialog box to dialog container.
     */
    private void addBotMessageToDisplay(String botMessage) {
        dialogContainer.getChildren().add(DialogBox.createSquirtleBotDialog(botMessage, botImage));
    }

    /**
     * Adds user's message as dialog box to dialog container.
     */
    private void addUserMessageToDisplay(String userInput) {
        dialogContainer.getChildren().add(DialogBox.createUserDialog(userInput, userImage));
    }

}
