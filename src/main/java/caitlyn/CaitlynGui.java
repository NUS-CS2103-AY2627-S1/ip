package caitlyn;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Provides a resizable conversation with distinct command, response, and error cards.
 */
public final class CaitlynGui extends Application {
    private static final double WINDOW_WIDTH = 720;
    private static final double WINDOW_HEIGHT = 620;
    private static final double AVATAR_SIZE = 30;

    /** Accumulates the output of one command into a single readable reply. */
    private final StringBuilder pendingReply = new StringBuilder();

    private VBox conversation;
    private ScrollPane chatScroll;
    private TextField commandInput;
    private Button sendButton;
    private MenuButton commandsButton;
    private Label taskCountLabel;
    private TaskSession session;
    private Ui ui;
    private Image userImage;
    private Image caitlynImage;
    private boolean isSessionEnded;

    /**
     * Creates the chat window and displays the greeting and any startup error.
     *
     * @param stage the primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        session = new TaskSession();
        userImage = loadImage("user.jpg");
        caitlynImage = loadImage("caitlyn.png");
        conversation = new VBox(12);
        conversation.getStyleClass().add("conversation");
        chatScroll = new ScrollPane(conversation);
        chatScroll.setFitToWidth(true);
        chatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScroll.getStyleClass().add("chat-scroll");
        chatScroll.setAccessibleText("Conversation with Caitlyn");

        initializeInputControls();
        configureWindow(stage);
        ui = new Ui(this::collectReply, message -> appendMessage(message.strip(), false, true));
        ui.showWelcome();
        showPendingReply();
        if (session.hasLoadingError()) {
            ui.showLoadingError();
        }
        commandInput.requestFocus();
    }

    /** Creates the command controls and descriptive, accessible input labels. */
    private void initializeInputControls() {
        commandInput = new TextField();
        commandInput.setId("command-input");
        commandInput.setPromptText("Type a command… e.g. todo read book");
        commandInput.setAccessibleText("Command");
        commandInput.setOnAction(event -> submitCommand());
        commandInput.setMinWidth(0);

        sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submitCommand());
        sendButton.getStyleClass().add("send-button");
        sendButton.disableProperty().bind(commandInput.disabledProperty()
                .or(commandInput.textProperty().isEmpty()));

        taskCountLabel = new Label();
        taskCountLabel.setId("task-count");
        taskCountLabel.getStyleClass().add("status");
        updateTaskCount();
    }

    /** Arranges fixed controls around a conversation that uses the remaining space. */
    private void configureWindow(Stage stage) {
        BorderPane root = new BorderPane(chatScroll);
        root.getStyleClass().add("chat-window");
        root.setTop(createHeader());
        root.setBottom(createComposer());
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("chat.css").toExternalForm());
        stage.setTitle("Caitlyn · Task assistant");
        stage.setMinWidth(380);
        stage.setMinHeight(360);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    /** Creates a compact header with command examples available on demand. */
    private HBox createHeader() {
        Label title = new Label("Caitlyn");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Your task assistant");
        subtitle.getStyleClass().add("subtitle");
        VBox identity = new VBox(2, title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        commandsButton = createCommandsMenu();
        HBox header = new HBox(10, createAvatar(false), identity, spacer, commandsButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        return header;
    }

    /** Displays syntax and examples, inserting an editable example only when selected. */
    private MenuButton createCommandsMenu() {
        MenuButton menu = new MenuButton("Commands");
        for (CommandExample command : CommandExample.values()) {
            Label template = new Label("Template: " + command.getTemplate());
            template.getStyleClass().add("command-template");
            template.setWrapText(true);
            template.setPrefWidth(306);
            template.setMinHeight(Region.USE_PREF_SIZE);
            Label example = new Label("Example: " + command.getExample());
            example.getStyleClass().add("command-example");
            example.setWrapText(true);
            example.setPrefWidth(306);
            example.setMinHeight(Region.USE_PREF_SIZE);
            VBox details = new VBox(3, template, example);
            details.getStyleClass().add("command-details");
            details.setPrefWidth(310);
            details.setMaxWidth(310);
            CustomMenuItem item = new CustomMenuItem(details, true);
            item.setText(command.getTemplate());
            item.setOnAction(event -> {
                commandInput.setText(command.getExample());
                // Return focus after the popup closes so typing replaces the selected value.
                Platform.runLater(() -> {
                    commandInput.requestFocus();
                    commandInput.selectRange(command.getSelectionStart(), command.getSelectionEnd());
                });
            });
            menu.getItems().add(item);
        }
        return menu;
    }

    /** Keeps input, submission, and session status visible while the history scrolls. */
    private VBox createComposer() {
        HBox inputRow = new HBox(8, commandInput, sendButton);
        inputRow.setAlignment(Pos.CENTER);
        HBox.setHgrow(commandInput, Priority.ALWAYS);
        Label hint = new Label("Enter to send");
        hint.getStyleClass().add("input-hint");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox statusRow = new HBox(8, taskCountLabel, spacer, hint);
        VBox composer = new VBox(8, inputRow, statusRow);
        composer.getStyleClass().add("composer");
        return composer;
    }

    /** Runs one command, grouping its reply and retaining invalid input for correction. */
    private void submitCommand() {
        String fullCommand = commandInput.getText().trim();
        if (isSessionEnded || fullCommand.isEmpty()) {
            return;
        }
        appendMessage(fullCommand, true, false);
        try {
            Command command = Parser.parse(fullCommand);
            session.execute(command, ui);
            showPendingReply();
            commandInput.clear();
            if (command.isExit()) {
                endSession();
            }
        } catch (CaitlynException exception) {
            showPendingReply();
            ui.showError(exception.getMessage());
            commandInput.selectAll();
        } finally {
            updateTaskCount();
            if (!isSessionEnded) {
                commandInput.requestFocus();
            }
        }
    }

    /** Removes console indentation while preserving multiline reply content. */
    private void collectReply(String message) {
        if (!pendingReply.isEmpty()) {
            pendingReply.append('\n');
        }
        pendingReply.append(message.strip());
    }

    /** Displays the completed reply once, so task lists share one card and avatar. */
    private void showPendingReply() {
        if (!pendingReply.isEmpty()) {
            appendMessage(pendingReply.toString(), false, false);
            pendingReply.setLength(0);
        }
    }

    /**
     * Adds a wrapping message with a role label and a copy action.
     * User commands occupy compact right-aligned bubbles; replies use the available width.
     */
    private void appendMessage(String message, boolean isUser, boolean isError) {
        Label role = new Label(isUser ? "YOU" : isError ? "CAITLYN · ERROR" : "CAITLYN");
        role.getStyleClass().add("message-role");
        Label body = new Label(message);
        body.setWrapText(true);
        body.setMinWidth(0);
        body.setMaxWidth(Double.MAX_VALUE);
        body.setMinHeight(Region.USE_PREF_SIZE);
        body.getStyleClass().add("message-body");
        body.setAccessibleText(role.getText() + ": " + message);
        MenuItem copy = new MenuItem("Copy message");
        copy.setOnAction(event -> {
            ClipboardContent clipboard = new ClipboardContent();
            clipboard.putString(message);
            Clipboard.getSystemClipboard().setContent(clipboard);
        });
        body.setContextMenu(new ContextMenu(copy));

        VBox card = new VBox(5, role, body);
        card.getStyleClass().addAll("message-card", isUser ? "user-card" : "assistant-card");
        if (isError) {
            card.getStyleClass().add("error-card");
        }
        card.setMinWidth(0);
        HBox row = new HBox(8);
        row.setMinHeight(Region.USE_PREF_SIZE);
        ImageView avatar = createAvatar(isUser);
        if (isUser) {
            card.maxWidthProperty().bind(conversation.widthProperty().subtract(32).multiply(0.8));
            row.setAlignment(Pos.TOP_RIGHT);
            row.getChildren().addAll(card, avatar);
        } else {
            HBox.setHgrow(card, Priority.ALWAYS);
            row.getChildren().addAll(avatar, card);
        }
        conversation.getChildren().add(row);
        // Wait for wrapping and layout before scrolling to the newest reply.
        Platform.runLater(() -> {
            chatScroll.applyCss();
            chatScroll.layout();
            chatScroll.setVvalue(1);
        });
    }

    /** Loads an image from the packaged resources rather than an external user path. */
    private Image loadImage(String name) {
        return new Image(getClass().getResource("images/" + name).toExternalForm());
    }

    /** Crops the supplied portraits around the faces and clips them to small circles. */
    private ImageView createAvatar(boolean isUser) {
        Image image = isUser ? userImage : caitlynImage;
        double size = image.getWidth() * (isUser ? 0.40 : 0.75);
        double left = image.getWidth() * (isUser ? 0.32 : 0.23);
        double top = image.getHeight() * (isUser ? 0.02 : 0.10);
        ImageView avatar = new ImageView(image);
        avatar.setViewport(new Rectangle2D(left, top, size, size));
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        avatar.setMouseTransparent(true);
        return avatar;
    }

    /** Displays the task count or a persistent session/protection status. */
    private void updateTaskCount() {
        if (isSessionEnded) {
            taskCountLabel.setText("Session ended · Close the window to exit");
        } else if (session.hasLoadingError()) {
            taskCountLabel.setText("Saved tasks unavailable · Changes disabled");
        } else {
            int count = session.getTaskCount();
            taskCountLabel.setText(count + (count == 1 ? " task" : " tasks") + " saved");
        }
    }

    /** Ends command entry while leaving the conversation available for review. */
    private void endSession() {
        isSessionEnded = true;
        commandInput.setDisable(true);
        commandInput.setPromptText("Session ended");
        commandsButton.setDisable(true);
    }
}
