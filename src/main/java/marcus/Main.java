package marcus;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Displays Marcus's JavaFX task-management interface.
 */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "data/results.txt";
    private static final int CONVERSATION_GAP = 8;
    private static final int COMMAND_BAR_GAP = 10;
    private static final int CONTENT_PADDING = 16;
    private static final int MESSAGE_PADDING = 12;
    private static final int MESSAGE_MAX_WIDTH = 440;
    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 480;
    private static final int AVATAR_RADIUS = 16;
    private static final String USER_BUBBLE_STYLE = "-fx-background-color: #E0E7FF;"
            + " -fx-background-radius: 16 5 16 16; -fx-padding: 10 14;"
            + " -fx-text-fill: #312E81; -fx-font-size: 13px;";
    private static final String MARCUS_BUBBLE_STYLE = "-fx-background-color: #ECFDF5;"
            + " -fx-background-radius: 5 16 16 16; -fx-padding: 11 14;"
            + " -fx-text-fill: #134E4A; -fx-font-size: 13px;";
    private static final String ERROR_BUBBLE_STYLE = "-fx-background-color: #FEE2E2;"
            + " -fx-background-radius: 5 16 16 16; -fx-padding: 11 14;"
            + " -fx-text-fill: #991B1B; -fx-font-size: 13px;";
    private static final String ROOT_STYLE = "-fx-background-color: #F8FAFC;";
    private static final String HEADER_STYLE = "-fx-background-color: #EEF2FF;"
            + " -fx-border-color: #DDE4FF; -fx-border-width: 0 0 1 0;";
    private static final String INPUT_STYLE = "-fx-background-color: #FFFFFF;"
            + " -fx-border-color: #D1D5DB; -fx-border-radius: 9; -fx-background-radius: 9;"
            + " -fx-padding: 9 12; -fx-font-size: 13px;";
    private static final String SEND_BUTTON_STYLE = "-fx-background-color: #0F766E;"
            + " -fx-text-fill: white; -fx-background-radius: 9; -fx-padding: 9 18;"
            + " -fx-font-weight: bold;";
    private static final String TASK_TEXT_STYLE = "-fx-text-fill: #134E4A; -fx-font-size: 13px;";
    private static final String TAG_TEXT_STYLE = "-fx-fill: #7C3AED; -fx-font-style: italic;"
            + " -fx-font-weight: bold;";
    private static final Pattern TAG_PATTERN = Pattern.compile("#[A-Za-z0-9_-]+");
    private VBox conversation;
    private ScrollPane conversationScroll;
    private TextField commandInput;
    private Marcus marcus;

    /**
     * Creates the JavaFX application.
     */
    public Main() {

    }

    @Override
    public void start(Stage stage) {
        marcus = new Marcus(DEFAULT_FILE_PATH);
        marcus.startSession();

        conversation = new VBox(CONVERSATION_GAP);
        conversation.setPadding(new Insets(MESSAGE_PADDING, 10, MESSAGE_PADDING, 10));
        conversation.setFillWidth(true);

        conversationScroll = new ScrollPane(conversation);
        conversationScroll.setFitToWidth(true);
        conversationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        conversationScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(conversationScroll, Priority.ALWAYS);

        appendMarcusMessage("Hey! I'm Marcus, your calm task companion.\nWhat shall we tackle?");

        Label prompt = new Label("✦  Ask Marcus");
        prompt.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px; -fx-font-weight: bold;");
        commandInput = new TextField();
        commandInput.setPromptText("e.g. list, todo read a book, or bye");
        commandInput.setStyle(INPUT_STYLE);
        commandInput.setOnAction(event -> submitCommand());

        Button sendButton = new Button("Send  ➤");
        sendButton.setDefaultButton(true);
        sendButton.setStyle(SEND_BUTTON_STYLE);
        sendButton.setOnAction(event -> submitCommand());

        HBox commandBar = new HBox(COMMAND_BAR_GAP, prompt, commandInput, sendButton);
        commandBar.setAlignment(Pos.CENTER_LEFT);
        commandBar.setPadding(new Insets(10, 12, 12, 12));
        HBox.setHgrow(commandInput, Priority.ALWAYS);

        Label title = new Label("Marcus");
        title.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label subtitle = new Label("Your calm task companion  •  ready when you are");
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
        VBox headerText = new VBox(2, title, subtitle);
        HBox header = new HBox(10, createMarcusAvatar(), headerText);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, CONTENT_PADDING, 11, CONTENT_PADDING));
        header.setStyle(HEADER_STYLE);

        VBox content = new VBox(0, header, conversationScroll, commandBar);
        content.setStyle(ROOT_STYLE);

        BorderPane root = new BorderPane(content);
        root.setStyle(ROOT_STYLE);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Marcus Chatbot");
        stage.setResizable(true);
        stage.setMinWidth(420);
        stage.setMinHeight(360);
        stage.show();
    }

    private void submitCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        appendUserMessage(command);
        commandInput.clear();
        boolean shouldContinue = marcus.processCommand(command, new GuiResponseHandler());
        if (!shouldContinue) {
            commandInput.setDisable(true);
        }
    }

    private void appendUserMessage(String message) {
        appendMessage(message, USER_BUBBLE_STYLE, Pos.BOTTOM_RIGHT, true);
    }

    private void appendMarcusMessage(String message) {
        appendMessage(message, MARCUS_BUBBLE_STYLE, Pos.BOTTOM_LEFT, false);
    }

    private void appendErrorMessage(String message) {
        appendMessage(message, ERROR_BUBBLE_STYLE, Pos.BOTTOM_LEFT, false);
    }

    private void appendMessage(String message, String bubbleStyle, Pos alignment,
            boolean isUserMessage) {
        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMinWidth(0);
        bindResponsiveWidth(bubble, isUserMessage);
        bubble.setStyle(bubbleStyle);

        HBox messageRow = isUserMessage
                ? new HBox(8, bubble, createUserAvatar())
                : new HBox(8, createMarcusAvatar(), bubble);
        messageRow.setMaxWidth(Double.MAX_VALUE);
        messageRow.setAlignment(alignment);
        conversation.getChildren().add(messageRow);
        Platform.runLater(() -> conversationScroll.setVvalue(1.0));
    }

    private void appendTaskListMessage(String taskList) {
        VBox bubble = new VBox(6);
        bubble.setMinWidth(0);
        bindResponsiveWidth(bubble, false);
        bubble.setStyle(MARCUS_BUBBLE_STYLE);

        for (String line : taskList.split("\\n", -1)) {
            if (line.isBlank()) {
                continue;
            }
            bubble.getChildren().add(createTaskListLine(line));
        }

        HBox messageRow = new HBox(8, createMarcusAvatar(), bubble);
        messageRow.setMaxWidth(Double.MAX_VALUE);
        messageRow.setAlignment(Pos.BOTTOM_LEFT);
        conversation.getChildren().add(messageRow);
        Platform.runLater(() -> conversationScroll.setVvalue(1.0));
    }

    private Region createTaskListLine(String line) {
        String trimmedLine = line.trim();
        if (!trimmedLine.matches("\\d+\\.\\[[TDE]\\]\\[[ X]\\].*")) {
            Label text = new Label(trimmedLine);
            text.setWrapText(true);
            text.setStyle(TASK_TEXT_STYLE);
            return text;
        }

        String taskNumber = trimmedLine.substring(0, trimmedLine.indexOf('.')) + ".";
        String taskStatus = trimmedLine.substring(trimmedLine.indexOf('.') + 1, 9);
        String taskDescription = trimmedLine.substring(9).trim();
        Label number = new Label(taskNumber);
        number.setMinWidth(28);
        number.setStyle(TASK_TEXT_STYLE);
        Label status = new Label(taskStatus);
        status.setMinWidth(42);
        status.setStyle(TASK_TEXT_STYLE);
        TextFlow description = createStyledTaskDescription(taskDescription);
        description.setMinWidth(0);
        description.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(description, Priority.ALWAYS);
        return new HBox(4, number, status, description);
    }

    private TextFlow createStyledTaskDescription(String description) {
        TextFlow textFlow = new TextFlow();
        Matcher matcher = TAG_PATTERN.matcher(description);
        int textStart = 0;
        while (matcher.find()) {
            addTaskText(textFlow, description.substring(textStart, matcher.start()), TASK_TEXT_STYLE);
            addTaskText(textFlow, matcher.group(), TAG_TEXT_STYLE);
            textStart = matcher.end();
        }
        addTaskText(textFlow, description.substring(textStart), TASK_TEXT_STYLE);
        return textFlow;
    }

    private void addTaskText(TextFlow textFlow, String content, String style) {
        if (!content.isEmpty()) {
            Text text = new Text(content);
            text.setStyle(style);
            textFlow.getChildren().add(text);
        }
    }

    private void bindResponsiveWidth(Region bubble, boolean isUserMessage) {
        conversation.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double availableWidth = Math.max(200, newWidth.doubleValue() - 20);
            double preferredWidth = availableWidth * (isUserMessage ? 0.82 : 0.92);
            bubble.setMaxWidth(Math.min(MESSAGE_MAX_WIDTH, preferredWidth));
        });
        double availableWidth = Math.max(200, conversation.getWidth() - 20);
        double preferredWidth = availableWidth * (isUserMessage ? 0.82 : 0.92);
        bubble.setMaxWidth(Math.min(MESSAGE_MAX_WIDTH, preferredWidth));
    }

    private StackPane createMarcusAvatar() {
        Circle avatarBackground = new Circle(AVATAR_RADIUS, Color.web("#5B6EE1"));
        Label robotIcon = new Label("🤖");
        robotIcon.setStyle("-fx-font-size: 14px;");
        return new StackPane(avatarBackground, robotIcon);
    }

    private StackPane createUserAvatar() {
        URL imageResource = getClass().getResource("/user-avatar.png");
        if (imageResource != null) {
            ImageView userImage = new ImageView(new Image(imageResource.toExternalForm()));
            userImage.setFitWidth(AVATAR_RADIUS * 2);
            userImage.setFitHeight(AVATAR_RADIUS * 2);
            userImage.setPreserveRatio(false);
            userImage.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
            return new StackPane(userImage);
        }

        Circle avatarBackground = new Circle(AVATAR_RADIUS, Color.web("#8B5CF6"));
        Label userIcon = new Label("🙂");
        userIcon.setStyle("-fx-font-size: 14px;");
        return new StackPane(avatarBackground, userIcon);
    }

    private class GuiResponseHandler implements ResponseHandler {
        @Override
        public void showMessage(String message) {
            appendMarcusMessage(message);
        }

        @Override
        public void showError(String message) {
            appendErrorMessage(message);
        }

        @Override
        public void showTaskList(String taskList) {
            appendTaskListMessage(taskList);
        }

        @Override
        public void showGoodbye() {
            appendMarcusMessage("See you later! Your tasks will be here when you're ready.");
        }
    }
}
