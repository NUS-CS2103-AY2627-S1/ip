package vermithor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** A minimal, fit-for-purpose JavaFX interface for Vermithor, with per-message avatars. */
public class MainApp extends Application {
    private final List<Task> tasks = new ArrayList<>();
    private final Parser parser = new Parser();
    private final Storage storage = new Storage(Path.of("data", "vermithor.txt"));
    private final VBox dialogContainer = new VBox(8);
    private final ScrollPane scrollPane = new ScrollPane(dialogContainer);
    private final Label status = new Label("Ready");
    private final Image botAvatar = new Image(getClass().getResourceAsStream("/images/vermithor.png"));
    private final Image userAvatar = new Image(getClass().getResourceAsStream("/images/user.png"));
    private Stage stage;

    /** Builds and displays the chatbot window. */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        dialogContainer.setPadding(new Insets(12));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f4f7f6; -fx-background-color: transparent;");
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> scrollPane.setVvalue(1.0));

        TextField input = new TextField();
        input.setPromptText("Enter a command, e.g. list or todo read a book");
        input.setStyle("-fx-font-size: 14px;");
        Button send = new Button("Send");
        send.setDefaultButton(true);
        send.setStyle("-fx-font-weight: bold; -fx-padding: 8 18 8 18;");
        send.setOnAction(event -> submit(input));
        input.setOnAction(event -> submit(input));

        HBox controls = new HBox(8, input, send);
        controls.setPadding(new Insets(10));
        HBox.setHgrow(input, Priority.ALWAYS);
        status.setStyle("-fx-text-fill: #356859; -fx-font-size: 12px;");
        VBox bottom = new VBox(4, controls, status);
        BorderPane root = new BorderPane(scrollPane, null, null, bottom, null);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f4f7f6;");
        stage.setTitle("Vermithor");
        stage.setScene(new Scene(root, 650, 450));
        stage.setMinWidth(420);
        stage.setMinHeight(300);
        stage.show();
        addBotMessage("Hello! I'm Vermithor. What can I do for you?");
        loadSavedTasks();
    }

    /** Loads tasks saved in the folder from which the application was launched. */
    private void loadSavedTasks() {
        try {
            tasks.addAll(storage.load());
            if (!tasks.isEmpty()) {
                addBotMessage("Loaded " + tasks.size() + " saved task(s).");
            }
        } catch (VermithorException exception) {
            addBotMessage("OOPS!!! " + exception.getMessage());
            status.setText("Could not load saved tasks.");
            status.setStyle("-fx-text-fill: #b23a48; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
    }

    /** Processes one entered command and displays the chatbot response. */
    private void submit(TextField input) {
        String command = input.getText().trim();
        if (command.isEmpty()) {
            status.setText("Please enter a command.");
            status.setStyle("-fx-text-fill: #b23a48; -fx-font-size: 12px; -fx-font-weight: bold;");
            return;
        }
        status.setText("Processing...");
        status.setStyle("-fx-text-fill: #356859; -fx-font-size: 12px;");
        input.clear();
        addUserMessage(command);
        if (command.equalsIgnoreCase("bye")) {
            addBotMessage("Bye. Hope to see you again soon!");
            try {
                storage.save(tasks);
            } catch (VermithorException exception) {
                addBotMessage("OOPS!!! " + exception.getMessage());
            }
            stage.close();
            Platform.exit();
            return;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output));
            Vermithor.processCommand(parser, command, tasks);
            storage.save(tasks);
        } catch (VermithorException exception) {
            System.out.println("OOPS!!! " + exception.getMessage());
            status.setText("Error: " + exception.getMessage());
            status.setStyle("-fx-text-fill: #b23a48; -fx-font-size: 12px; -fx-font-weight: bold;");
        } finally {
            System.setOut(original);
        }
        String reply = output.toString().stripTrailing();
        if (!reply.isEmpty()) {
            addBotMessage(reply);
        }
        if (!status.getText().startsWith("Error:")) {
            status.setText("Ready");
        }
    }

    /** Appends a left-aligned message bubble from Vermithor. */
    private void addBotMessage(String text) {
        dialogContainer.getChildren().add(DialogBox.botMessage(text, botAvatar));
    }

    /** Appends a right-aligned message bubble for the user's own input. */
    private void addUserMessage(String text) {
        dialogContainer.getChildren().add(DialogBox.userMessage(text, userAvatar));
    }
}
