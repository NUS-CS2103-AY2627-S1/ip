package johnny;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import johnny.ui.Ui;

/**
 * Provides a fit-for-purpose JavaFX interface for Johnny.
 */
public class Main extends Application {

    private static final String DEFAULT_FILE_PATH = "./data/johnny.txt";
    private static final String JOHNNY_BADGE_STYLE = "-fx-background-color: #c88a2d; -fx-background-radius: 14; "
            + "-fx-font-weight: bold; -fx-text-fill: #2f2a24;";
    private static final String JOHNNY_MESSAGE_STYLE = "-fx-background-color: #e8dcc4; -fx-text-fill: #2f2a24; "
            + "-fx-background-radius: 8; -fx-padding: 8 10 8 10;";
    private static final String USER_MESSAGE_STYLE = "-fx-background-color: #264653; -fx-text-fill: white; "
            + "-fx-background-radius: 12; -fx-padding: 8 12 8 12;";

    private final StringBuilder responseBuffer = new StringBuilder();
    private Johnny johnny;
    private ScrollPane conversationArea;
    private VBox messagesPane;
    private TextField inputField;

    @Override
    public void start(Stage stage) {
        messagesPane = new VBox(8);
        messagesPane.setPadding(new Insets(4));
        messagesPane.setStyle("-fx-background-color: #f7f3e8;");
        conversationArea = new ScrollPane(messagesPane);
        conversationArea.setFitToWidth(true);
        conversationArea.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationArea.setAccessibleText("Conversation with Johnny");
        conversationArea.setStyle("-fx-background: #f7f3e8; -fx-border-color: transparent;");

        inputField = new TextField();
        inputField.setPromptText("Enter a command, e.g. todo read book");
        inputField.setAccessibleText("Command input");
        inputField.setStyle("-fx-background-color: white; -fx-border-color: #c88a2d; "
                + "-fx-border-radius: 6; -fx-background-radius: 6;");

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submit());
        sendButton.setStyle("-fx-background-color: #c88a2d; -fx-font-weight: bold; "
                + "-fx-text-fill: #2f2a24; -fx-background-radius: 6;");
        inputField.setOnAction(event -> submit());

        Label title = new Label("Johnny · At your service");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 18px; "
                + "-fx-font-weight: bold; -fx-text-fill: #2f2a24;");
        HBox header = new HBox(8, createJohnnyBadge(), title);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox inputBar = new HBox(8, inputField, sendButton);
        HBox.setHgrow(inputField, Priority.ALWAYS);
        VBox root = new VBox(10, header, conversationArea, inputBar);
        VBox.setVgrow(conversationArea, Priority.ALWAYS);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f7f3e8;");

        Ui guiUi = new Ui(this::collectJohnnyResponse);
        johnny = new Johnny(DEFAULT_FILE_PATH, guiUi);
        guiUi.showGreeting();
        showJohnnyResponse();

        stage.setTitle("Johnny");
        stage.setScene(new Scene(root, 640, 480));
        stage.setMinWidth(420);
        stage.setMinHeight(300);
        stage.show();
        inputField.requestFocus();
    }

    private void submit() {
        assert johnny != null : "Johnny must be initialized before command submission";

        String command = inputField.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        showUserMessage(command);
        boolean shouldExit = johnny.processCommand(command);
        showJohnnyResponse();
        inputField.clear();
        if (shouldExit) {
            Platform.exit();
        }
    }

    private void collectJohnnyResponse(String line) {
        if (!responseBuffer.isEmpty()) {
            responseBuffer.append(System.lineSeparator());
        }
        responseBuffer.append(line);
    }

    private void showJohnnyResponse() {
        if (responseBuffer.isEmpty()) {
            return;
        }

        Label message = createMessageLabel(responseBuffer.toString(), JOHNNY_MESSAGE_STYLE);
        message.setFont(Font.font("Monospaced"));
        message.setAccessibleText("Johnny: " + responseBuffer);
        message.maxWidthProperty().bind(messagesPane.widthProperty().multiply(0.88));
        addMessage(message, true);
        responseBuffer.setLength(0);
    }

    private void showUserMessage(String command) {
        Label message = createMessageLabel(command, USER_MESSAGE_STYLE);
        message.setAccessibleText("You: " + command);
        message.maxWidthProperty().bind(messagesPane.widthProperty().multiply(0.72));
        addMessage(message, false);
    }

    private Label createMessageLabel(String text, String style) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setStyle(style);
        return message;
    }

    private Label createJohnnyBadge() {
        Label badge = new Label("J");
        badge.setAlignment(Pos.CENTER);
        badge.setMinSize(28, 28);
        badge.setMaxSize(28, 28);
        badge.setStyle(JOHNNY_BADGE_STYLE);
        badge.setAccessibleText("Johnny");
        return badge;
    }

    private void addMessage(Label message, boolean isJohnny) {
        HBox row = isJohnny ? new HBox(8, createJohnnyBadge(), message) : new HBox(message);
        row.setAlignment(isJohnny ? Pos.TOP_LEFT : Pos.CENTER_RIGHT);
        messagesPane.getChildren().add(row);
        Platform.runLater(() -> conversationArea.setVvalue(1.0));
    }
}
