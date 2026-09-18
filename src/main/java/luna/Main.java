package luna;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import luna.storage.Storage;
import luna.ui.DialogBox;
import luna.ui.Ui;

/**
 * JavaFX GUI for the Luna chatbot.
 */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 520.0;
    private static final double WINDOW_HEIGHT = 620.0;
    private static final double MINIMUM_WINDOW_WIDTH = 420.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 480.0;
    private static final double COMPOSER_HEIGHT = 52.0;
    private static final double INPUT_HEIGHT = 36.0;
    private static final double SEND_BUTTON_WIDTH = 58.0;
    private static final double EDGE_PADDING = 8.0;
    private static final double HEADER_ICON_SIZE = 38.0;
    private static final double EXIT_DELAY_MILLIS = 900.0;
    private static final String MOON_ICON = "\u263E";
    private static final String STYLESHEET_PATH = "/luna.css";

    private final Luna luna;
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private HBox inputContainer;
    private TextField userInput;
    private Button sendButton;
    private Stage primaryStage;

    /**
     * Creates the JavaFX application and its chatbot backend.
     */
    public Main() {
        this.luna = new Luna(new Ui(false), new Storage());
    }

    /**
     * Builds and shows the main JavaFX window.
     *
     * @param stage Primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("chat-scroll");

        dialogContainer = new VBox(10);
        dialogContainer.setPadding(new Insets(12));
        dialogContainer.getStyleClass().add("dialog-container");
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        userInput.setPromptText("Type a command...");
        userInput.getStyleClass().add("command-input");

        sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");

        inputContainer = new HBox(8, userInput, sendButton);
        inputContainer.setAlignment(Pos.CENTER);
        inputContainer.setPadding(new Insets(7));
        inputContainer.getStyleClass().add("composer");
        HBox.setHgrow(userInput, Priority.ALWAYS);

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getStyleClass().add("app-root");
        mainLayout.setSnapToPixel(true);
        mainLayout.getChildren().addAll(scrollPane, inputContainer);

        Scene scene = new Scene(mainLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(Main.class.getResource(STYLESHEET_PATH).toExternalForm());
        configureStage(stage, mainLayout, scene);
        registerInputHandlers();
        showStartupMessages();

        stage.show();
        userInput.requestFocus();
    }

    /**
     * Configures the window layout and sizing.
     *
     * @param stage Application window.
     * @param mainLayout Root layout pane.
     * @param scene Scene displayed on the stage.
     */
    private void configureStage(Stage stage, AnchorPane mainLayout, Scene scene) {
        stage.setTitle("Luna - Night-shift planner");
        stage.setResizable(true);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setScene(scene);

        mainLayout.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        scrollPane.setPrefSize(WINDOW_WIDTH - (EDGE_PADDING * 2),
                WINDOW_HEIGHT - COMPOSER_HEIGHT - (EDGE_PADDING * 3));
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        inputContainer.setPrefHeight(COMPOSER_HEIGHT);
        userInput.setPrefHeight(INPUT_HEIGHT);
        userInput.setMaxWidth(Double.MAX_VALUE);
        sendButton.setPrefHeight(INPUT_HEIGHT);
        sendButton.setPrefWidth(SEND_BUTTON_WIDTH);

        AnchorPane.setTopAnchor(scrollPane, EDGE_PADDING);
        AnchorPane.setLeftAnchor(scrollPane, EDGE_PADDING);
        AnchorPane.setRightAnchor(scrollPane, EDGE_PADDING);
        AnchorPane.setBottomAnchor(scrollPane, COMPOSER_HEIGHT + (EDGE_PADDING * 2));

        AnchorPane.setLeftAnchor(inputContainer, EDGE_PADDING);
        AnchorPane.setRightAnchor(inputContainer, EDGE_PADDING);
        AnchorPane.setBottomAnchor(inputContainer, EDGE_PADDING);
    }

    /**
     * Registers the handlers for sending chat messages.
     */
    private void registerInputHandlers() {
        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());
    }

    /**
     * Shows any startup notice and Luna's welcome message.
     */
    private void showStartupMessages() {
        dialogContainer.getChildren().add(createAppHeader());

        String startupNotice = luna.consumePendingResponse();
        if (!startupNotice.isBlank()) {
            dialogContainer.getChildren().add(createResponseDialog(startupNotice));
        }

        dialogContainer.getChildren().add(DialogBox.getWelcomeDialog(luna.getWelcomeMessage()));
    }

    /**
     * Creates the branded header shown above the conversation.
     *
     * @return Compact header matching Luna's night-shift personality.
     */
    private HBox createAppHeader() {
        Label iconLabel = new Label(MOON_ICON);
        iconLabel.setMinSize(HEADER_ICON_SIZE, HEADER_ICON_SIZE);
        iconLabel.setPrefSize(HEADER_ICON_SIZE, HEADER_ICON_SIZE);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.getStyleClass().add("app-header-icon");

        Label titleLabel = new Label("Luna");
        titleLabel.getStyleClass().add("app-header-title");
        Label subtitleLabel = new Label("Calm focus after dark");
        subtitleLabel.getStyleClass().add("app-header-subtitle");
        VBox identity = new VBox(1, titleLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label("NIGHT SHIFT");
        statusLabel.getStyleClass().add("app-header-status");

        HBox appHeader = new HBox(11, iconLabel, identity, spacer, statusLabel);
        appHeader.setAlignment(Pos.CENTER_LEFT);
        appHeader.getStyleClass().add("app-header");
        return appHeader;
    }

    /**
     * Sends the current input to Luna and appends the conversation to the GUI.
     */
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = luna.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                createResponseDialog(response)
        );
        userInput.clear();

        if (luna.isExitRequested()) {
            startExitSequence();
        }
    }

    /**
     * Prevents further input and closes the window after the farewell has been visible briefly.
     */
    private void startExitSequence() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition exitDelay = new PauseTransition(Duration.millis(EXIT_DELAY_MILLIS));
        exitDelay.setOnFinished(event -> primaryStage.close());
        exitDelay.play();
    }

    /**
     * Creates the appropriate Luna panel for a normal response or an error.
     *
     * @param response Response text to display.
     * @return Dialog box using the response's presentation type.
     */
    private DialogBox createResponseDialog(String response) {
        if (luna.isLatestResponseError()) {
            return DialogBox.getErrorDialog(response);
        }
        if (luna.isLatestResponseHelp()) {
            return DialogBox.getHelpDialog(response);
        }

        return DialogBox.getLunaDialog(response);
    }
}
