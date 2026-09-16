package bambolino;

import java.util.ArrayList;
import java.util.List;

import bambolino.exception.BambolinoException;
import bambolino.parser.Parser;
import bambolino.storage.Storage;
import bambolino.task.TaskList;
import bambolino.ui.Ui;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
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
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/** Provides a chat-style JavaFX interface for Bambolino. */
public class BambolinoGui extends Application {
    private final Storage storage = new Storage();
    private TaskList tasks;
    private VBox conversation;
    private TextField input;

    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        conversation = new VBox(12);
        conversation.getStyleClass().add("conversation");
        ScrollPane scrollPane = new ScrollPane(conversation);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vvalueProperty().bind(conversation.heightProperty());
        scrollPane.getStyleClass().add("conversation-scroll");

        input = new TextField();
        input.setPromptText("Enter a command, e.g. todo read book");
        input.getStyleClass().add("command-input");
        Button send = new Button("Send");
        send.getStyleClass().add("send-button");
        send.disableProperty().bind(Bindings.createBooleanBinding(
                () -> input.getText().isBlank(), input.textProperty()));
        send.setOnAction(event -> submit());
        input.setOnAction(event -> submit());

        HBox composer = new HBox(8, input, send);
        composer.setAlignment(Pos.CENTER);
        composer.getStyleClass().add("composer");
        HBox.setHgrow(input, Priority.ALWAYS);

        ImageView headerAvatar = createAvatar("/bambolino.jpeg", 40);
        Label title = new Label("Bambolino");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Your friendly task companion");
        subtitle.getStyleClass().add("subtitle");
        VBox header = new VBox(2, title, subtitle);
        header.getStyleClass().add("header");
        HBox headerContent = new HBox(12, headerAvatar, header);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.getStyleClass().add("header-content");

        BorderPane root = new BorderPane(scrollPane, headerContent, null, composer, null);
        BorderPane.setMargin(scrollPane, new Insets(0, 16, 8, 16));
        BorderPane.setMargin(composer, new Insets(8, 16, 16, 16));
        addMessage("Hello! I'm Bambolino.\nWhat can I do for you?", false, false);
        tasks = Bambolino.loadTasks(storage, new Ui(message -> addMessage(message, false, true)));
        stage.setMinWidth(380);
        stage.setMinHeight(360);
        stage.setTitle("Bambolino");
        Scene scene = new Scene(root, 680, 520);
        scene.getStylesheets().add(getClass().getResource("/bambolino.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        input.requestFocus();
    }

    private void submit() {
        String command = input.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        List<String> lines = new ArrayList<>();
        Ui ui = new Ui(line -> {
            if (!line.matches("_+")) {
                lines.add(line);
            }
        });
        boolean isError = false;
        try {
            Parser.Command parsedCommand = new Parser().parse(command);
            if (parsedCommand.name().equals("bye") && parsedCommand.arguments().isEmpty()) {
                ui.showGoodbye();
                addMessage(command, true, false);
                addMessage(String.join("\n", lines), false, isError);
                input.clear();
                return;
            }
            Bambolino.processCommand(command, tasks, storage, ui);
        } catch (BambolinoException error) {
            isError = true;
            ui.showError(error.getMessage());
        }
        addMessage(command, true, false);
        addMessage(String.join("\n", lines), false, isError);
        if (isError) {
            input.selectAll();
        } else {
            input.clear();
        }
        input.requestFocus();
    }

    /** Adds a compact command bubble or a full-width response card with explicit error feedback. */
    private void addMessage(String text, boolean isUser, boolean isError) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMinWidth(0);
        message.setMaxWidth(Double.MAX_VALUE);
        message.getStyleClass().add("message-text");
        Label caption = new Label(isUser ? "YOU" : isError ? "ERROR / WARNING" : "BAMBOLINO");
        caption.getStyleClass().add("message-caption");
        VBox card = new VBox(5, caption, message);
        card.getStyleClass().add(isUser ? "user-message" : "bambolino-message");
        if (isError) {
            card.getStyleClass().add("error-message");
        }
        card.setMinWidth(0);
        // Leave a small inset for commands; give long task lists the full available width.
        card.maxWidthProperty().bind(conversation.widthProperty().subtract(16).multiply(isUser ? 0.85 : 1));
        if (!isUser) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }
        HBox row = new HBox(card);
        row.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        conversation.getChildren().add(row);
    }

    /** Returns a consistently sized avatar loaded from the application's resources. */
    private ImageView createAvatar(String resourcePath, double size) {
        ImageView avatar = new ImageView(new Image(getClass().getResourceAsStream(resourcePath)));
        avatar.setFitWidth(size);
        avatar.setFitHeight(size);
        avatar.setPreserveRatio(false);
        avatar.setClip(new Circle(size / 2, size / 2, size / 2));
        avatar.getStyleClass().add(resourcePath.contains("user") ? "user-avatar" : "bambolino-avatar");
        return avatar;
    }
}
