package tt;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays the task manager as an interactive JavaFX chatbot.
 */
public class Gui extends Application {
    private final TT taskManager = new TT();
    private final VBox dialogContainer = new VBox(12);
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send");

    /**
     * Creates the JavaFX chatbot interface.
     */
    public Gui() {
    }

    @Override
    public void start(Stage stage) {
        ScrollPane scrollPane = createConversationPane();
        HBox inputArea = createInputArea();

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(createHeader());
        mainLayout.setCenter(scrollPane);
        mainLayout.setBottom(inputArea);
        mainLayout.getStyleClass().add("main-layout");

        Scene scene = new Scene(mainLayout, 520, 640);
        scene.getStylesheets().add(getClass().getResource("/styles/chat.css").toExternalForm());

        stage.setTitle("tt.TT");
        stage.setMinWidth(420);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();

        dialogContainer.getChildren().add(DialogBox.getBotDialog(
                "Hello! I'm tt.TT, your friendly task manager.\n\nTry: todo read a book"));
        String startupError = taskManager.getStartupError();
        if (startupError != null) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(startupError));
        }
        userInput.requestFocus();
    }

    private VBox createHeader() {
        Label appName = new Label("tt.TT");
        appName.getStyleClass().add("app-name");

        Label tagline = new Label("Keep every task on track");
        tagline.getStyleClass().add("tagline");

        VBox header = new VBox(2, appName, tagline);
        header.setPadding(new Insets(14, 16, 12, 16));
        header.getStyleClass().add("header");
        return header;
    }

    private ScrollPane createConversationPane() {
        dialogContainer.setPadding(new Insets(16));

        ScrollPane scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("conversation-pane");
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        return scrollPane;
    }

    private HBox createInputArea() {
        userInput.setPromptText("Enter a command...");
        userInput.setOnAction(event -> handleUserInput());
        sendButton.setDefaultButton(true);
        sendButton.setTooltip(new Tooltip("Send command (Enter)"));
        sendButton.setOnAction(event -> handleUserInput());

        HBox inputArea = new HBox(10, userInput, sendButton);
        inputArea.setPadding(new Insets(12));
        inputArea.getStyleClass().add("input-area");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        return inputArea;
    }

    /**
     * Adds the user's command and the task manager's response to the conversation.
     */
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            CommandResult result = taskManager.getCommandResult(input);
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(result.message()));
            return;
        }

        CommandResult result = taskManager.getCommandResult(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                result.isError()
                        ? DialogBox.getErrorDialog(result.message())
                        : DialogBox.getBotDialog(result.message()));
        userInput.clear();

        if (!result.isError() && Command.fromString(input.split("\\s+", 2)[0]) == Command.BYE) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
