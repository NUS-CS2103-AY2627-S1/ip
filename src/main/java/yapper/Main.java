package yapper;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Presents Yapper's commands and responses as an accessible desktop conversation.
 */
public class Main extends Application {
    @FXML
    private VBox messages;
    @FXML
    private TextField input;
    @FXML
    private Button send;
    @FXML
    private ScrollPane scroll;
    private Yapper yapper;

    @Override
    public void start(Stage stage) throws IOException {
        yapper = new Yapper(Path.of(System.getProperty("yapper.data", "data/yapper.txt")));
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/yapper/MainWindow.fxml"));
        loader.setController(this);
        BorderPane root = loader.load();
        messages.heightProperty().addListener((observable, oldHeight, newHeight) -> scroll.setVvalue(1));
        Scene scene = new Scene(root, 760, 700);
        scene.getStylesheets().add(Main.class.getResource("/yapper/chat.css").toExternalForm());
        stage.setTitle("Yapper — Your task companion");
        stage.setMinWidth(480);
        stage.setMinHeight(420);
        stage.setScene(scene);
        String welcome = yapper.getWelcome();
        int greeting = welcome.indexOf(" WELL, HELLO THERE!");
        appendMessage(greeting < 0 ? welcome : welcome.substring(greeting), false);
        stage.show();
        Platform.runLater(input::requestFocus);
    }

    /**
     * Sends one nonblank command and displays the backend's complete response.
     */
    @FXML
    private void submit() {
        String command = input.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        input.clear();
        appendMessage(command, true);
        appendMessage(yapper.getResponse(command), false);
        if (yapper.isExitRequested()) {
            input.setDisable(true);
            send.setDisable(true);
            input.setPromptText("Conversation finished. Close the window to exit.");
        } else {
            input.requestFocus();
        }
    }

    /**
     * Adds a wrapping bubble whose width follows the conversation viewport.
     *
     * @param text Message content.
     * @param isUser Whether this message is a command from the user.
     */
    private void appendMessage(String text, boolean isUser) {
        Label speaker = new Label(isUser ? "YOU" : "YAPPER");
        speaker.getStyleClass().add("speaker");
        String displayed = isUser ? text : text.replaceAll("(?m)^_{3,}\\R?", "").strip();
        Label body = new Label(displayed);
        body.setWrapText(true);
        body.setMinWidth(0);
        body.setAccessibleText((isUser ? "You: " : "Yapper: ") + text);
        VBox bubble = new VBox(7, speaker, body);
        bubble.getStyleClass().addAll("bubble", isUser ? "user-bubble" : "bot-bubble");
        bubble.maxWidthProperty().bind(messages.widthProperty().subtract(64));
        HBox row = new HBox(bubble);
        row.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        messages.getChildren().add(row);
    }
}
