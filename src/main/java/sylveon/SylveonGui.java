package sylveon;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays a JavaFX interface for the Sylveon chatbot.
 */
public class SylveonGui extends Application {
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;

    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/jigglypic.jpg"));

    private final Image sylveonImage = new Image(
            getClass().getResourceAsStream("/images/sylveonpic.jpg"));

    /**
     * Creates and displays the Sylveon window.
     *
     * @param stage the primary application window
     */
    @Override
    public void start(Stage stage) {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();

        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        userInput.setPromptText("Type your command here...");

        sendButton = new Button("Send");

        dialogContainer.getChildren().add(DialogBox.getSylveonDialog(
                "Hi! I'm Sylveon 💗 Ready to get your tasks sorted?", sylveonImage));

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        Scene scene = new Scene(mainLayout);

        stage.setTitle("Sylveon Task Manager");
        stage.setResizable(false);
        stage.setMinWidth(400.0);
        stage.setMinHeight(600.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385.0, 535.0);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);
        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        AnchorPane.setRightAnchor(sendButton, 1.0);
        AnchorPane.setBottomAnchor(sendButton, 1.0);

        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        stage.setScene(scene);
        stage.show();
    }

    /** Adds the user's message and a response to the conversation. */
    private void handleUserInput() {
        String userText = userInput.getText().trim();

        if (userText.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getSylveonDialog("Got it! I'll help you with that task 💗", sylveonImage));
        userInput.clear();
    }
}
