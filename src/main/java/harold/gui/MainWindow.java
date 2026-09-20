package harold.gui;

import harold.CommandResult;
import harold.Harold;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Displays the conversation and forwards submitted commands to Harold.
 */
public class MainWindow extends BorderPane {
    private static final String WELCOME_MESSAGE =
            "Arf! I'm Harold, your mischievous Frenchie task-fetcher.\n"
                    + "Tasks are my treats. nomnomnom ball!";

    private final Harold harold;
    private final VBox conversation = new VBox(14);
    private final ScrollPane conversationScroll = new ScrollPane(conversation);
    private final TextField commandInput = new TextField();
    private final Button sendButton = new Button("FETCH  🐾");

    /**
     * Creates the main conversation window for the given Harold instance.
     *
     * @param harold Chatbot that handles submitted commands.
     */
    public MainWindow(Harold harold) {
        this.harold = harold;
        getStyleClass().add("app-shell");
        setTop(createHeader());
        setCenter(createConversationArea());
        setBottom(createCommandBar());

        addMessage(WELCOME_MESSAGE, false);
        if (harold.getLoadMessage() != null) {
            addMessage("i pooped on myself... " + harold.getLoadMessage(), false);
        }
    }

    /**
     * Creates the branded application header.
     */
    private Node createHeader() {
        Label pawBadge = new Label("🐾");
        pawBadge.getStyleClass().add("paw-badge");

        Label title = new Label("HAROLD");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("MISCHIEVOUS FRENCHIE TASK-FETCHER");
        subtitle.getStyleClass().add("app-subtitle");

        VBox titles = new VBox(2, title, subtitle);
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        HBox header = new HBox(
                14, pawBadge, titles, headerSpacer, createBallDecorations()
        );
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 24, 18, 24));
        header.getStyleClass().add("app-header");
        return header;
    }

    /**
     * Creates the offset ball motif used in Harold's header.
     */
    private Node createBallDecorations() {
        Region smallBall = new Region();
        smallBall.getStyleClass().addAll("ball-icon", "small-ball");
        Region largeBall = new Region();
        largeBall.getStyleClass().addAll("ball-icon", "large-ball");

        HBox balls = new HBox(8, smallBall, largeBall);
        balls.setAlignment(Pos.CENTER);
        balls.getStyleClass().add("ball-decorations");
        return balls;
    }

    /**
     * Creates the scrollable conversation surface.
     */
    private Node createConversationArea() {
        conversation.setPadding(new Insets(22));
        conversation.setFillWidth(true);
        conversation.getStyleClass().add("conversation");

        conversationScroll.setFitToWidth(true);
        conversationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationScroll.getStyleClass().add("conversation-scroll");
        conversation.heightProperty().addListener((observable, oldHeight, newHeight) ->
                conversationScroll.setVvalue(1.0));
        return conversationScroll;
    }

    /**
     * Creates the command field and send button.
     */
    private Node createCommandBar() {
        commandInput.setPromptText("Throw me a command: todo give Harold a treat");
        commandInput.getStyleClass().add("command-input");
        commandInput.setOnAction(event -> submitCommand());
        HBox.setHgrow(commandInput, Priority.ALWAYS);

        sendButton.getStyleClass().add("send-button");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submitCommand());

        HBox commandBar = new HBox(12, commandInput, sendButton);
        commandBar.setAlignment(Pos.CENTER);
        commandBar.setPadding(new Insets(16, 20, 20, 20));
        commandBar.getStyleClass().add("command-bar");
        return commandBar;
    }

    /**
     * Sends the current input to Harold and displays both sides of the exchange.
     */
    private void submitCommand() {
        String command = commandInput.getText();
        commandInput.clear();
        addMessage(command.isBlank() ? "(empty command)" : command, true);

        CommandResult result = harold.respond(command);
        addMessage(result.message(), false);
        if (result.isExit()) {
            commandInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        } else {
            commandInput.requestFocus();
        }
    }

    /**
     * Adds one user or chatbot bubble to the conversation.
     */
    private void addMessage(String message, boolean isUser) {
        Label avatar = new Label(isUser ? "YOU" : "🐾");
        avatar.getStyleClass().add(isUser ? "user-avatar" : "harold-avatar");

        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMinHeight(Region.USE_PREF_SIZE);
        bubble.setMaxWidth(480);
        bubble.getStyleClass().add(isUser ? "user-bubble" : "harold-bubble");

        HBox row = new HBox(10);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        if (isUser) {
            row.getChildren().addAll(bubble, avatar);
        } else {
            row.getChildren().addAll(avatar, bubble);
        }
        conversation.getChildren().add(row);
    }
}
