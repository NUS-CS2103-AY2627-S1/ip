package rudra;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rudra.command.Command;
import rudra.exception.RudraException;
import rudra.parser.Parser;
import rudra.storage.Storage;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * JavaFX interface for interacting with Rudra through the existing command language.
 */
public class RudraGui extends Application {
    private static final String DATA_FILE_PATH = "data/rudra.txt";

    private final ArrayList<Task> tasks = new ArrayList<>();
    private final ArrayList<String> pendingMessages = new ArrayList<>();
    private final VBox conversation = new VBox(10);

    private Ui ui;
    private Storage storage;
    private TextField commandField;
    private Button sendButton;
    private Label taskCountLabel;
    private ScrollPane conversationScrollPane;

    /**
     * Creates and shows Rudra's primary application window.
     *
     * @param stage JavaFX stage that hosts the interface.
     */
    @Override
    public void start(Stage stage) {
        this.ui = new Ui(this.pendingMessages::add);
        this.storage = new Storage(DATA_FILE_PATH);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-shell");
        root.setTop(createHeader());
        root.setCenter(createChatPane());

        Scene scene = new Scene(root, 800, 680);
        scene.getStylesheets().add(getClass().getResource("/rudra/ui/rudra.css").toExternalForm());
        stage.setTitle("Rudra // Y2K Task Sidekick");
        stage.setMinWidth(520);
        stage.setMinHeight(480);
        stage.setResizable(true);
        stage.setScene(scene);

        addMessage(createWelcomeMessage(), MessageType.BOT);
        loadTasks();
        stage.show();
        this.commandField.requestFocus();
    }

    private HBox createHeader() {
        Label brandMark = new Label("R");
        brandMark.getStyleClass().add("brand-mark");
        Label brand = new Label("RUDRA");
        brand.getStyleClass().add("brand");
        Label subtitle = new Label("YOUR Y2K TASK SIDEKICK");
        subtitle.getStyleClass().add("subtitle");
        VBox identity = new VBox(0, brand, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.taskCountLabel = new Label();
        this.taskCountLabel.getStyleClass().add("task-count");

        HBox header = new HBox(12, brandMark, identity, spacer, this.taskCountLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("app-header");
        return header;
    }

    private VBox createChatPane() {
        Label title = new Label("What's on your radar?");
        title.getStyleClass().add("conversation-title");
        Label hint = new Label("Rudra keeps every move saved automatically.");
        hint.getStyleClass().add("conversation-hint");

        VBox headingText = new VBox(2, title, hint);
        Region headingSpacer = new Region();
        HBox.setHgrow(headingSpacer, Priority.ALWAYS);
        Button showTasksButton = new Button("Show my list");
        showTasksButton.getStyleClass().add("secondary-button");
        showTasksButton.setOnAction(event -> submitCommand("list"));
        HBox heading = new HBox(12, headingText, headingSpacer, showTasksButton);
        heading.setAlignment(Pos.CENTER_LEFT);
        heading.getStyleClass().add("conversation-heading");

        TitledPane commandGuide = createCommandGuide();

        this.conversation.getStyleClass().add("conversation");
        this.conversationScrollPane = new ScrollPane(this.conversation);
        this.conversationScrollPane.setFitToWidth(true);
        this.conversationScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.conversationScrollPane.getStyleClass().add("chat-scroll");
        VBox.setVgrow(this.conversationScrollPane, Priority.ALWAYS);

        this.commandField = new TextField();
        this.commandField.setPromptText("Drop a command, e.g. todo read chapter 4");
        this.commandField.setAccessibleHelp("Enter a Rudra command");
        this.commandField.setOnAction(event -> submitCommand(this.commandField.getText()));
        HBox.setHgrow(this.commandField, Priority.ALWAYS);

        this.sendButton = new Button("Lock it in");
        this.sendButton.getStyleClass().add("send-button");
        this.sendButton.setDisable(true);
        this.commandField.textProperty().addListener((observable, oldValue, newValue) ->
                this.sendButton.setDisable(newValue.trim().isEmpty()));
        this.sendButton.setOnAction(event -> submitCommand(this.commandField.getText()));
        HBox composer = new HBox(10, this.commandField, this.sendButton);
        composer.setAlignment(Pos.CENTER);
        composer.getStyleClass().add("composer");

        VBox chatPane = new VBox(10, heading, commandGuide, this.conversationScrollPane, composer);
        chatPane.getStyleClass().add("chat-pane");
        return chatPane;
    }

    private TitledPane createCommandGuide() {
        Label guide = new Label("todo DESCRIPTION  •  deadline DESCRIPTION /by YYYY-MM-DD [HHmm]\n"
                + "event DESCRIPTION /from YYYY-MM-DD HHmm /to YYYY-MM-DD HHmm\n"
                + "list  •  find KEYWORD  •  sort deadline  •  mark/unmark/delete NUMBER  •  bye");
        guide.setWrapText(true);
        guide.getStyleClass().add("guide");

        TitledPane commandGuide = new TitledPane("Need the cheat codes?", guide);
        commandGuide.setExpanded(false);
        commandGuide.setAnimated(false);
        commandGuide.getStyleClass().add("command-guide");
        return commandGuide;
    }

    private void loadTasks() {
        try {
            Storage.LoadResult loadResult = this.storage.loadTasks();
            this.tasks.addAll(loadResult.getTasks());
            if (loadResult.getSkippedTaskCount() > 0) {
                this.ui.showCorruptedTaskWarning(loadResult.getSkippedTaskCount());
                showPendingReply(MessageType.WARNING);
            }
        } catch (RudraException e) {
            this.ui.showLoadingError(e.getMessage());
            showPendingReply(MessageType.ERROR);
        }
        updateTaskCount();
    }

    private void submitCommand(String rawCommand) {
        String command = rawCommand.trim();
        if (command.isEmpty()) {
            return;
        }

        addMessage(command, MessageType.USER);
        this.commandField.clear();
        this.pendingMessages.clear();

        try {
            Command parsedCommand = Parser.parse(command);
            parsedCommand.execute(this.tasks, this.ui, this.storage);
            showPendingReply(MessageType.BOT);
            if (parsedCommand.isExit()) {
                this.commandField.setDisable(true);
                this.sendButton.setDisable(true);
            }
        } catch (RudraException e) {
            this.ui.showError(e.getMessage());
            showPendingReply(MessageType.ERROR);
        }

        updateTaskCount();
    }

    private void showPendingReply(MessageType messageType) {
        if (this.pendingMessages.isEmpty()) {
            return;
        }
        addMessage(String.join(System.lineSeparator(), this.pendingMessages), messageType);
        this.pendingMessages.clear();
    }

    private void addMessage(String text, MessageType messageType) {
        HBox messageRow = new HBox();
        messageRow.getStyleClass().add("message-row");

        if (messageType == MessageType.USER) {
            Label message = createMessageLabel(text, "user-message");
            message.maxWidthProperty().bind(this.conversationScrollPane.widthProperty().multiply(0.72));
            messageRow.getChildren().add(message);
            messageRow.setAlignment(Pos.CENTER_RIGHT);
        } else {
            String styleClass = messageType == MessageType.ERROR ? "error-message"
                    : messageType == MessageType.WARNING ? "warning-message" : "bot-message";
            Label role = new Label(messageType.getHeading());
            role.getStyleClass().add("message-role");
            Label message = createMessageLabel(text, "response-text");
            VBox responseCard = new VBox(5, role, message);
            responseCard.getStyleClass().addAll("response-card", styleClass);
            responseCard.maxWidthProperty().bind(this.conversationScrollPane.widthProperty().subtract(34));
            responseCard.prefWidthProperty().bind(this.conversationScrollPane.widthProperty().subtract(34));
            messageRow.getChildren().add(responseCard);
            messageRow.setAlignment(Pos.CENTER_LEFT);
        }

        this.conversation.getChildren().add(messageRow);
        Platform.runLater(() -> {
            this.conversationScrollPane.applyCss();
            this.conversationScrollPane.layout();
            this.conversationScrollPane.setVvalue(1.0);
        });
    }

    private Label createMessageLabel(String text, String styleClass) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(Double.MAX_VALUE);
        message.getStyleClass().addAll("message", styleClass);
        return message;
    }

    private String createWelcomeMessage() {
        return "Yo! Rudra's online. What are we getting done today?\n"
                + "Try: deadline submit report /by 2026-09-10 - or open the cheat codes above.";
    }

    private void updateTaskCount() {
        this.taskCountLabel.setText(this.tasks.size() + (this.tasks.size() == 1 ? " task" : " tasks"));
    }

    private enum MessageType {
        BOT("RUDRA // ONLINE"),
        ERROR("WHOA // COMMAND ERROR"),
        USER("YOU"),
        WARNING("HEADS UP");

        private final String heading;

        MessageType(String heading) {
            this.heading = heading;
        }

        private String getHeading() {
            return this.heading;
        }
    }
}
