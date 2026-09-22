package friday;

import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

/**
 * Displays Friday's graphical chat interface.
 */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 600;
    private static final double WINDOW_HEIGHT = 500;
    private static final double AVATAR_SIZE = 40;

    private final Friday friday = new Friday();
    private final Image fridayAvatar = loadFridayAvatar();

    @Override
    public void start(Stage stage) {
        VBox messages = new VBox(10);
        ScrollPane conversation = createConversationArea(messages);
        TextField input = new TextField();
        input.setPromptText("Enter a command...");
        input.setStyle("-fx-background-color: #fffdf5; -fx-background-radius: 10; "
                + "-fx-border-color: #ffd1d6; -fx-border-radius: 10; "
                + "-fx-text-fill: #403840; -fx-prompt-text-fill: #746974; "
                + "-fx-focus-color: #d1eef6; -fx-faint-focus-color: transparent;");
        HBox inputBar = createInputBar(input, messages, conversation);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #fff0b8;");
        root.setCenter(conversation);
        root.setBottom(inputBar);

        stage.setTitle("Friday");
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
        input.requestFocus();
    }

    private ScrollPane createConversationArea(VBox messages) {
        messages.setPadding(new Insets(10));
        addMessage(messages, "Hello! I'm Friday.\nWhat you want ah?", false);

        ScrollPane conversation = new ScrollPane(messages);
        conversation.setFitToWidth(true);
        conversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversation.setStyle("-fx-background-color: transparent; -fx-background: #fffdf5;");
        return conversation;
    }

    private HBox createInputBar(TextField input, VBox messages, ScrollPane conversation) {
        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-base: #ffd6b3; -fx-background-radius: 10; "
                + "-fx-text-fill: #403840; -fx-focus-color: #d1eef6; "
                + "-fx-faint-focus-color: transparent;");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> handleUserInput(input, messages, conversation));
        input.setOnAction(event -> handleUserInput(input, messages, conversation));

        HBox inputBar = new HBox(10, input, sendButton);
        inputBar.setPadding(new Insets(10, 0, 0, 0));
        HBox.setHgrow(input, Priority.ALWAYS);
        return inputBar;
    }

    private void handleUserInput(TextField input, VBox messages, ScrollPane conversation) {
        String userInput = input.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }

        String response = friday.getResponse(userInput);
        addMessage(messages, userInput, true);
        addMessage(messages, response, false);
        input.clear();
        Platform.runLater(() -> conversation.setVvalue(1.0));

        if (userInput.equals("bye")) {
            input.setDisable(true);
            Platform.exit();
        }
    }

    private void addMessage(VBox messages, String text, boolean isUser) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(400);
        message.setPadding(new Insets(8, 12, 8, 12));
        message.setStyle(isUser
                ? "-fx-background-color: #d1eef6; -fx-background-radius: 14; -fx-text-fill: #403840;"
                : "-fx-background-color: #ffd1d6; -fx-background-radius: 14; -fx-text-fill: #403840;");

        StackPane avatar = createAvatar(isUser);
        HBox messageRow = isUser ? new HBox(8, message, avatar) : new HBox(8, avatar, message);
        messageRow.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        messages.getChildren().add(messageRow);
    }

    /** Loads the bundled portrait once, allowing a generic fallback if it is unavailable. */
    private Image loadFridayAvatar() {
        URL resource = Main.class.getResource("/images/friday.png");
        if (resource == null) {
            return null;
        }
        Image image = new Image(resource.toExternalForm());
        return image.isError() ? null : image;
    }

    /** Creates a fixed-size avatar with a portrait or a generic person silhouette. */
    private StackPane createAvatar(boolean isUser) {
        StackPane avatar = new StackPane();
        avatar.setMinSize(AVATAR_SIZE, AVATAR_SIZE);
        avatar.setPrefSize(AVATAR_SIZE, AVATAR_SIZE);
        avatar.setMaxSize(AVATAR_SIZE, AVATAR_SIZE);
        avatar.setAccessibleText(isUser ? "User profile" : "Friday profile");

        if (!isUser && fridayAvatar != null) {
            ImageView portrait = new ImageView(fridayAvatar);
            double side = Math.min(fridayAvatar.getWidth(), fridayAvatar.getHeight());
            double cropX = (fridayAvatar.getWidth() - side) / 2;
            double cropY = (fridayAvatar.getHeight() - side) / 2;
            portrait.setViewport(new Rectangle2D(cropX, cropY, side, side));
            portrait.setFitWidth(AVATAR_SIZE);
            portrait.setFitHeight(AVATAR_SIZE);
            avatar.getChildren().add(portrait);
        } else {
            Circle background = new Circle(AVATAR_SIZE / 2,
                    Color.web(isUser ? "#d1eef6" : "#ffd1d6"));
            Circle head = new Circle(7, Color.web("#403840"));
            head.setTranslateY(-6);
            Ellipse shoulders = new Ellipse(13, 10);
            shoulders.setFill(Color.web("#403840"));
            shoulders.setTranslateY(12);
            avatar.getChildren().addAll(background, shoulders, head);
        }
        avatar.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        return avatar;
    }
}
