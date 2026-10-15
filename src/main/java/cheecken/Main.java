package cheecken;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Builds a small chat window and forwards commands to the independent chatbot.
 */
public class Main extends Application {
    private final Cheecken chatbot;
    private final VBox conversation = new VBox(14);
    private final TextField input = new TextField();
    private final Button send = new Button("Send");
    private final Label status = new Label("Enter to send • Tasks saved on this device");

    /**
     * Creates the normal desktop application.
     */
    public Main() {
        this(new Cheecken());
    }

    /**
     * Supplies an isolated chatbot for GUI acceptance tests.
     * @param chatbot chatbot session used by the window
     */
    Main(Cheecken chatbot) {
        this.chatbot = chatbot;
    }

    /**
     * Creates the conversation, command guide, and input controls.
     * @param stage application window
     */
    @Override
    public void start(Stage stage) {
        VBox header = createHeader();
        ScrollPane scroll = createConversationPane();
        VBox footer = createFooter(stage);
        BorderPane root = new BorderPane(scroll, header, null, footer, null);
        Scene scene = new Scene(root, 620, 720);
        // beautiful green style
        scene.getStylesheets().add(getClass().getResource("/styles/chat.css").toExternalForm());
        stage.setTitle("Cheecken — your task companion");
        stage.setMinWidth(420); // window is resizable by default
        stage.setMinHeight(480);
        stage.setScene(scene);
        showGreeting();
        stage.show();
        Platform.runLater(input::requestFocus);
    }

    /**
     * Creates the application heading and expandable command guide.
     * @return header containing the title, subtitle, and command guide
     */
    private VBox createHeader() {
        Label title = new Label("Cheecken");
        title.getStyleClass().add("title");
        Label subtitle = new Label("A little help with your everyday tasks.");
        subtitle.getStyleClass().add("muted");
        VBox header = new VBox(5, title, subtitle, createGuide());
        header.getStyleClass().add("header");
        return header;
    }

    /**
     * Creates a conversation viewport that follows newly added messages.
     * @return scrollable conversation viewport
     */
    private ScrollPane createConversationPane() {
        conversation.setPadding(new Insets(20));
        ScrollPane scroll = new ScrollPane(conversation);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversation.heightProperty().addListener((observable, oldHeight, newHeight) -> scroll.setVvalue(1));
        return scroll;
    }

    /**
     * Creates the input controls and connects keyboard and button submission.
     * @param stage application window
     * @return footer containing command input and status controls
     */
    private VBox createFooter(Stage stage) {
        input.setId("commandInput");
        input.setPromptText("Try: todo read a book");
        input.setAccessibleText("Task command");
        input.setOnAction(event -> handleUserInput(stage));
        send.setId("sendButton");
        send.setOnAction(event -> handleUserInput(stage));
        send.disableProperty().bind(input.textProperty().isEmpty().or(input.disabledProperty()));
        HBox composer = new HBox(10, input, send);
        HBox.setHgrow(input, Priority.ALWAYS);
        status.getStyleClass().add("muted");
        status.setWrapText(true);
        VBox footer = new VBox(8, composer, status);
        footer.getStyleClass().add("footer");
        return footer;
    }

    /**
     * Introduces the chatbot and displays any warnings from loading saved tasks.
     */
    private void showGreeting() {
        addMessage("Cheecken", "Hello! What can I help you remember?\n"
                + "Try todo read a book, or list to see your saved tasks.", false);
        String warning = chatbot.initialize();
        if (!warning.isBlank()) {
            addMessage("Cheecken", warning, false, chatbot.hasResponseError());
        }
    }

    /**
     * Displays command examples without requiring a separate help window.
     * @return collapsible command guide
     */
    private TitledPane createGuide() {
        Label examples = new Label("todo read a book\n"
                + "deadline submit report /by 15/10/2026 1800\n"
                + "event meeting /from 15/10/2026 0900 /to 15/10/2026 1000\n"
                + "list • find book • mark 1 • unmark 1 • delete 1 • bye\n"
                + "Dates: d/M/yyyy or ISO yyyy-MM-dd; optional HHmm (ISO: T18:00).\n"
                + "Natural dates: today, tomorrow, now, Mon–Sun or full weekday names.\n"
                + "Weekdays mean the strictly next occurrence, even on the same weekday.\n"
                + "Try: deadline report /by tomorrow 1800\n"
                + "Use HHmm after a date word; now takes no time suffix. Dates use local time.");
        examples.setWrapText(true);
        examples.setMaxWidth(Double.MAX_VALUE);
        TitledPane guide = new TitledPane("Command guide", examples);
        guide.setExpanded(false);
        guide.setAnimated(false);
        return guide;
    }

    /**
     * Sends a nonblank command once and closes the window after bye.
     * @param stage application window
     */
    private void handleUserInput(Stage stage) {
        String command = input.getText().strip();
        if (command.isEmpty() || chatbot.isFinished()) {
            return;
        }
        addMessage("You", command, true);
        String response = chatbot.getResponse(command);
        addMessage("Cheecken", response, false, chatbot.hasResponseError());
        input.clear();
        if (chatbot.isFinished()) {
            stage.close();
        } else {
            input.requestFocus();
        }
    }

    /**
     * Adds a wrapping message with a visible speaker label and distinct alignment.
     * @param speaker name displayed above the message
     * @param text message text to display
     * @param isUser true for a user message; false for a chatbot reply
     */
    private void addMessage(String speaker, String text, boolean isUser) {
        addMessage(speaker, text, isUser, false);
    }

    /**
     * Adds a message and styles error text separately from ordinary replies.
     * @param speaker name displayed above the message
     * @param text message text to display
     * @param isUser true for a user message; false for a chatbot reply
     * @param isError true to apply error styling to the message
     */
    private void addMessage(String speaker, String text, boolean isUser, boolean isError) {
        Label name = new Label(speaker);
        name.getStyleClass().add("speaker");
        Label message = new Label(text);
        if (isError) {
            message.getStyleClass().add("error-message");
        }
        message.setWrapText(true);
        message.setMinWidth(0);
        message.setMaxWidth(Double.MAX_VALUE);
        VBox bubble = new VBox(5, name, message);
        bubble.getStyleClass().add(isUser ? "user-message" : "bot-message");
        bubble.maxWidthProperty().bind(conversation.widthProperty().subtract(65));
        HBox row = new HBox(bubble);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        conversation.getChildren().add(row);
    }
}
