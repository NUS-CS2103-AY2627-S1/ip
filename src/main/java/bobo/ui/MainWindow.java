package bobo.ui;

import bobo.Bobo;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;

/**
 * Controller and layout container for the main JavaFX chat view.
 */
public class MainWindow extends AnchorPane {

    private final ScrollPane scrollPane;
    private final VBox dialogContainer;
    private final TextField userInput;
    private final Button sendButton;

    private Bobo bobo;

    private final Image userImage;
    private final Image boboImage;

    /**
     * Constructs the main chat window layout and initializes GUI controls.
     */
    public MainWindow() {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        userInput = new TextField();
        sendButton = new Button("Send");

        userImage = loadImage("/images/user.png");
        boboImage = loadImage("/images/bobo.png");
        Image bgImage = loadImage("/images/bg.png");

        setupLayout(bgImage);
        setupEventHandlers();
    }

    private Image loadImage(String resourcePath) {
        try {
            var stream = getClass().getResourceAsStream(resourcePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            // Ignore missing images gracefully
        }
        return null;
    }

    private void setupLayout(Image bgImage) {
        setPrefSize(450.0, 650.0);

        if (bgImage != null && !bgImage.isError()) {
            BackgroundSize bgSize = new BackgroundSize(
                    BackgroundSize.AUTO, 1.0, false, true, false, false
            );
            BackgroundPosition bgPos = new BackgroundPosition(
                    Side.RIGHT, 0, false,
                    Side.TOP, 0, false
            );
            BackgroundImage bg = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    bgPos,
                    bgSize
            );
            setBackground(new Background(bg));
        } else {
            setStyle("-fx-background-color: #FAFAFA;");
        }

        scrollPane.setContent(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        dialogContainer.setSpacing(12);
        dialogContainer.setStyle("-fx-padding: 12; -fx-background-color: transparent;");
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 55.0);

        userInput.setPrefHeight(42.0);
        userInput.setPromptText("Type a command (or 'help')...");
        userInput.setStyle("-fx-background-radius: 20px; -fx-background-color: #FFFFFF; "
                + "-fx-border-color: #CBD5E0; -fx-border-radius: 20px; -fx-padding: 8px 14px; "
                + "-fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif;");
        AnchorPane.setLeftAnchor(userInput, 10.0);
        AnchorPane.setBottomAnchor(userInput, 8.0);

        sendButton.setPrefHeight(42.0);
        sendButton.setPrefWidth(75.0);
        sendButton.setStyle("-fx-background-radius: 20px; -fx-background-color: #007AFF; "
                + "-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-cursor: hand; "
                + "-fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif;");
        sendButton.setOnMouseEntered((e) -> sendButton.setStyle("-fx-background-radius: 20px; "
                + "-fx-background-color: #0062CC; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; "
                + "-fx-cursor: hand; -fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif;"));
        sendButton.setOnMouseExited((e) -> sendButton.setStyle("-fx-background-radius: 20px; "
                + "-fx-background-color: #007AFF; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; "
                + "-fx-cursor: hand; -fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif;"));

        AnchorPane.setRightAnchor(sendButton, 10.0);
        AnchorPane.setBottomAnchor(sendButton, 8.0);

        userInput.prefWidthProperty().bind(widthProperty().subtract(105.0));

        getChildren().addAll(scrollPane, userInput, sendButton);
    }

    private void setupEventHandlers() {
        sendButton.setOnAction((event) -> handleUserInput());
        userInput.setOnAction((event) -> handleUserInput());
    }

    /**
     * Sets the Bobo chatbot instance and displays initial welcome message.
     *
     * @param b Bobo instance.
     */
    public void setBobo(Bobo b) {
        assert b != null : "Bobo instance passed to MainWindow must not be null";
        this.bobo = b;
        dialogContainer.getChildren().add(DialogBox.getBoboDialog(bobo.getWelcomeMessage(), boboImage));
    }

    /**
     * Handles user input by displaying user and bot dialogs, and processing exit.
     */
    private void handleUserInput() {
        assert bobo != null : "Bobo instance must be set before handling user input";
        String input = userInput.getText();
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String response = bobo.getResponse(input);
        boolean isError = response.startsWith("OOPS!!!") || response.startsWith("Error:")
                || response.startsWith("Please specify");

        DialogBox boboDialog = isError
                ? DialogBox.getBoboErrorDialog(response, boboImage)
                : DialogBox.getBoboDialog(response, boboImage);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                boboDialog
        );
        userInput.clear();

        if (bobo.isExit()) {
            Platform.exit();
        }
    }
}
