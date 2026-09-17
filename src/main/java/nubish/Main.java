package nubish;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX application that shows Nubish as a WhatsApp-style chat.
 */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 520;
    private static final double WINDOW_HEIGHT = 680;
    private static final double MESSAGE_WIDTH_RATIO = 0.78;
    private static final double CHARACTER_TYPING_MILLIS = 12;
    private static final double MAX_TYPING_DURATION_MILLIS = 2000;

    private final Nubish nubish = new Nubish();

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextField input;

    @FXML
    private Button sendButton;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        fxmlLoader.setController(this);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        stage.setTitle("Nubish");
        stage.setScene(scene);
        stage.show();

        addNubishMessage(nubish.start());
        addNubishMessage(nubish.remind());
        chatBox.heightProperty().addListener((observable) -> scrollToLatestMessage());
    }

    @Override
    public void stop() {
        nubish.save();
    }

    @FXML
    private void handleUserInput() {
        String command = input.getText();
        if (command.isBlank()) {
            return;
        }

        addUserMessage(command);
        input.clear();

        String response = nubish.getResponse(command);
        addNubishMessage(response, nubish.isLastResponseError());

        if (command.equalsIgnoreCase("bye")) {
            input.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    private void addUserMessage(String message) {
        chatBox.getChildren().add(createMessageRow(message, true));
        scrollToLatestMessage();
    }

    private void addNubishMessage(String message) {
        addNubishMessage(message, false);
    }

    private void addNubishMessage(String message, boolean isError) {
        if (!message.isEmpty()) {
            chatBox.getChildren().add(createMessageRow(message, false, isError));
            scrollToLatestMessage();
        }
    }

    private HBox createMessageRow(String message, boolean isUser) {
        return createMessageRow(message, isUser, false);
    }

    private HBox createMessageRow(String message, boolean isUser, boolean isError) {
        HBox row = loadDialogBox();
        row.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);

        Label bubble = (Label) row.lookup("#bubble");
        bubble.setText(isUser ? message : "");
        bubble.setTextOverrun(OverrunStyle.CLIP);
        bubble.setMinHeight(Region.USE_PREF_SIZE);
        bubble.setPrefHeight(Region.USE_COMPUTED_SIZE);
        bubble.setMaxHeight(Double.MAX_VALUE);
        bubble.maxWidthProperty().bind(chatScrollPane.widthProperty().multiply(MESSAGE_WIDTH_RATIO));
        bubble.getStyleClass().add(isUser ? "user-bubble" : "nubish-bubble");
        if (isError) {
            bubble.getStyleClass().add("error-bubble");
        }
        if (!isUser && message.contains("\n")) {
            bubble.getStyleClass().add("ascii-bubble");
        }

        Label avatarLabel = (Label) row.lookup("#avatarLabel");
        avatarLabel.setText(isUser ? "You" : "N");

        StackPane avatar = (StackPane) row.lookup("#avatar");
        avatar.getStyleClass().add(isUser ? "user-avatar" : "nubish-avatar");

        Region leftSpacer = (Region) row.lookup("#leftSpacer");
        Region rightSpacer = (Region) row.lookup("#rightSpacer");
        Node leftTail = row.lookup("#leftTail");
        Node rightTail = row.lookup("#rightTail");
        StackPane bubbleGroup = (StackPane) row.lookup("#bubbleGroup");
        bubbleGroup.setMinHeight(Region.USE_PREF_SIZE);
        row.setMinHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(bubble, isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        StackPane.setAlignment(leftTail, Pos.TOP_LEFT);
        StackPane.setAlignment(rightTail, Pos.TOP_RIGHT);

        setVisible(leftTail, !isUser);
        setVisible(rightTail, isUser);
        if (isError) {
            leftTail.getStyleClass().add("error-tail");
        }

        if (isUser) {
            row.getChildren().setAll(leftSpacer, bubbleGroup, avatar);
        } else {
            row.getChildren().setAll(avatar, bubbleGroup, rightSpacer);
            playTypingAnimation(bubble, bubbleGroup, row, message);
        }

        return row;
    }

    private void playTypingAnimation(Label bubble, StackPane bubbleGroup, HBox row, String message) {
        if (message.isEmpty()) {
            return;
        }

        double typingInterval = Math.min(CHARACTER_TYPING_MILLIS, MAX_TYPING_DURATION_MILLIS / message.length());
        Timeline timeline = new Timeline();
        for (int i = 1; i <= message.length(); i++) {
            int visibleCharacters = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(typingInterval * i), event -> {
                bubble.setText(message.substring(0, visibleCharacters));
                refreshBubbleLayout(bubble, bubbleGroup, row);
            }));
        }
        timeline.setOnFinished(event -> {
            bubble.setText(message);
            Platform.runLater(() -> refreshBubbleLayout(bubble, bubbleGroup, row));
        });
        timeline.play();
    }

    private void refreshBubbleLayout(Label bubble, StackPane bubbleGroup, HBox row) {
        bubble.applyCss();
        bubble.autosize();
        bubbleGroup.requestLayout();
        row.requestLayout();
        chatBox.requestLayout();
        scrollToLatestMessage();
    }

    private HBox loadDialogBox() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/DialogBox.fxml"));
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load dialog box FXML.", e);
        }
    }

    private void setVisible(Node node, boolean isVisible) {
        node.setVisible(isVisible);
        node.setManaged(isVisible);
    }

    private void scrollToLatestMessage() {
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }
}
