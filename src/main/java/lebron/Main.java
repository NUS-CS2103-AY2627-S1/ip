package lebron;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main JavaFX application for Lebron.
 */
public class Main extends Application {

    private static final double WINDOW_WIDTH = 400.0;
    private static final double WINDOW_HEIGHT = 600.0;
    private static final double CHAT_WIDTH = 398.0;
    private static final double CHAT_HEIGHT = 535.0;
    private static final double INPUT_WIDTH = 325.0;
    private static final double SEND_BUTTON_WIDTH = 70.0;
    private static final double EDGE_OFFSET = 1.0;
    private static final double DIALOG_SPACING = 10.0;
    private static final double EXIT_DELAY_MILLISECONDS = 700.0;

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;

    private final Lebron lebron =
            new Lebron("data", "lebron.txt");

    @Override
    public void start(Stage stage) {
        initialiseControls();

        AnchorPane mainLayout = createMainLayout();

        configureStage(stage, mainLayout);
        showGreeting();

        stage.show();
    }

    /**
     * Initialises and configures the controls used in the GUI.
     */
    private void initialiseControls() {
        dialogContainer = new VBox();
        dialogContainer.setSpacing(DIALOG_SPACING);
        dialogContainer.setPadding(new Insets(12));
        dialogContainer.setStyle(
                "-fx-background-color: #fffaf0;"
        );
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        scrollPane = new ScrollPane();
        scrollPane.setContent(dialogContainer);
        scrollPane.setPrefSize(CHAT_WIDTH, CHAT_HEIGHT);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);

        userInput = new TextField();
        userInput.setPromptText("Enter your next play...");
        userInput.setPrefWidth(INPUT_WIDTH);
        userInput.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-padding: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: #FDB927;"
                        + "-fx-border-radius: 8;"
        );

        sendButton = new Button("SEND");
        sendButton.setPrefWidth(SEND_BUTTON_WIDTH);
        sendButton.setStyle(
                "-fx-background-color: #FDB927;"
                        + "-fx-text-fill: #552583;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 8;"
        );

        configureEventHandlers();
    }

    /**
     * Configures actions triggered by the GUI controls.
     */
    private void configureEventHandlers() {
        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());

        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0)
        );
    }

    /**
     * Creates and positions the main application layout.
     *
     * @return configured main layout
     */
    private AnchorPane createMainLayout() {
        AnchorPane mainLayout = new AnchorPane();

        mainLayout.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainLayout.setStyle(
                "-fx-background-color: #fffaf0;"
        );

        mainLayout.getChildren().addAll(
                scrollPane,
                userInput,
                sendButton
        );

        AnchorPane.setTopAnchor(scrollPane, EDGE_OFFSET);
        AnchorPane.setLeftAnchor(scrollPane, EDGE_OFFSET);

        AnchorPane.setLeftAnchor(userInput, EDGE_OFFSET);
        AnchorPane.setBottomAnchor(userInput, EDGE_OFFSET);

        AnchorPane.setRightAnchor(sendButton, EDGE_OFFSET);
        AnchorPane.setBottomAnchor(sendButton, EDGE_OFFSET);

        return mainLayout;
    }

    /**
     * Configures the application's main window.
     *
     * @param stage application stage
     * @param mainLayout main application layout
     */
    private void configureStage(Stage stage, AnchorPane mainLayout) {
        Scene scene = new Scene(mainLayout);

        stage.setTitle("Lebron 🏀");
        stage.setResizable(false);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setScene(scene);
    }

    /**
     * Displays Lebron's greeting when the application starts.
     */
    private void showGreeting() {
        dialogContainer.getChildren().add(
                DialogBox.getLebronDialog(
                        "Hello! I'm Lebron.\nWhat can I do for you?"
                )
        );
    }

    /**
     * Handles a command entered by the user.
     */
    private void handleUserInput() {
        String input = userInput.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        String response = lebron.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getLebronDialog(response)
        );

        userInput.clear();

        if (input.equals("bye")) {
            exitAfterDelay();
        }
    }

    /**
     * Closes the application after allowing the goodbye message to display.
     */
    private void exitAfterDelay() {
        PauseTransition delay =
                new PauseTransition(
                        Duration.millis(EXIT_DELAY_MILLISECONDS)
                );

        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }
}
