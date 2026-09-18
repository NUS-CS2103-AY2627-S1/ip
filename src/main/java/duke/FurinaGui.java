package duke;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** JavaFX interface that lets users interact with Furina through typed commands. */
public class FurinaGui extends Application {
    private static final String PRODUCT_NAME = "Furina";

    private static final String BOT_GREETING = "Hello! I'm Furina, your delightfully organised task companion.";

    private final CommandHandler handler = new CommandHandler();

    private final VBox conversation = new VBox(10);

    private final TextField commandInput = new TextField();

    private final Button sendButton = new Button("Send");

    /** Builds and displays the chatbot window. */
    @Override
    public void start(Stage stage) {
        conversation.setPadding(new Insets(16));
        conversation.setFillWidth(true);
        addBotMessage(BOT_GREETING);
        addBotMessage("Tell me what you need to remember, and I will keep it neatly organised.");

        ScrollPane conversationScroll = new ScrollPane(conversation);
        conversationScroll.setFitToWidth(true);
        conversationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationScroll.setStyle("-fx-background: #f4f7fb; -fx-background-color: #f4f7fb;");

        commandInput.setPromptText("Try: todo read book");
        commandInput.setStyle("-fx-font-size: 14px; -fx-padding: 9px;");
        sendButton.setDefaultButton(true);
        sendButton.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold;");
        sendButton.setOnAction(event -> sendCommand());
        commandInput.setOnAction(event -> sendCommand());

        Label commandLabel = new Label("Command");
        commandLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        HBox inputRow = new HBox(10, commandLabel, commandInput, sendButton);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        inputRow.setPadding(new Insets(12, 16, 16, 16));
        HBox.setHgrow(commandInput, Priority.ALWAYS);

        Label title = new Label(PRODUCT_NAME + "  •  your task companion");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setPadding(new Insets(16));
        title.setStyle("-fx-background-color: #312e81; -fx-text-fill: white;"
                + " -fx-font-size: 18px; -fx-font-weight: bold;");

        BorderPane root = new BorderPane(conversationScroll, title, null, inputRow, null);
        root.setStyle("-fx-background-color: #f4f7fb;");
        stage.setTitle(PRODUCT_NAME);
        stage.setMinWidth(520);
        stage.setMinHeight(360);
        stage.setScene(new Scene(root, 720, 520));
        stage.show();
        commandInput.requestFocus();
    }

    private void sendCommand() {
        String command = commandInput.getText().trim();
        if (command.isBlank()) {
            return;
        }

        addUserMessage(command);
        String response = handler.handleCommand(command);
        addBotMessage(response);
        commandInput.clear();
        if (command.equalsIgnoreCase("bye")) {
            commandInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    private void addUserMessage(String message) {
        Label userMessage = createMessage(message, "#dbeafe", "#1e3a8a");
        userMessage.setAlignment(Pos.CENTER_RIGHT);
        conversation.getChildren().add(userMessage);
    }

    private void addBotMessage(String message) {
        String background = message.startsWith("OOPS!!!") ? "#fee2e2" : "#ffffff";
        String text = message.startsWith("OOPS!!!") ? "#991b1b" : "#1e293b";
        conversation.getChildren().add(createMessage(message, background, text));
    }

    private Label createMessage(String message, String background, String text) {
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setPadding(new Insets(10, 12, 10, 12));
        messageLabel.setStyle("-fx-background-color: " + background + "; -fx-text-fill: " + text + ";"
                + " -fx-font-size: 14px; -fx-background-radius: 10px;");
        return messageLabel;
    }
}
