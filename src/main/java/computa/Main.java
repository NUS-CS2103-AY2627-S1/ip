package computa;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/** JavaFX user interface for the Computa chatbot. */
public class Main extends Application {
    private static final String TASK_FILE_PATH = "./tasks.txt";
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 450;
    private final Computa computa = new Computa(TASK_FILE_PATH);

    @Override
    public void start(Stage stage) {
        VBox messages = new VBox(12);
        messages.setPadding(new Insets(16, 12, 16, 12));
        messages.setStyle("-fx-background-color: #fff1f6;");
        ScrollPane chat = new ScrollPane(messages);
        chat.setFitToWidth(true);
        addMessage(messages, "haiii i am computa! lmk what u need ehaha", false);

        TextField input = new TextField();
        input.setPromptText("Enter a command, e.g. list or todo study");
        Button send = new Button("Send");
        Runnable submit = () -> {
            String command = input.getText().trim();
            if (command.isEmpty()) {
                return;
            }
            String response = computa.processCommand(command);
            if (computa.lastCommandHadError()) {
                showError(stage, response);
            } else {
                // Only display the user's message after parsing succeeds, so invalid
                // commands do not remain in the conversation history.
                addMessage(messages, command, true);
                if (!response.isBlank()) {
                    addMessage(messages, response, false);
                }
            }
            chat.setVvalue(1.0);
            input.clear();
            if (command.equals("bye")) {
                stage.close();
            }
        };
        send.setOnAction(event -> submit.run());
        input.setOnAction(event -> submit.run());

        HBox commandBar = new HBox(8, input, send);
        HBox.setHgrow(input, Priority.ALWAYS);
        commandBar.setPadding(new Insets(10, 12, 12, 12));
        commandBar.setStyle("-fx-background-color: #ffdce9;");
        input.setStyle("-fx-font-size: 14px; -fx-background-radius: 14px;");
        send.setStyle("-fx-font-weight: bold; -fx-text-fill: white; "
                + "-fx-background-color: #d96b96; -fx-background-radius: 14px;");

        BorderPane root = new BorderPane(chat);
        root.setBottom(commandBar);
        BorderPane.setMargin(chat, new Insets(8));
        root.setStyle("-fx-background-color: #fff1f6;");
        stage.setTitle("Computa");
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }

    /** Adds one message row containing the sender's avatar, name, and text. */
    private void addMessage(VBox messages, String text, boolean fromUser) {
        String imagePath = fromUser ? "/EvilPlankton.png" : "/Computa.png";
        var imageStream = Main.class.getResourceAsStream(imagePath);
        Node avatar;
        if (imageStream != null) {
            ImageView imageView = new ImageView(new Image(imageStream));
            imageView.setFitWidth(48);
            imageView.setFitHeight(48);
            imageView.setPreserveRatio(true);
            imageView.setClip(new Circle(24, 24, 24));
            avatar = imageView;
        } else {
            avatar = new Label(fromUser ? "UserImage" : "ComputaImage");
        }

        Label sender = new Label(fromUser ? "User" : "Computa");
        sender.setStyle("-fx-font-weight: bold; -fx-text-fill: #8c3f62;");
        sender.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        Label message = new Label(text);
        message.setWrapText(true);
        message.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #442c38; "
                + "-fx-background-color: #ffffff; -fx-padding: 8px 10px; "
                + "-fx-background-radius: 10px;");

        VBox messageContent = new VBox(2, sender, message);
        messageContent.setMaxWidth(450);
        messageContent.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        HBox row = fromUser
                ? new HBox(8, messageContent, avatar)
                : new HBox(8, avatar, messageContent);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messages.getChildren().add(row);
    }

    /** Shows command errors without adding them to the conversation. */
    private void showError(Stage owner, String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(owner);
        alert.setTitle("Command error");
        alert.setHeaderText("Computa could not understand that command");
        alert.setContentText(error.trim());
        alert.showAndWait();
    }
}
