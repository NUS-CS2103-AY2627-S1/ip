package probe.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import probe.ProbeException;
import probe.handler.CommandHandler;
import probe.parser.Parser;
import probe.storage.Storage;
import probe.task.TaskList;

/**
 * Provides a chatbot-style JavaFX user interface for the Probe task manager.
 */
public class Main extends Application {
    private static final String FILE_PATH = "probe.txt";
    private final Parser parser = new Parser();
    private final Storage storage = new Storage(FILE_PATH);
    private final TaskList tasks = new TaskList(storage.load());
    private final CommandHandler commandHandler = new CommandHandler(storage, tasks, parser);
    private final VBox dialogContainer = new VBox(8);
    private final ScrollPane scrollPane = new ScrollPane(dialogContainer);
    private TextField userInput;

    /**
     * Displays the JavaFX chatbot window.
     *
     * @param stage Primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        userInput = new TextField();
        userInput.setPromptText("Enter a command, e.g. todo read a book");
        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> handleInput());
        userInput.setOnAction(event -> handleInput());

        AnchorPane mainLayout = new AnchorPane(scrollPane, userInput, sendButton);
        configureLayout(mainLayout, sendButton);
        mainLayout.setStyle("-fx-background-color: #f8fafc;");
        userInput.setStyle("-fx-font-size: 14px; -fx-background-radius: 8;");
        sendButton.setStyle("-fx-font-weight: bold; -fx-background-radius: 8;");
        addProbeMessage("Hello! I'm Probe. How can I help you?");
        stage.setTitle("Probe");
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.setScene(new Scene(mainLayout, 500, 650));
        stage.show();
    }

    /**
     * Handles a command submitted through the chat input field.
     */
    private void handleInput() {
        String command = userInput.getText().trim();
        if (command.isBlank()) {
            return;
        }
        addUserMessage(command);
        userInput.clear();
        if (command.equals("bye")) {
            addProbeMessage("Bye. Hope to see you again soon!");
            Platform.exit();
            return;
        }
        try {
            addProbeMessage(commandHandler.execute(command));
        } catch (ProbeException exception) {
            addErrorMessage(exception.getMessage());
        }
    }

    /**
     * Configures the tutorial-style chat layout and its anchors.
     */
    private void configureLayout(AnchorPane mainLayout, Button sendButton) {
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8fafc; -fx-background-color: #f8fafc;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        dialogContainer.setFillWidth(true);
        dialogContainer.setPadding(new Insets(12, 10, 12, 10));
        dialogContainer.setStyle("-fx-background-color: #f8fafc;");
        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 55.0);
        AnchorPane.setLeftAnchor(userInput, 8.0);
        AnchorPane.setRightAnchor(userInput, 70.0);
        AnchorPane.setBottomAnchor(userInput, 8.0);
        AnchorPane.setRightAnchor(sendButton, 8.0);
        AnchorPane.setBottomAnchor(sendButton, 8.0);
    }

    /**
     * Adds a user message to the conversation.
     */
    private void addUserMessage(String message) {
        dialogContainer.getChildren().add(new DialogBox("You", message, true));
        scrollPane.setVvalue(1.0);
    }

    /**
     * Adds a Probe response to the conversation.
     */
    private void addProbeMessage(String message) {
        dialogContainer.getChildren().add(new DialogBox("Probe", message, false));
        scrollPane.setVvalue(1.0);
    }

    /**
     * Adds an error response with visually distinct styling.
     *
     * @param message Error message to display.
     */
    private void addErrorMessage(String message) {
        dialogContainer.getChildren().add(new DialogBox("Probe • Error", message, false, true));
        scrollPane.setVvalue(1.0);
    }

}
