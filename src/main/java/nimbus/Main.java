package nimbus;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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

/** Provides the JavaFX user interface for Nimbus. */
public class Main extends Application {
    private final Nimbus nimbus = new Nimbus(Path.of("data", "nimbus.txt"));
    private final VBox dialogContainer = new VBox(10);
    private final TextField userInput = new TextField();

    @Override
    public void start(Stage stage) {
        Label title = new Label("Mr. Nimbus");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("King of the ocean. Ruler of your task list.");
        subtitle.getStyleClass().add("app-subtitle");
        VBox header = new VBox(2, title, subtitle);
        header.getStyleClass().add("header");

        ScrollPane dialogScrollPane = new ScrollPane(dialogContainer);
        dialogScrollPane.setFitToWidth(true);
        dialogScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialogScrollPane.getStyleClass().add("dialog-scroll-pane");
        dialogContainer.setPadding(new Insets(12));
        dialogContainer.getStyleClass().add("dialog-container");
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) ->
                dialogScrollPane.setVvalue(1.0));

        Button sendButton = new Button("Command");
        sendButton.setDefaultButton(true);
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(event -> handleUserInput());
        userInput.setPromptText("Your decree, e.g. todo conquer chapter 6");
        userInput.getStyleClass().add("command-input");
        userInput.setOnAction(event -> handleUserInput());
        HBox inputBar = new HBox(8, userInput, sendButton);
        inputBar.getStyleClass().add("input-bar");
        HBox.setHgrow(userInput, Priority.ALWAYS);

        Label hint = new Label("Decrees: list, todo, deadline, event, find, update, bye");
        hint.getStyleClass().add("command-hint");
        VBox composer = new VBox(7, inputBar, hint);

        BorderPane root = new BorderPane(dialogScrollPane, header, null, composer, null);
        root.getStyleClass().add("app-root");
        root.setPadding(new Insets(16));
        BorderPane.setMargin(header, new Insets(0, 0, 12, 0));
        BorderPane.setMargin(composer, new Insets(12, 0, 0, 0));

        Scene scene = new Scene(root, 640, 520);
        scene.getStylesheets().add(Main.class.getResource("/nimbus/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(480);
        stage.setMinHeight(360);
        stage.setTitle("Mr. Nimbus");
        stage.show();

        addMessage(nimbus.getWelcomeMessage(), MessageSender.NIMBUS);
        userInput.requestFocus();
    }

    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        addMessage(input, MessageSender.USER);
        userInput.clear();
        Nimbus.Response response = nimbus.getResponseWithStatus(input);
        addMessage(response.message(), MessageSender.NIMBUS);
        if (response.isExit()) {
            userInput.setDisable(true);
            Platform.runLater(() -> ((Stage) userInput.getScene().getWindow()).close());
        }
    }

    private void addMessage(String message, MessageSender sender) {
        boolean isFromUser = sender == MessageSender.USER;
        Label speaker = new Label(isFromUser ? "You" : "Mr. Nimbus");
        speaker.getStyleClass().add("speaker-label");
        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMaxWidth(460);
        bubble.getStyleClass().addAll("message-bubble", isFromUser ? "user-bubble" : "nimbus-bubble");
        VBox messageGroup = new VBox(4, speaker, bubble);
        messageGroup.setMaxWidth(460);
        HBox row = new HBox(messageGroup);
        row.getStyleClass().add(isFromUser ? "user-row" : "nimbus-row");
        dialogContainer.getChildren().add(row);
    }

    private enum MessageSender {
        USER,
        NIMBUS
    }

    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
