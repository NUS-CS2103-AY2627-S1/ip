package happy;

import java.nio.file.Paths;

import happy.ui.DialogBox;
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
 * Main JavaFX Application class for the Happy chatbot.
 */
public class Main extends Application {
    private final Happy happy = new Happy(Paths.get(".", "data", "happy.txt").toString());

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    private Image userImage = new Image(getClass().getResourceAsStream("/images/happygoose.jpg"));
    private Image happyImage = new Image(getClass().getResourceAsStream("/images/dog.jpg"));

    @Override
    public void start(Stage stage) {
        initializeControls();

        AnchorPane mainLayout = new AnchorPane(scrollPane, userInput, sendButton);
        mainLayout.setPrefSize(400.0, 600.0);

        scene = new Scene(mainLayout);
        scene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());

        configureStage(stage);
        configureLayoutAnchors();
        showWelcomeGreeting();

        stage.show();
    }

    private void initializeControls() {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox(10);
        dialogContainer.setPadding(new javafx.geometry.Insets(10));
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        scrollPane.setContent(dialogContainer);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        userInput = new TextField();
        sendButton = new Button("Send");
        sendButton.setPrefWidth(55.0);

        sendButton.setOnMouseClicked((event) -> handleUserInput());
        userInput.setOnAction((event) -> handleUserInput());
    }

    private void configureStage(Stage stage) {
        stage.setScene(scene);
        stage.setTitle("Happy Chatbot");
        stage.setResizable(true);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);
    }

    private void configureLayoutAnchors() {
        AnchorPane.setTopAnchor(scrollPane, 1.0);
        AnchorPane.setLeftAnchor(scrollPane, 1.0);
        AnchorPane.setRightAnchor(scrollPane, 1.0);
        AnchorPane.setBottomAnchor(scrollPane, 45.0);

        AnchorPane.setBottomAnchor(userInput, 1.0);
        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setRightAnchor(userInput, 65.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);
    }

    private void showWelcomeGreeting() {
        Label welcomeText = new Label(
            "Hellllloooooooooo! I'm Happy, the happiest chatbot in the world! What can I do for you today?"
        );
        dialogContainer.getChildren().add(DialogBox.getHappyDialog(welcomeText, new ImageView(happyImage)));
    }

    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        Label userText = new Label(input);
        Label happyText = new Label(happy.getResponse(input));
        dialogContainer.getChildren().addAll(
            DialogBox.getUserDialog(userText, new ImageView(userImage)),
            DialogBox.getHappyDialog(happyText, new ImageView(happyImage))
        );
        userInput.clear();
    }
}
