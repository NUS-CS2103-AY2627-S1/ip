package mochi.ui;

import java.nio.file.Path;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mochi.Mochi;

/**
 * Controls Mochi's main chat window.
 */
public class MainWindow extends BorderPane {
    private final VBox dialogContainer = new VBox();
    private final TextField userInput = new TextField();
    private final Mochi mochi = Mochi.forGui(Path.of("data", "mochi.txt"));

    /**
     * Creates and wires the chat controls.
     */
    public MainWindow() {
        getStylesheets().add(MainWindow.class.getResource("/mochi.css").toExternalForm());
        getStyleClass().add("chat-window");
        Label title = new Label("Mochi");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("A little less on your plate.");
        subtitle.getStyleClass().add("subtitle");
        VBox header = new VBox(3, title, subtitle);
        header.setPadding(new Insets(18, 20, 16, 20));
        header.getStyleClass().add("chat-header");
        setTop(header);

        ScrollPane scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("chat-scroll");
        dialogContainer.setPadding(new Insets(12, 16, 12, 16));
        dialogContainer.setSpacing(12);
        dialogContainer.setFillWidth(true);
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        Button sendButton = new Button("Send");
        Button clearButton = new Button("Clear");
        sendButton.getStyleClass().add("send-button");
        sendButton.disableProperty().bind(userInput.textProperty().isEmpty());
        sendButton.setOnAction(event -> handleUserInput());
        clearButton.setOnAction(event -> {
            dialogContainer.getChildren().clear();
            showWelcome();
            userInput.requestFocus();
        });
        userInput.setOnAction(event -> handleUserInput());
        userInput.setPromptText("Try: todo take a study break");

        HBox inputBar = new HBox(8, userInput, sendButton, clearButton);
        HBox.setHgrow(userInput, Priority.ALWAYS);
        userInput.setMinWidth(0);
        Label hint = new Label("list  ·  todo  ·  deadline  ·  event  ·  find  ·  sort"
                + "  ·  mark  ·  unmark  ·  delete  ·  bye");
        hint.setWrapText(true);
        hint.getStyleClass().add("command-hint");
        VBox composer = new VBox(8, inputBar, hint);
        composer.setPadding(new Insets(12, 16, 12, 16));
        composer.getStyleClass().add("composer");
        setCenter(scrollPane);
        setBottom(composer);
        showWelcome();
        Platform.runLater(userInput::requestFocus);
    }

    /** Displays the greeting and any load error, including after clearing the chat. */
    private void showWelcome() {
        dialogContainer.getChildren().add(DialogBox.mochi(Ui.WELCOME_MESSAGE));
        if (!mochi.getStartupError().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.error(mochi.getStartupError()));
        }
    }

    /** Submits one command, displays its outcome, and closes on a valid farewell. */
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        boolean shouldClose = input.equals("bye");
        String response = mochi.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.user(input),
                mochi.hasResponseError() ? DialogBox.error(response) : DialogBox.mochi(response));
        userInput.clear();
        userInput.requestFocus();
        if (shouldClose) {
            closeWindow();
        }
    }

    /**
     * Closes the JavaFX window containing this chat interface.
     */
    private void closeWindow() {
        getScene().getWindow().hide();
        Platform.exit();
    }
}
