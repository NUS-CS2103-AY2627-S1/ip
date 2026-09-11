package lynn;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Provides a graphical interface for Lynn while reusing the command-line core. */
public class LynnGui extends Application {
    private static final String WINDOW_TITLE = "Lynn";

    private static final double WINDOW_WIDTH = 700;

    private static final double WINDOW_HEIGHT = 500;

    private final ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();

    private Lynn lynn;

    private TextArea conversation;

    private TextField commandField;

    private Button sendButton;

    private Label statusLabel;

    private Ui ui;

    private int commandCount = 0;

    /** Creates a Lynn graphical application. */
    public LynnGui() {
    }

    /** Builds and displays Lynn's main window. */
    @Override
    public void start(Stage stage) {
        PrintStream responseOutput = new PrintStream(responseBuffer, true, StandardCharsets.UTF_8);
        ui = new Ui(responseOutput);
        lynn = new Lynn(ui);

        Label title = new Label("Lynn");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #17324d;");
        Label subtitle = new Label("Your upbeat study companion — one small step at a time");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #536779;");
        VBox header = new VBox(4, title, subtitle);
        header.setStyle("-fx-background-color: #e5f1ff; -fx-padding: 16px 20px;");

        conversation = new TextArea();
        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.setStyle("-fx-control-inner-background: #ffffff; -fx-font-size: 14px;");

        commandField = new TextField();
        commandField.setPromptText("Try: todo read book, list, or help");
        commandField.setTooltip(new Tooltip("Press Enter to send a command"));
        commandField.setOnAction(event -> sendCommand());

        sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setStyle("-fx-background-color: #2f80ed; -fx-text-fill: white; -fx-font-weight: bold;");
        sendButton.setOnAction(event -> sendCommand());

        Button clearButton = new Button("Clear chat");
        clearButton.setTooltip(new Tooltip("Clear the conversation; saved tasks are kept"));
        clearButton.setOnAction(event -> clearConversation());

        HBox inputRow = new HBox(8, commandField, sendButton, clearButton);
        HBox.setHgrow(commandField, Priority.ALWAYS);
        inputRow.setPadding(new Insets(10, 12, 6, 12));

        Label quickHelp = new Label(
                "Quick start: todo …   •   deadline … /by yyyy-MM-dd   •   event …   •   help");
        quickHelp.setStyle("-fx-text-fill: #536779; -fx-font-size: 12px;");

        statusLabel = new Label("Ready — your tasks are saved automatically");
        statusLabel.setStyle("-fx-text-fill: #536779; -fx-padding: 0 12px 10px 12px;");

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(conversation);
        root.setBottom(new VBox(inputRow, quickHelp, statusLabel));
        root.setStyle("-fx-background-color: #f5f8fc;");

        ui.showWelcome();
        appendBufferedResponse();

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }

    /** Sends the current command to Lynn and appends the response to the conversation. */
    private void sendCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) {
            conversation.appendText("You: (empty command)\n");
            lynn.respondTo(command);
            appendBufferedResponse();
            statusLabel.setText("Please enter a command, or choose help for guidance");
            return;
        }

        conversation.appendText("You: " + command + "\n");
        boolean shouldExit = lynn.respondTo(command);
        appendBufferedResponse();
        commandField.clear();
        commandCount++;
        statusLabel.setText("Commands this session: " + commandCount + " — tasks are saved automatically");

        if (shouldExit) {
            commandField.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /** Clears only the visible conversation while keeping Lynn's saved tasks unchanged. */
    private void clearConversation() {
        conversation.clear();
        conversation.appendText("Chat cleared. Your saved tasks are still safe.\n");
        commandField.requestFocus();
        statusLabel.setText("Chat cleared — saved tasks are unchanged");
    }

    /** Copies the latest core response into the conversation and clears the buffer. */
    private void appendBufferedResponse() {
        conversation.appendText(responseBuffer.toString(StandardCharsets.UTF_8));
        responseBuffer.reset();
    }
}
