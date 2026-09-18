package computah;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import computah.command.Command;
import computah.exception.ComputahException;
import computah.model.Model;
import computah.parser.Parser;
import computah.storage.Storage;
import computah.ui.Ui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Shows the JavaFX graphical interface for Computah.
 */
public class Main extends Application {
    private static final String CONSOLE_DIVIDER = "____________________________________________________________";

    private final Storage storage = new Storage("data/duke.txt", "data/clients.txt");
    private final Model model = new Model();

    private VBox conversation;
    private ScrollPane conversationPane;
    private TextField commandBox;
    private Button sendButton;

    /**
     * Creates the JavaFX Computah application.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        conversation = new VBox(20);
        conversation.getStyleClass().add("conversation");

        conversationPane = new ScrollPane(conversation);
        conversationPane.setFitToWidth(true);
        conversationPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationPane.getStyleClass().add("conversation-pane");

        commandBox = new TextField();
        commandBox.setPromptText("Ask Computah to add, find, or update something...");
        commandBox.setOnAction(event -> handleUserCommand());
        commandBox.getStyleClass().add("command-box");

        sendButton = new Button("Send");
        sendButton.setOnAction(event -> handleUserCommand());
        sendButton.getStyleClass().add("send-button");

        HBox inputArea = new HBox(10, commandBox, sendButton);
        inputArea.setAlignment(Pos.CENTER);
        inputArea.getStyleClass().add("input-area");
        HBox.setHgrow(commandBox, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setTop(createHeader());
        root.setCenter(conversationPane);
        root.setBottom(inputArea);

        loadData();
        appendAppMessage("Hello! I'm Computah.\nWhat can I do for you?");

        stage.setTitle("Computah");
        Scene scene = new Scene(root, 720, 560);
        scene.getStylesheets().add(getClass().getResource("/computah/main.css").toExternalForm());
        stage.setMinWidth(520);
        stage.setMinHeight(420);
        stage.setScene(scene);
        stage.show();
        commandBox.requestFocus();
    }

    private GridPane createHeader() {
        Label appMark = new Label("C");
        appMark.getStyleClass().add("app-mark");

        Label title = new Label("Computah");
        title.getStyleClass().add("app-title");

        Label description = new Label("TASK & CLIENT ASSISTANT");
        description.getStyleClass().add("app-description");

        VBox identity = new VBox(1, title, description);

        Label status = new Label("READY");
        status.getStyleClass().add("status-label");

        GridPane header = new GridPane();
        header.getStyleClass().add("header");
        header.setHgap(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.add(appMark, 0, 0);
        header.add(identity, 1, 0);
        header.add(status, 2, 0);
        GridPane.setHalignment(status, HPos.RIGHT);

        ColumnConstraints markColumn = new ColumnConstraints();
        ColumnConstraints identityColumn = new ColumnConstraints();
        identityColumn.setHgrow(Priority.ALWAYS);
        ColumnConstraints statusColumn = new ColumnConstraints();
        header.getColumnConstraints().addAll(markColumn, identityColumn, statusColumn);
        return header;
    }

    private void loadData() {
        try {
            model.getTasks().addAll(storage.loadTasks());
        } catch (ComputahException e) {
            appendAppMessage("OOPS!!! " + e.getMessage());
        }
        try {
            model.getClients().addAll(storage.loadClients());
        } catch (ComputahException e) {
            appendAppMessage("OOPS!!! " + e.getMessage());
        }
    }

    private void handleUserCommand() {
        String input = commandBox.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        commandBox.clear();
        appendCommand(input);

        try {
            Command command = Parser.parse(input, model.getTaskCount(), model.getClientCount());
            String response = executeCommand(command);
            appendResponse(response);
            if (command.isExit()) {
                commandBox.setDisable(true);
                sendButton.setDisable(true);
            }
        } catch (ComputahException e) {
            appendAppMessage("OOPS!!! " + e.getMessage());
        }
    }

    private String executeCommand(Command command) throws ComputahException {
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(response, true, StandardCharsets.UTF_8);
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), output);

        command.execute(model, ui, storage);
        output.flush();
        return response.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    private void appendCommand(String input) {
        Label message = new Label(input);
        message.setWrapText(true);
        message.setMaxWidth(420);
        message.getStyleClass().add("user-message");

        HBox row = new HBox(message);
        row.setAlignment(Pos.CENTER_RIGHT);
        row.getStyleClass().add("user-row");
        conversation.getChildren().add(row);
        scrollToLatestMessage();
    }

    private void appendResponse(String response) {
        String displayResponse = response.lines()
                .filter(line -> !line.equals(CONSOLE_DIVIDER))
                .collect(Collectors.joining("\n"));
        appendAppMessage(displayResponse);
    }

    private void appendAppMessage(String message) {
        Label source = new Label("COMPUTAH");
        source.getStyleClass().add("message-source");

        Label content = new Label(message);
        content.setWrapText(true);
        content.setMaxWidth(Double.MAX_VALUE);
        content.getStyleClass().add("app-message");

        VBox response = new VBox(5, source, content);
        response.setMaxWidth(Double.MAX_VALUE);
        response.getStyleClass().add("app-response");

        HBox row = new HBox(response);
        row.getStyleClass().add("app-row");
        HBox.setHgrow(response, Priority.ALWAYS);
        conversation.getChildren().add(row);
        scrollToLatestMessage();
    }

    private void scrollToLatestMessage() {
        Platform.runLater(() -> conversationPane.setVvalue(1.0));
    }
}
