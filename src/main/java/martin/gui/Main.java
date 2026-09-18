package martin.gui;

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
import martin.core.Martin;
import martin.exception.MartinException;
import martin.ui.Ui;

/**
 * A JavaFX application that provides a graphical interface for Martin.
 */
public class Main extends Application {

    private static final String WINDOW_TITLE = "Martin";
    private static final double WINDOW_WIDTH = 680;
    private static final double WINDOW_HEIGHT = 520;
    private static final double MAX_RESPONSE_WIDTH = 540;
    private static final double MAX_COMMAND_WIDTH = 360;
    private static final String MARTIN_FONT_FAMILY = "Garamond";

    /** Processes commands entered through the graphical interface. */
    private Martin martin;
    /** Holds the conversation entries in chronological order. */
    private VBox messages;
    /** Provides scrolling for the conversation. */
    private ScrollPane transcript;
    /** Accepts the user's next command. */
    private TextField commandInput;
    /** Submits the text currently in the command field. */
    private Button sendButton;

    /**
     * Creates and displays the application's primary window.
     *
     * @param stage The primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        this.messages = new VBox(14);
        this.messages.setPadding(new Insets(20));

        this.transcript = new ScrollPane(this.messages);
        this.transcript.setFitToWidth(true);
        this.transcript.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.transcript.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.transcript.setStyle("-fx-background: #f6f8fb; -fx-background-color: #f6f8fb;");

        this.commandInput = new TextField();
        this.commandInput.setPromptText("Type a command, e.g. todo read book");
        this.commandInput.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; "
                + "-fx-border-color: #d7dde7; -fx-padding: 10 12;");
        this.sendButton = new Button("Send");
        this.sendButton.setDefaultButton(true);
        this.sendButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 18;");
        this.sendButton.setOnAction(event -> this.submitCommand());
        this.commandInput.setOnAction(event -> this.submitCommand());

        HBox inputBar = new HBox(10, this.commandInput, this.sendButton);
        inputBar.setPadding(new Insets(14, 20, 18, 20));
        inputBar.setStyle("-fx-background-color: white; "
                + "-fx-border-color: #e4e8ef transparent transparent transparent;");
        HBox.setHgrow(this.commandInput, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(this.createHeader());
        root.setCenter(this.transcript);
        root.setBottom(inputBar);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();

        try {
            this.martin = new Martin();
            this.appendMartinMessage(Ui.MARTIN_GREETING);
        } catch (MartinException exception) {
            this.appendMartinMessage("Martin could not start: " + exception.getMessage());
            this.commandInput.setDisable(true);
            this.sendButton.setDisable(true);
        }
    }

    /**
     * Processes the command currently entered in the input field.
     */
    private void submitCommand() {
        String input = this.commandInput.getText().trim();
        if (input.isEmpty() || this.martin == null || !this.martin.isRunning()) {
            return;
        }

        String response = this.martin.executeCommand(input);
        this.appendUserMessage(input);
        this.appendMartinMessage(response);
        this.commandInput.clear();

        if (!this.martin.isRunning()) {
            this.commandInput.getScene().getWindow().hide();
        }
    }

    /**
     * Adds a user command to the chat transcript.
     *
     * @param input The command entered by the user.
     */
    private void appendUserMessage(String input) {
        Label commandLabel = this.createWrappedLabel(input, MAX_COMMAND_WIDTH);
        commandLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        VBox commandChip = new VBox(commandLabel);
        commandChip.setMaxWidth(MAX_COMMAND_WIDTH);
        commandChip.setPadding(new Insets(10, 14, 10, 14));
        commandChip.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 14 14 3 14;");
        this.addMessageRow(commandChip, Pos.CENTER_RIGHT);
    }

    /**
     * Adds a Martin response to the chat transcript.
     *
     * @param message The response from Martin.
     */
    private void appendMartinMessage(String message) {
        Label senderLabel = new Label("MARTIN");
        senderLabel.setStyle("-fx-font-family: '" + MARTIN_FONT_FAMILY + "'; -fx-text-fill: #2563eb; "
                + "-fx-font-size: 11px; -fx-font-weight: bold;");

        Label messageLabel = this.createWrappedLabel(message, MAX_RESPONSE_WIDTH);
        messageLabel.setStyle("-fx-font-family: '" + MARTIN_FONT_FAMILY + "'; -fx-text-fill: #1f2937; "
                + "-fx-font-size: 16px;");

        VBox responseCard = new VBox(5, senderLabel, messageLabel);
        responseCard.setMaxWidth(MAX_RESPONSE_WIDTH);
        responseCard.setPadding(new Insets(12, 16, 12, 16));
        responseCard.setStyle("-fx-background-color: white; -fx-background-radius: 4 14 14 14; "
                + "-fx-border-color: #e3e8f0; -fx-border-radius: 4 14 14 14;");
        this.addMessageRow(responseCard, Pos.CENTER_LEFT);
    }

    /**
     * Creates a label that grows vertically to fit a multi-line message.
     *
     * @param message  The message to display.
     * @param maxWidth The largest width allowed for the message.
     * @return A configured message label.
     */
    private Label createWrappedLabel(String message, double maxWidth) {
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(maxWidth);
        return messageLabel;
    }

    /**
     * Adds a conversation entry aligned to the appropriate side of the transcript.
     *
     * @param messageEntry The visual representation of the message.
     * @param alignment    The side of the transcript on which to show the entry.
     */
    private void addMessageRow(VBox messageEntry, Pos alignment) {
        HBox messageRow = new HBox(messageEntry);
        messageRow.setAlignment(alignment);
        this.messages.getChildren().add(messageRow);
        this.transcript.layout();
        this.transcript.setVvalue(1.0);
    }

    /**
     * Creates the fixed application heading above the conversation.
     *
     * @return The heading for the application window.
     */
    private VBox createHeader() {
        Label title = new Label(WINDOW_TITLE);
        title.setStyle("-fx-text-fill: #172554; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitle = new Label("Your command assistant");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        VBox header = new VBox(2, title, subtitle);
        header.setPadding(new Insets(16, 20, 16, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #e4e8ef transparent;");
        return header;
    }
}
