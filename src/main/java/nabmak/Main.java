package nabmak;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Provides the JavaFX graphical user interface for Nabmak.
 * Users can enter commands and view Nabmak's responses.
 */
public class Main extends Application {
    private final Nabmak nabmak = new Nabmak();
    private final TextArea chatArea = new TextArea();

    @Override
    public void start(Stage stage) {
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setStyle("-fx-control-inner-background: #1e1e1e;"
                + "-fx-font-family: 'Papyrus';"
                + "-fx-font-size: 14px;"
                + "-fx-text-fill: #f5f5f5;");
        chatArea.appendText("Nabmak: Yo im Nabmak.\n");
        chatArea.appendText("Whatchu wanna do?\n\n");

        TextField inputField = new TextField();
        inputField.setPromptText("Enter a command...");
        inputField.setStyle("-fx-background-color: #2a2a2a;"
                + "-fx-font-family: 'Consolas';"
                + "-fx-font-size: 14px;"
                + "-fx-text-fill: #f5f5f5;"
                + "-fx-prompt-text-fill: #999999;");

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setStyle("-fx-background-color: #f5a623;"
                + "-fx-text-fill: #1e1e1e;"
                + "-fx-font-weight: bold;");

        Runnable sendCommand = () -> {
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                return;
            }

            String response = nabmak.processCommand(input);
            chatArea.appendText("You: " + input + "\n");

            if (response.startsWith("TOUGH!")) {
                chatArea.appendText("⚠ ERROR: " + response + "\n\n");
            } else {
                chatArea.appendText("Nabmak: " + response + "\n\n");
            }

            inputField.clear();

            if (input.equals("bye")) {
                stage.close();
            }
        };

        sendButton.setOnAction(event -> sendCommand.run());
        inputField.setOnAction(event -> sendCommand.run());

        HBox inputArea = new HBox(10, inputField, sendButton);
        inputArea.setPadding(new Insets(10));
        HBox.setHgrow(inputField, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setCenter(chatArea);
        root.setBottom(inputArea);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #520d41;");

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Nabmak");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.show();
    }
}
