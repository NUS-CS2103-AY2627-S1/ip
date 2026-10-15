package bingbong;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Handles layout initialization and user interaction triggers for the GUI.
 */
public class MainApp extends Application {

    private final BingBong bingBong = new BingBong();
    private final Image userImage = new Image(MainApp.class.getResourceAsStream("/images/user.png"));
    private final Image bingbongImage = new Image(MainApp.class.getResourceAsStream("/images/bingbong.jpg"));
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;

    @Override
    public void start(Stage stage) {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane(scrollPane, userInput, sendButton);
        Scene scene = new Scene(mainLayout, 400, 600);

        scene.getStylesheets().add(this.getClass().getResource("/css/styles.css").toExternalForm());

        scrollPane.getStyleClass().add("scroll-pane");
        userInput.getStyleClass().add("text-field");
        sendButton.getStyleClass().add("button");

        stage.setScene(scene);
        stage.setTitle("BingBong Chatbot");
        stage.setResizable(true);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        userInput.setPrefWidth(315.0);
        sendButton.setPrefWidth(60.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);
        AnchorPane.setBottomAnchor(scrollPane, 60.0);
        AnchorPane.setLeftAnchor(scrollPane, 1.0);
        AnchorPane.setRightAnchor(scrollPane, 1.0);

        AnchorPane.setLeftAnchor(userInput, 6.0);
        AnchorPane.setRightAnchor(userInput, 76.0);
        AnchorPane.setBottomAnchor(userInput, 6.0);

        AnchorPane.setRightAnchor(sendButton, 6.0);
        AnchorPane.setBottomAnchor(sendButton, 6.0);

        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
        dialogContainer.getChildren().add(
                DialogBox.getBingBongDialog(new Label(bingBong.getGreeting()), new ImageView(bingbongImage), false)
        );

        stage.show();

        sendButton.setOnMouseClicked((event) -> handleUserInput());
        userInput.setOnAction((event) -> handleUserInput());
    }

    private void handleUserInput() {
        String inputText = userInput.getText().trim().replaceAll("\\s+", " ");
        if (inputText.isEmpty()) {
            return;
        }

        String responseText = bingBong.getResponse(inputText);
        boolean isError = responseText.contains("BING BONG! Error:")
                || responseText.contains("not know what that means");

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(new Label(inputText), new ImageView(userImage)),
                DialogBox.getBingBongDialog(new Label(responseText), new ImageView(bingbongImage), isError)
        );

        userInput.clear();

        if (inputText.equalsIgnoreCase("bye")) {
            javafx.application.Platform.exit();
        }
    }
}
