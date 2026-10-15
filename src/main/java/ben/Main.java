package ben;

import javafx.animation.PauseTransition;
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
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The JavaFX entry point: a chat-style window for talking to {@link Ben}.
 * <p>
 * Level-10: replaces the text-only console interface with a GUI. The
 * layout is built in code rather than FXML so the whole UI stays in one
 * readable file &ndash; it is a vertically scrolling list of message
 * "bubbles" with a text field and a Send button along the bottom. FXML
 * would be the more scalable alternative once the UI grows.
 */
public class Main extends Application {
    /** Widest a single message bubble may grow before its text wraps. */
    private static final double BUBBLE_MAX_WIDTH = 280;
    /** Pause after a "bye" so the user can read the farewell before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    private final Ben ben = new Ben();

    private final VBox dialogContainer = new VBox(10);
    private final ScrollPane scrollPane = new ScrollPane(dialogContainer);
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send");

    /**
     * Builds the window and shows the greeting.
     *
     * @param stage the primary stage supplied by JavaFX
     */
    @Override
    public void start(Stage stage) {
        dialogContainer.setPadding(new Insets(10));
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        // Keep the newest message in view as the conversation grows.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollPane.setVvalue(1.0));

        userInput.setPromptText("Type a command and press Enter");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        HBox inputRow = new HBox(10, userInput, sendButton);
        inputRow.setPadding(new Insets(10));

        // Send on either the button or the Enter key.
        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());

        VBox root = new VBox(scrollPane, inputRow);
        stage.setScene(new Scene(root, 400, 600));
        stage.setTitle("Ben");
        stage.setMinWidth(300);
        stage.setMinHeight(400);
        stage.show();

        addDialog("Hello! I'm Ben\nWhat can I do for you?", false);
    }

    /**
     * Reads the text field, shows the user's line and Ben's reply, then
     * clears the field. Typing "bye" closes the window after a short pause.
     */
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        addDialog(input, true);
        addDialog(ben.getResponse(input), false);
        userInput.clear();

        if (input.equals("bye")) {
            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }

    /**
     * Appends one message bubble to the conversation.
     *
     * @param text     the message to display
     * @param fromUser {@code true} to align/colour it as the user's message,
     *                 {@code false} for one of Ben's replies
     */
    private void addDialog(String text, boolean fromUser) {
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setPadding(new Insets(8));
        bubble.setMaxWidth(BUBBLE_MAX_WIDTH);
        bubble.setStyle(fromUser
                ? "-fx-background-color: #d0e6ff; -fx-background-radius: 8;"
                : "-fx-background-color: #ececec; -fx-background-radius: 8;");

        HBox row = new HBox(bubble);
        row.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        dialogContainer.getChildren().add(row);
    }
}
