package luckynoslacky.luckyui.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import luckynoslacky.CommandResult;
import luckynoslacky.LuckyNoSlacky;
import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.TextContent;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/**
 * Controls the main LuckyNoSlacky JavaFX window.
 */
public class MainWindow {
    private static final double EXIT_DELAY_SECONDS = 1.5;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private StackPane personalityBackground;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private LuckyNoSlacky chatbot;
    private final ExitScheduler exitScheduler;
    private final Runnable exitAction;
    private boolean exitScheduled;

    private final Image chatbotImage = new Image(
            MainWindow.class.getResourceAsStream(
                    "/images/luckynoslacky.jpg"));

    private final Image userImage = new Image(
            MainWindow.class.getResourceAsStream("/images/user.png"));

    /**
     * Creates the main-window controller with the production exit behavior.
     */
    public MainWindow() {
        this(MainWindow::scheduleExit, Platform::exit);
    }

    /**
     * Creates the main-window controller with injectable exit behavior.
     *
     * <p>The package-private dependencies allow GUI tests to observe delayed
     * exit without terminating the JavaFX test toolkit.</p>
     *
     * @param exitScheduler scheduler used to delay application exit
     * @param exitAction action performed after the delay
     * @throws IllegalArgumentException if either dependency is null
     */
    MainWindow(ExitScheduler exitScheduler, Runnable exitAction) {
        if (exitScheduler == null || exitAction == null) {
            throw new IllegalArgumentException(
                    "Exit scheduler and action cannot be null.");
        }
        this.exitScheduler = exitScheduler;
        this.exitAction = exitAction;
    }

    /**
     * Scrolls to the latest message whenever the conversation grows.
     */
    @FXML
    private void initialize() {
        addPersonalityStyleClasses();
        bindConversationBackgroundHeight();
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                        scrollPane.setVvalue(1.0));
        configureKeyboardTraversal();
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Provides the chatbot used to process user commands.
     *
     * @param chatbot chatbot backend
     * @throws IllegalArgumentException if {@code chatbot} is null
     */
    public void setChatbot(LuckyNoSlacky chatbot) {
        if (chatbot == null) {
            throw new IllegalArgumentException("Chatbot cannot be null.");
        }
        this.chatbot = chatbot;
        addChatbotMessage(
                new TextContent(LuckyNoQuips.greeting()),
                ResponseTone.NEUTRAL);

        if (chatbot.hasLoadError()) {
            addChatbotMessage(
                    new TextContent(LuckyNoQuips.loadErrorMessage()),
                    ResponseTone.SYSTEM_ERROR);
        }
    }

    /**
     * Processes a command entered by the user.
     */
    @FXML
    private void handleUserInput() {
        String userInputText = userInput.getText().trim();
        if (userInputText.isEmpty()) {
            userInput.requestFocus();
            return;
        }

        addUserMessage(userInputText);
        CommandResult response = chatbot.getResponse(userInputText);
        addChatbotMessage(response.content(), response.tone());
        userInput.clear();

        if (response.shouldExit() && !exitScheduled) {
            exitScheduled = true;
            userInput.setDisable(true);
            sendButton.setDisable(true);
            scrollPane.requestFocus();
            exitScheduler.schedule(
                    Duration.seconds(EXIT_DELAY_SECONDS), exitAction);
        } else {
            userInput.requestFocus();
        }
    }

    /**
     * Schedules an action on the JavaFX timeline after the requested delay.
     *
     * @param delay delay before running the action
     * @param action action to run after the delay
     */
    private static void scheduleExit(Duration delay, Runnable action) {
        PauseTransition pause = new PauseTransition(delay);
        pause.setOnFinished(event -> action.run());
        pause.play();
    }

    /** Schedules a delayed action used by the main window's exit flow. */
    @FunctionalInterface
    interface ExitScheduler {
        /**
         * Schedules an action to run after a delay.
         *
         * @param delay delay before running the action
         * @param action action to run after the delay
         */
        void schedule(Duration delay, Runnable action);
    }

    /**
     * Adds a user message aligned to the right.
     *
     * @param message user message
     */
    private void addUserMessage(String message) {
        dialogContainer.getChildren().add(
                new DialogueBox(
                        message,
                        userImage,
                        DialogueBox.DialogueType.USER));
    }

    /**
     * Adds an application message using the appropriate visual role.
     *
     * @param responseContent structured chatbot response
     * @param responseTone semantic tone for the response
     */
    private void addChatbotMessage(
            ResponseContent responseContent,
            ResponseTone responseTone) {
        dialogContainer.getChildren().add(
                new DialogueBox(
                        chatbotImage,
                        DialogueBox.DialogueType.CHATBOT,
                        responseTone,
                        responseContent));
    }

    /** Adds stable semantic hooks consumed by the personality stylesheet. */
    private void addPersonalityStyleClasses() {
        userInput.getStyleClass().add("personality-interface-text");
        userInput.getStyleClass().add("personality-input-field");
        sendButton.getStyleClass().add("personality-interface-text");
        sendButton.getStyleClass().add("personality-send-button");
    }

    /** Keeps the patterned conversation background at least as tall as its viewport. */
    private void bindConversationBackgroundHeight() {
        personalityBackground.minHeightProperty().bind(
                Bindings.createDoubleBinding(() ->
                        scrollPane.getViewportBounds().getHeight(),
                        scrollPane.viewportBoundsProperty()));
    }

    /** Configures keyboard traversal for the main interactive controls. */
    private void configureKeyboardTraversal() {
        configureTraversal(userInput, sendButton, scrollPane);
        configureTraversal(sendButton, scrollPane, userInput);
        configureTraversal(scrollPane, userInput, sendButton);
    }

    /**
     * Configures forward and reverse Tab traversal for one interactive node.
     *
     * @param currentNode node receiving the Tab key
     * @param forwardNode node focused by Tab
     * @param backwardNode node focused by Shift+Tab
     */
    private void configureTraversal(
            Node currentNode,
            Node forwardNode,
            Node backwardNode) {
        currentNode.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                Node nextNode = event.isShiftDown() ? backwardNode : forwardNode;
                nextNode.requestFocus();
                event.consume();
            }
        });
    }
}
