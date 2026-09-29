package xue;

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
import xue.storage.Storage;
import xue.ui.Ui;

/** JavaFX user interface for Xue, backed by the reusable command processor. */
public class Main extends Application {
    private static final String WINDOW_TITLE = "Xue | Your Reluctant Task Assistant";
    private static final String APP_BACKGROUND = "-fx-background-color: #17202a;";
    private static final String CONVERSATION_STYLE = "-fx-control-inner-background: #202b36;"
            + "-fx-text-fill: #ecf0f1; -fx-highlight-fill: #3f6f8f;"
            + "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;";
    private static final String INPUT_STYLE = "-fx-background-color: #263746; -fx-text-fill: #ecf0f1;"
            + "-fx-prompt-text-fill: #91a4b5; -fx-font-size: 14px;";
    private static final String BUTTON_STYLE = "-fx-background-color: #4aa3df; -fx-text-fill: white;"
            + "-fx-font-weight: bold; -fx-background-radius: 6px;";

    private final CommandProcessor processor = new CommandProcessor(new Storage());
    private final TextArea conversation = new TextArea();
    private final TextField commandInput = new TextField();

    /** Builds and displays the main window. */
    @Override
    public void start(Stage stage) {
        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.setStyle(CONVERSATION_STYLE);
        conversation.appendText(Ui.WELCOME_MESSAGE + "\n\n");
        if (processor.getLoadError() != null) {
            conversation.appendText("Xue: OOPS!!! " + processor.getLoadError() + "\n\n");
        }
        commandInput.setPromptText("Enter a command");
        commandInput.setStyle(INPUT_STYLE);
        Button sendButton = new Button("Send");
        sendButton.setStyle(BUTTON_STYLE);
        sendButton.setOnAction(event -> submitCommand());
        commandInput.setOnAction(event -> submitCommand());

        HBox inputRow = new HBox(8, commandInput, sendButton);
        HBox.setHgrow(commandInput, Priority.ALWAYS);
        inputRow.setStyle(APP_BACKGROUND);
        inputRow.setPadding(new Insets(0, 8, 8, 8));
        BorderPane root = new BorderPane(conversation);
        root.setBottom(inputRow);
        root.setStyle(APP_BACKGROUND);
        BorderPane.setMargin(conversation, new Insets(8));

        stage.setTitle(WINDOW_TITLE);
        stage.setMinWidth(420);
        stage.setMinHeight(300);
        stage.setScene(new Scene(root, 600, 450));
        stage.show();
    }

    /** Adds both the command and response to the conversation history. */
    private void submitCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        conversation.appendText("You: " + command + "\n");
        String response = processor.process(command);
        String responseLabel = response.startsWith("OOPS!!!") ? "Xue ⚠: " : "Xue: ";
        conversation.appendText(responseLabel + response + "\n\n");
        commandInput.clear();
    }
}
