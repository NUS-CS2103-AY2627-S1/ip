package nexus;

import javafx.application.Application;
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

/** Provides the JavaFX graphical interface for Nexus. */
public class Main extends Application {
    private static final String BACKGROUND = "#071525";
    private static final String PRIMARY = "#16b8a6";
    private static final String TEXT = "#dcecf5";
    private static final String ERROR = "#ff8a8a";
    private final Nexus nexus = new Nexus("data/nexus.txt");

    /** Builds and displays the responsive Nexus window. */
    @Override
    public void start(Stage stage) {
        VBox messages = new VBox(12);
        messages.setPadding(new Insets(20, 18, 20, 18));
        messages.setStyle("-fx-background-color: " + BACKGROUND + ";");
        addBotMessage(messages, "✦ Nexus online.\nGive me a task, and I’ll keep it in your flight plan.");

        ScrollPane history = new ScrollPane(messages);
        history.setFitToWidth(true);
        history.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        history.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");

        TextField input = new TextField();
        input.setPromptText("Try: list, find, mark 1, or add a waypoint");
        input.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px; -fx-padding: 11px 13px;"
                + " -fx-background-radius: 9px; -fx-background-color: #10263a; -fx-text-fill: " + TEXT + ";");
        Button send = new Button("Launch");
        send.setDefaultButton(true);
        send.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 11px 18px; -fx-background-radius: 9px;");
        HBox controls = new HBox(10, input, send);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(12, 18, 16, 18));
        controls.setStyle("-fx-background-color: #0b1d30; -fx-border-color: #1e4053; -fx-border-width: 1px 0 0 0;");
        HBox.setHgrow(input, Priority.ALWAYS);

        Runnable submit = () -> {
            String command = input.getText().trim();
            if (command.isEmpty()) {
                return;
            }
            addUserMessage(messages, command);
            String response = nexus.executeCommand(command);
            if (isError(response)) {
                addErrorMessage(messages, response);
            } else {
                addBotMessage(messages, response);
            }
            input.clear();
            history.setVvalue(1.0);
        };
        send.setOnAction(event -> submit.run());
        input.setOnAction(event -> submit.run());

        BorderPane root = new BorderPane(history);
        root.setBottom(controls);
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");
        Scene scene = new Scene(root, 600, 450);
        stage.setTitle("Nexus — Mission Control");
        stage.setMinWidth(360);
        stage.setMinHeight(300);
        stage.setScene(scene);
        stage.show();
    }

    /** Adds a user command using a compact, right-aligned treatment. */
    private void addUserMessage(VBox messages, String text) {
        Label message = createMessage(text, "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white;");
        HBox row = new HBox(message);
        row.setAlignment(Pos.CENTER_RIGHT);
        messages.getChildren().add(row);
    }

    /** Adds a Nexus response using a spacious, left-aligned treatment. */
    private void addBotMessage(VBox messages, String text) {
        Label message = createMessage("✦  " + text, "-fx-background-color: #10263a; -fx-text-fill: " + TEXT + ";"
                + " -fx-border-color: #1e4053; -fx-border-width: 1px;");
        HBox row = new HBox(message);
        row.setAlignment(Pos.CENTER_LEFT);
        messages.getChildren().add(row);
    }

    /** Adds an error response with a visually distinct warning treatment. */
    private void addErrorMessage(VBox messages, String text) {
        Label message = createMessage("⚠ " + text, "-fx-background-color: #fff1f0; -fx-text-fill: " + ERROR + ";"
                + " -fx-border-color: #fecdca; -fx-border-width: 1px;");
        HBox row = new HBox(message);
        row.setAlignment(Pos.CENTER_LEFT);
        messages.getChildren().add(row);
    }

    /** Creates a wrapped message label that adapts to the available window width. */
    private Label createMessage(String text, String colors) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(Double.MAX_VALUE);
        message.setPadding(new Insets(10, 13, 10, 13));
        message.setStyle(colors + " -fx-font-family: 'Consolas'; -fx-font-size: 14px; -fx-line-spacing: 2px;"
                + " -fx-background-radius: 11px;");
        return message;
    }

    /** Returns whether a response describes an invalid or unsuccessful command. */
    private boolean isError(String response) {
        return response.startsWith("OOPS!!!") || response.startsWith("Please")
                || response.startsWith("There is no task") || response.startsWith("Nexus protocol:")
                || response.startsWith("The jump date") || response.startsWith("Enter the jump date")
                || response.startsWith("Give me a signal") || response.startsWith("Specify a valid");
    }
}
