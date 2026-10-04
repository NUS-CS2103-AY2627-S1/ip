package kelore;

import java.util.Objects;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

/** Displays the JavaFX user interface for Kelore. */
public class Main extends Application {
    private static final double INITIAL_WIDTH = 520;
    private static final double INITIAL_HEIGHT = 640;
    private static final double MINIMUM_WIDTH = 360;
    private static final double MINIMUM_HEIGHT = 420;
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.76;
    private static final double KELORE_MESSAGE_WIDTH_RATIO = 0.86;

    private final Kelore kelore = new Kelore();
    private final VBox dialogContainer = new VBox(12);
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send  →");
    private final ScrollPane conversationScrollPane = new ScrollPane(dialogContainer);

    /** Creates the Kelore JavaFX application. */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        conversationScrollPane.setFitToWidth(true);
        conversationScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationScrollPane.getStyleClass().add("conversation-scroll-pane");
        dialogContainer.getStyleClass().add("dialog-container");
        dialogContainer.setPadding(new Insets(20));
        dialogContainer.heightProperty().addListener(
                observable -> conversationScrollPane.setVvalue(1.0));

        userInput.setPromptText("Enter a command, such as \"list\"");
        userInput.getStyleClass().add("command-input");
        userInput.setAccessibleHelp("Enter a Kelore command, then press Enter to send it.");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        sendButton.getStyleClass().add("send-button");
        HBox inputArea = new HBox(10, userInput, sendButton);
        inputArea.setAlignment(Pos.CENTER);
        inputArea.setPadding(new Insets(12, 16, 16, 16));
        inputArea.getStyleClass().add("input-area");

        BorderPane root = new BorderPane(conversationScrollPane);
        root.setTop(createHeader());
        root.setBottom(inputArea);
        root.getStyleClass().add("app-root");
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        String styleSheet = Objects.requireNonNull(
                getClass().getResource("/kelore/main.css"), "GUI stylesheet is missing")
                .toExternalForm();
        scene.getStylesheets().add(styleSheet);

        userInput.setOnAction(event -> handleUserInput());
        sendButton.setOnAction(event -> handleUserInput());

        addKeloreMessage(kelore.getWelcomeMessage(), false);
        stage.setTitle("Kelore");
        stage.getIcons().add(createAppIcon());
        stage.setScene(scene);
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setResizable(true);
        stage.show();
        userInput.requestFocus();
    }

    private HBox createHeader() {
        Label brandIcon = new Label("✓");
        brandIcon.setAccessibleText("Kelore logo");
        brandIcon.getStyleClass().add("brand-icon");
        Label title = new Label("Kelore");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Plan clearly. Get things done.");
        subtitle.getStyleClass().add("app-subtitle");

        VBox titleArea = new VBox(1, title, subtitle);
        HBox header = new HBox(10, brandIcon, titleArea);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(13, 18, 13, 18));
        header.getStyleClass().add("app-header");
        return header;
    }

    /** Adds the user's command and Kelore's response to the conversation. */
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        addUserMessage(input);
        Kelore.Response response = kelore.getResponseDetails(input);
        addKeloreMessage(response.message(), response.isError());
        userInput.clear();

        if (input.equals("bye")) {
            userInput.setDisable(true);
            userInput.setPromptText("Conversation ended");
            sendButton.setDisable(true);
        }
    }

    /**
     * Adds a compact user command to the right side of the conversation.
     *
     * @param message Message to display.
     */
    private void addUserMessage(String message) {
        Label messageLabel = createWrappingLabel(message.strip(), "user-message-text");
        VBox bubble = new VBox(messageLabel);
        bubble.getStyleClass().add("user-message");
        DoubleBinding maximumWidth = Bindings.createDoubleBinding(() ->
                conversationScrollPane.getViewportBounds().getWidth() * USER_MESSAGE_WIDTH_RATIO,
                conversationScrollPane.viewportBoundsProperty());
        bubble.maxWidthProperty().bind(maximumWidth);

        Label avatar = createAvatar("Y", "user-avatar");
        HBox row = new HBox(8, bubble, avatar);
        row.setAlignment(Pos.CENTER_RIGHT);
        row.getStyleClass().add("message-row");
        dialogContainer.getChildren().add(row);
    }

    /**
     * Adds a readable Kelore response or a visually prominent error card.
     *
     * @param message Message to display.
     * @param isError Whether the message reports an error.
     */
    private void addKeloreMessage(String message, boolean isError) {
        Label heading = new Label(isError ? "Try again" : "Kelore");
        heading.getStyleClass().add("message-heading");
        Label messageLabel = createWrappingLabel(message.strip(), "kelore-message-text");

        VBox card = new VBox(5, heading, messageLabel);
        card.getStyleClass().add(isError ? "error-message" : "kelore-message");
        DoubleBinding maximumWidth = Bindings.createDoubleBinding(() ->
                conversationScrollPane.getViewportBounds().getWidth()
                        * KELORE_MESSAGE_WIDTH_RATIO,
                conversationScrollPane.viewportBoundsProperty());
        card.maxWidthProperty().bind(maximumWidth);

        Label avatar = createAvatar(isError ? "!" : "✓",
                isError ? "error-avatar" : "kelore-avatar");
        HBox row = new HBox(8, avatar, card);
        row.setAlignment(Pos.TOP_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        row.getStyleClass().add("message-row");
        dialogContainer.getChildren().add(row);
    }

    private Label createWrappingLabel(String message, String styleClass) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private Label createAvatar(String text, String styleClass) {
        Label avatar = new Label(text);
        avatar.getStyleClass().addAll("message-avatar", styleClass);
        return avatar;
    }

    private WritableImage createAppIcon() {
        int iconSize = 64;
        Canvas canvas = new Canvas(iconSize, iconSize);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.web("#0F766E"));
        graphics.fillOval(2, 2, 60, 60);
        graphics.setStroke(Color.WHITE);
        graphics.setLineWidth(7);
        graphics.setLineCap(StrokeLineCap.ROUND);
        graphics.strokeLine(17, 33, 28, 44);
        graphics.strokeLine(28, 44, 48, 21);
        return canvas.snapshot(null, new WritableImage(iconSize, iconSize));
    }
}
