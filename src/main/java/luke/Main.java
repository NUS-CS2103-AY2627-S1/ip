package luke;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A simple JavaFX interface for chatting with Luke.
 */
public class Main extends Application {
    private static final int WINDOW_WIDTH = 980;
    private static final int WINDOW_HEIGHT = 680;
    private static final int MESSAGE_WIDTH = 460;

    private static final String FONT = "'Trebuchet MS', 'Verdana', sans-serif";
    private static final String BACKGROUND = "#e8faf4";
    private static final String PAPER = "#fffaf0";
    private static final String MINT = "#d8f5ee";
    private static final String TEAL = "#51bdb8";
    private static final String TEAL_DARK = "#1f6f6b";
    private static final String INK = "#18232f";
    private static final String MUTED = "#64736f";
    private static final String LUKE_PROFILE_IMAGE = "/images/luke-profile.png";
    private static final String STYLESHEET = "/styles/main.css";

    private final Luke luke = new Luke();
    private final Image lukeProfileImage = new Image(getClass().getResourceAsStream(LUKE_PROFILE_IMAGE));
    private final VBox dialogContainer = new VBox(14);
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("^");
    private final Label taskCount = new Label();

    /**
     * Builds and displays the main chat window.
     *
     * @param stage the primary JavaFX window
     */
    @Override
    public void start(Stage stage) {
        HBox appShell = new HBox(12);
        appShell.setPadding(new Insets(12));
        appShell.setStyle("-fx-background-color: " + BACKGROUND + ";");

        appShell.getChildren().addAll(createSidebar(), createChatPanel());
        HBox.setHgrow(appShell.getChildren().get(1), Priority.ALWAYS);

        updateTaskCount();

        stage.setTitle("Luke");
        stage.setMinWidth(720);
        stage.setMinHeight(580);
        Scene scene = new Scene(appShell, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates the left project-style navigation column.
     *
     * @return the sidebar
     */
    private VBox createSidebar() {
        Region avatar = createLukeProfilePicture(58);

        Label title = new Label("Luke Chatbot");
        title.setStyle(labelStyle(20, "bold", INK));
        Label subtitle = new Label("Local tasks - JavaFX");
        subtitle.setStyle(labelStyle(12, "normal", MUTED));

        VBox sidebar = new VBox(12, avatar, title, subtitle,
                createTinyHeading("COMMANDS & USAGE"), createCommandList(createCommands()),
                createTaskPanel());
        sidebar.setPrefWidth(260);
        sidebar.setMinWidth(230);
        sidebar.setStyle(shellPanelStyle());
        return sidebar;
    }

    /**
     * Creates the command-reference rows displayed in the sidebar.
     *
     * @return the command rows
     */
    private VBox createCommands() {
        return new VBox(8,
                createCommandChip("todo", "todo DESCRIPTION"),
                createCommandChip("deadline", "deadline DESCRIPTION /by TIME"),
                createCommandChip("event", "event DESCRIPTION /from START /to END"),
                createCommandChip("list", "list [/sort alpha|time]"),
                createCommandChip("find", "find SEARCH_TEXT"),
                createCommandChip("mark", "mark INDEX"),
                createCommandChip("unmark", "unmark INDEX"),
                createCommandChip("delete", "delete INDEX"),
                createCommandChip("bye", "bye"));
    }

    /**
     * Creates the sidebar panel that shows the number of stored tasks.
     *
     * @return the task-count panel
     */
    private VBox createTaskPanel() {
        taskCount.setStyle(labelStyle(24, "bold", INK));
        Label taskCaption = new Label("tasks stored");
        taskCaption.setStyle(labelStyle(12, "normal", MUTED));
        VBox taskPanel = new VBox(3, createTinyHeading("TASKS"), taskCount, taskCaption);
        taskPanel.setAlignment(Pos.CENTER_LEFT);
        taskPanel.setStyle("-fx-background-color: rgba(255, 250, 240, 0.8); "
                + "-fx-border-color: " + TEAL + "; -fx-border-radius: 7px; "
                + "-fx-background-radius: 7px; -fx-padding: 10px;");
        return taskPanel;
    }

    /**
     * Creates a scrollable command list for the sidebar.
     *
     * @param commands command rows to show
     * @return the scrollable command list
     */
    private ScrollPane createCommandList(VBox commands) {
        commands.setPadding(new Insets(0, 6, 0, 0));

        ScrollPane commandList = new ScrollPane(commands);
        commandList.setFitToWidth(true);
        commandList.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        commandList.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        commandList.setStyle("-fx-background: transparent; -fx-background-color: transparent; "
                + "-fx-border-color: " + TEAL + "; -fx-border-radius: 7px; "
                + "-fx-background-radius: 7px; -fx-padding: 6px;");
        VBox.setVgrow(commandList, Priority.ALWAYS);
        return commandList;
    }

    /**
     * Creates the central chat workspace.
     *
     * @return the central chat panel
     */
    private BorderPane createChatPanel() {
        BorderPane chatPanel = new BorderPane();
        chatPanel.setStyle("-fx-background-color: " + PAPER + "; -fx-border-color: #e8cfa2; "
                + "-fx-border-width: 2px; -fx-background-radius: 8px; -fx-border-radius: 8px;");

        chatPanel.setTop(createChatHeader());

        dialogContainer.setPadding(new Insets(18, 20, 18, 20));
        dialogContainer.getChildren().add(createBotDialog(luke.getWelcomeMessage()));

        ScrollPane scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            scrollPane.setVvalue(1.0);
        });
        chatPanel.setCenter(scrollPane);
        chatPanel.setBottom(createInputArea());

        return chatPanel;
    }

    /**
     * Creates the central panel header.
     *
     * @return the header row
     */
    private HBox createChatHeader() {
        VBox heading = new VBox(3);
        Label eyebrow = createTinyHeading("PLAYGROUND");
        Label title = new Label("Build your task list with Luke");
        title.setStyle(labelStyle(18, "bold", INK));
        heading.getChildren().addAll(eyebrow, title);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label ready = createBadge("READY", MINT, TEAL_DARK);
        Label local = createBadge("LOCAL", "#f8edc3", "#785f15");
        HBox header = new HBox(10, heading, spacer, ready, local);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 20, 14, 20));
        header.setStyle("-fx-border-color: #eeddb8; -fx-border-width: 0 0 2px 0;");
        return header;
    }

    /**
     * Creates the text input row at the bottom of the chat window.
     *
     * @return the input row
     */
    private HBox createInputArea() {
        userInput.setPromptText("Enter a command, e.g. todo read book");
        userInput.setStyle("-fx-background-color: white; -fx-background-radius: 6px; "
                + "-fx-border-color: #e8cfa2; -fx-border-radius: 6px; -fx-padding: 12px; "
                + "-fx-font-family: " + FONT + "; -fx-font-size: 13px;");
        HBox.setHgrow(userInput, Priority.ALWAYS);

        sendButton.setDefaultButton(true);
        sendButton.setMinSize(46, 46);
        sendButton.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: #083f3c; "
                + "-fx-font-family: " + FONT + "; -fx-font-size: 22px; -fx-font-weight: bold; "
                + "-fx-background-radius: 6px; -fx-border-color: " + TEAL_DARK + "; "
                + "-fx-border-width: 2px; -fx-border-radius: 6px;");

        HBox inputArea = new HBox(10, createLukeProfilePicture(42), userInput, sendButton);
        inputArea.setPadding(new Insets(14, 18, 18, 18));
        inputArea.setAlignment(Pos.CENTER);
        inputArea.setStyle("-fx-background-color: #fff4df; -fx-border-color: #eeddb8; "
                + "-fx-border-width: 2px 0 0 0;");

        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());
        return inputArea;
    }

    /**
     * Sends the current user input to Luke and displays both sides of the exchange.
     */
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(createUserDialog(input));
        userInput.clear();

        String response = luke.getResponse(input);
        if (!response.isEmpty()) {
            dialogContainer.getChildren().add(createBotDialog(response));
        }

        updateTaskCount();

        if (luke.shouldExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            Platform.runLater(() -> userInput.getScene().getWindow().hide());
        }
    }

    /**
     * Updates the task count shown in the status panel.
     */
    private void updateTaskCount() {
        taskCount.setText(String.valueOf(luke.getItems().size()));
    }

    /**
     * Creates a right-aligned message bubble for user text.
     *
     * @param text message text
     * @return a row containing the message bubble
     */
    private HBox createUserDialog(String text) {
        Label message = createMessageLabel(text);
        message.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: #083f3c; "
                + "-fx-background-radius: 7px; -fx-padding: 10px 12px; "
                + "-fx-border-color: " + TEAL_DARK + "; -fx-border-radius: 7px; "
                + "-fx-border-width: 2px;");

        HBox row = new HBox(8, message, createMiniAvatar("YOU", "#d7eefb", "#2f6f98"));
        row.setAlignment(Pos.CENTER_RIGHT);
        return row;
    }

    /**
     * Creates a left-aligned message bubble for Luke's reply.
     *
     * @param text message text
     * @return a row containing the message bubble
     */
    private HBox createBotDialog(String text) {
        Label message = createMessageLabel(text);
        message.setStyle("-fx-background-color: white; -fx-text-fill: " + INK + "; "
                + "-fx-background-radius: 7px; -fx-padding: 10px 12px; "
                + "-fx-border-color: #e8cfa2; -fx-border-radius: 7px; -fx-border-width: 2px;");

        HBox row = new HBox(8, createLukeProfilePicture(42), message);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Creates a wrapping label suitable for a chat message.
     *
     * @param text message text
     * @return the configured label
     */
    private Label createMessageLabel(String text) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(MESSAGE_WIDTH);
        message.setStyle(labelStyle(13, "normal", INK));
        return message;
    }

    /**
     * Creates a small square avatar label.
     *
     * @param text avatar text
     * @param background background color
     * @param foreground text color
     * @return the avatar label
     */
    private Label createMiniAvatar(String text, String background, String foreground) {
        Label avatar = new Label(text);
        avatar.setAlignment(Pos.CENTER);
        avatar.setMinSize(40, 40);
        avatar.setMaxSize(40, 40);
        avatar.setStyle("-fx-background-color: " + background + "; -fx-text-fill: " + foreground + "; "
                + "-fx-border-color: " + foreground + "; -fx-border-width: 2px; "
                + "-fx-background-radius: 6px; -fx-border-radius: 6px; "
                + "-fx-font-family: " + FONT + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        return avatar;
    }

    /**
     * Creates Luke's image-backed profile picture.
     *
     * @param size width and height of the avatar
     * @return the profile picture container
     */
    private Region createLukeProfilePicture(int size) {
        ImageView imageView = new ImageView(lukeProfileImage);
        imageView.setFitWidth(size - 6);
        imageView.setFitHeight(size - 6);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);

        HBox frame = new HBox(imageView);
        frame.setAlignment(Pos.CENTER);
        frame.setMinSize(size, size);
        frame.setMaxSize(size, size);
        frame.setStyle("-fx-background-color: " + MINT + "; -fx-border-color: " + TEAL_DARK + "; "
                + "-fx-border-width: 2px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
        return frame;
    }

    /**
     * Creates a badge-style label.
     *
     * @param text badge text
     * @param background badge background
     * @param foreground badge text color
     * @return the badge label
     */
    private Label createBadge(String text, String background, String foreground) {
        Label badge = new Label(text);
        badge.setStyle("-fx-background-color: " + background + "; -fx-text-fill: " + foreground + "; "
                + "-fx-border-color: " + foreground + "; -fx-border-radius: 4px; "
                + "-fx-background-radius: 4px; -fx-padding: 5px 10px; "
                + "-fx-font-family: " + FONT + "; -fx-font-size: 11px; -fx-font-weight: bold;");
        return badge;
    }

    /**
     * Creates a compact command reference row.
     *
     * @param command command keyword
     * @param usage complete command syntax
     * @return the command row
     */
    private VBox createCommandChip(String command, String usage) {
        Label keyword = createBadge(command, MINT, TEAL_DARK);
        Label usageText = new Label(usage);
        usageText.setWrapText(true);
        usageText.setMaxWidth(Double.MAX_VALUE);
        usageText.setStyle(labelStyle(11, "normal", MUTED)
                + "-fx-font-family: monospace;");

        VBox row = new VBox(4, keyword, usageText);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: rgba(255, 250, 240, 0.75); -fx-padding: 7px; "
                + "-fx-background-radius: 5px;");
        return row;
    }

    /**
     * Creates a small section heading.
     *
     * @param text heading text
     * @return the heading label
     */
    private Label createTinyHeading(String text) {
        Label heading = new Label(text);
        heading.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 11px; "
                + "-fx-font-weight: bold; -fx-text-fill: " + TEAL_DARK + ";");
        return heading;
    }

    /**
     * Returns the shared outer panel style.
     *
     * @return JavaFX CSS for side panels
     */
    private String shellPanelStyle() {
        return "-fx-background-color: rgba(255, 250, 240, 0.72); "
                + "-fx-border-color: " + TEAL + "; -fx-border-width: 2px; "
                + "-fx-background-radius: 8px; -fx-border-radius: 8px; -fx-padding: 12px;";
    }

    /**
     * Creates a reusable label text style.
     *
     * @param size font size
     * @param weight font weight
     * @param color text color
     * @return JavaFX CSS text style
     */
    private String labelStyle(int size, String weight, String color) {
        return "-fx-font-family: " + FONT + "; -fx-font-size: " + size + "px; "
                + "-fx-font-weight: " + weight + "; -fx-text-fill: " + color + ";";
    }
}
