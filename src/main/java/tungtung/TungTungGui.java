package tungtung;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Provides a JavaFX chat interface for the Tung Tung chatbot. */
public class TungTungGui extends Application {
    private static final String WINDOW_BACKGROUND = "#1e1e1e";
    private static final String HISTORY_BACKGROUND = "#252526";
    private static final String INPUT_BACKGROUND = "#333333";
    private static final String TEXT_COLOR = "#f2f2f2";
    private static final String USER_MESSAGE_COLOR = "#087f8c";
    private static final String BOT_MESSAGE_COLOR = "#385a46";

    private final ChatbotEngine engine = new ChatbotEngine();
    private final VBox messages = new VBox(10);
    private ScrollPane history;
    private Stage stage;
    private final Image botAvatar = new Image(
            getClass().getResourceAsStream("/images/tungtung-avatar.png"));
    private final Image userAvatar = new Image(
            getClass().getResourceAsStream("/images/user-avatar.png"));

    /**
     * Builds and displays the chatbot window.
     *
     * @param stage primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        messages.setPadding(new Insets(16));
        messages.setStyle("-fx-background-color: " + HISTORY_BACKGROUND + ";");
        addBotMessage("Hello! Tung Tung Sahere!\nHow can I assist?");
        if (engine.getStartupError() != null) {
            addBotMessage(engine.getStartupError());
        }

        history = new ScrollPane(messages);
        history.setFitToWidth(true);
        history.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        history.setStyle("-fx-background: " + HISTORY_BACKGROUND + ";"
                + " -fx-background-color: " + HISTORY_BACKGROUND + ";");
        messages.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> history.setVvalue(1.0)));

        TextField input = new TextField();
        input.setPromptText("Type a command...");
        input.setStyle("-fx-background-color: " + INPUT_BACKGROUND + ";"
                + " -fx-text-fill: " + TEXT_COLOR + "; -fx-prompt-text-fill: #aaaaaa;");
        Button send = new Button("Send");
        send.setStyle("-fx-background-color: #4f8cff; -fx-text-fill: white;");
        send.setDefaultButton(true);
        Runnable submit = () -> submit(input);
        send.setOnAction(event -> submit.run());
        input.setOnAction(event -> submit.run());

        HBox composer = new HBox(8, input, send);
        composer.setPadding(new Insets(10));
        composer.setAlignment(Pos.CENTER);
        HBox.setHgrow(input, javafx.scene.layout.Priority.ALWAYS);

        VBox root = new VBox(history, composer);
        root.setStyle("-fx-background-color: " + WINDOW_BACKGROUND + ";");
        VBox.setVgrow(history, javafx.scene.layout.Priority.ALWAYS);
        Scene scene = new Scene(root, 520, 640);
        stage.setTitle("Tung Tung");
        stage.setScene(scene);
        stage.show();
    }

    private void submit(TextField input) {
        String command = input.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        addUserMessage(command);
        addUserMessage("giggity giggity");
        if (command.equals("bye")) {
            addBotMessage("Tung Tung Tung Sahur 67");
            addBotMessage("Bye! Tung Tung Sagone!");
            PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
            pause.setOnFinished(event -> stage.close());
            pause.play();
            input.clear();
            return;
        }
        addBotMessage("Tung Tung Tung Sahur 67");
        addBotMessage(engine.execute(command));
        input.clear();
    }

    private void addUserMessage(String text) {
        addMessage(text, USER_MESSAGE_COLOR, Pos.CENTER_RIGHT, userAvatar);
    }

    private void addBotMessage(String text) {
        addMessage(text, BOT_MESSAGE_COLOR, Pos.CENTER_LEFT, botAvatar);
    }

    private void addMessage(String text, String color, Pos alignment, Image avatar) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(420);
        message.setPadding(new Insets(10));
        message.setTextFill(Color.web(TEXT_COLOR));
        message.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12;");
        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(42);
        avatarView.setFitHeight(42);
        HBox row = alignment == Pos.CENTER_RIGHT
                ? createMessageRow(message, avatarView)
                : createMessageRow(avatarView, message);
        row.setAlignment(alignment);
        messages.getChildren().add(row);
    }

    /** Creates a message row containing a flexible number of JavaFX nodes. */
    private HBox createMessageRow(Node... nodes) {
        return new HBox(8, nodes);
    }
}
