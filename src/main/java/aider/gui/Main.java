package aider.gui;

import aider.Aider;
import aider.AiderException;
import aider.Personality;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Provides the graphical user interface for Aider. */
public final class Main extends javafx.application.Application {
    /** Displays the conversation history. */
    private final VBox dialogContainer = new VBox(12);

    /** Accepts commands from the user. */
    private final TextField userInput = new TextField();

    /** Sends the command in the input field. */
    private final Button sendButton = new Button("Send");

    /** Displays the scrollable conversation history. */
    private final ScrollPane conversation = new ScrollPane(dialogContainer);

    /** Displays the current application status. */
    private final Label statusLabel = new Label("Ready when you are");

    /** Connects the GUI to the existing application logic. */
    private final Aider aider = new Aider("./data/aider.txt");

    /** Builds and displays the application window. */
    @Override
    public void start(Stage stage) {
        conversation.setFitToWidth(true);
        conversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversation.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        conversation.getStyleClass().add("conversation-scroll");
        dialogContainer.getStyleClass().add("dialog-container");

        Label title = new Label("Aider");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Your focused task companion");
        subtitle.getStyleClass().add("app-subtitle");
        VBox titleBlock = new VBox(2, title, subtitle);

        statusLabel.getStyleClass().add("status-label");
        HBox header = new HBox(titleBlock, statusLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);
        HBox.setHgrow(titleBlock, Priority.ALWAYS);
        header.getStyleClass().add("app-header");

        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("app-layout");
        layout.setTop(header);
        layout.setCenter(conversation);
        HBox inputBar = new HBox(8, userInput, sendButton);
        inputBar.setPadding(new Insets(14, 18, 14, 18));
        inputBar.getStyleClass().add("input-bar");
        userInput.setPromptText("Type a command, e.g. todo read a book");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        layout.setBottom(inputBar);

        sendButton.setOnAction(event -> submitCommand());
        userInput.setOnAction(event -> submitCommand());
        addAiderMessage(Personality.welcomeMessage());

        Scene scene = new Scene(layout, 620, 720);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("Aider — Task Companion");
        stage.setMinWidth(440);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
        Platform.runLater(userInput::requestFocus);
    }

    /** Sends the entered command and displays the response. */
    private void submitCommand() {
        String command = userInput.getText().trim();
        if (command.isEmpty()) {
            addErrorMessage("OOPS!!! Please enter a command first.");
            userInput.requestFocus();
            return;
        }
        addUserMessage(command);
        userInput.clear();
        try {
            addAiderMessage(aider.processCommand(command));
            if (command.equals("bye")) {
                sendButton.setDisable(true);
                userInput.setDisable(true);
                statusLabel.setText("Session complete - nice work");
            }
        } catch (AiderException exception) {
            addErrorMessage("OOPS!!! " + exception.getMessage());
        }
        userInput.requestFocus();
    }

    /** Adds a user message to the conversation. */
    private void addUserMessage(String message) {
        addDialog(new DialogBox(message, true));
    }

    /** Adds an Aider message to the conversation. */
    private void addAiderMessage(String message) {
        addDialog(new DialogBox(message, false));
    }

    /** Adds an error message with a visual treatment distinct from normal replies. */
    private void addErrorMessage(String message) {
        addDialog(new DialogBox(message, false, true));
    }

    /** Adds a message and keeps the newest response visible. */
    private void addDialog(DialogBox dialog) {
        dialogContainer.getChildren().add(dialog);
        Platform.runLater(() -> conversation.setVvalue(1.0));
    }
}
