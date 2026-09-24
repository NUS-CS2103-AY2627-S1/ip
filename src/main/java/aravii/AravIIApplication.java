package aravii;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays the Arav II task manager in a JavaFX window.
 */
public class AravIIApplication extends Application {
    private static final Path DATA_FILE = Path.of("data", "aravii.txt");

    private static final String WELCOME_MESSAGE = "Hello! I'm Arav (II).\n"
            + "What can I do for you?\n"
            + "Type a command below, or type help to see the available commands.";

    private TextArea conversation;

    private ChatSession session;

    /**
     * Creates the JavaFX application, with storage loaded when the window starts.
     */
    public AravIIApplication() {
    }

    /**
     * Creates and displays the main application window.
     *
     * @param stage The primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        session = new ChatSession(new Storage(DATA_FILE));
        conversation = new TextArea(WELCOME_MESSAGE);
        if (!session.getStartupError().isEmpty()) {
            conversation.appendText("\n\n" + session.getStartupError());
        }
        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.setPrefHeight(360);

        TextField input = new TextField();
        input.setPromptText("Enter a command");
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> handleInput(input));

        HBox inputRow = new HBox(8, input, sendButton);
        HBox.setHgrow(input, javafx.scene.layout.Priority.ALWAYS);

        Label title = new Label("Arav (II) Task Manager");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox content = new VBox(12, title, conversation, inputRow);
        content.setPadding(new Insets(16));

        BorderPane root = new BorderPane(content);
        Scene scene = new Scene(root, 560, 460);
        stage.setTitle("Arav (II)");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            String response = session.respond("bye");
            if (!session.shouldExit()) {
                event.consume();
                conversation.appendText("\n\nArav (II): " + response);
            }
        });
        stage.show();
        input.requestFocus();
    }

    /**
     * Displays a response to a command entered by the user.
     *
     * @param input The text field containing the command.
     */
    private void handleInput(TextField input) {
        String command = input.getText();
        if (command.isEmpty()) {
            return;
        }
        conversation.appendText("\n\nYou: " + command);
        conversation.appendText("\nArav (II): " + session.respond(command));
        input.clear();
        if (session.shouldExit()) {
            input.setDisable(true);
            Platform.exit();
        }
    }

}
