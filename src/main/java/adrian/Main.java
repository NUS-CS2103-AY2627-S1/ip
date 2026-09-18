package adrian;

import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays the JavaFX user interface for Adrian.
 */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 520.0;
    private static final double WINDOW_HEIGHT = 680.0;
    private static final double MINIMUM_WINDOW_WIDTH = 360.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 460.0;
    private static final double SEND_BUTTON_WIDTH = 88.0;

    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox dialogContainer = new VBox();
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Transmit");
    private final Adrian adrian = new Adrian();
    private final Image userImage = loadImage("/images/rocky.jpeg");
    private final Image adrianImage = loadImage("/images/adrian.jpeg");

    /**
     * Creates the Adrian JavaFX application.
     */
    public Main() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param stage primary window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        configureDialogContainer();
        configureScrollPane();
        configureInputControls();
        AnchorPane mainLayout = createMainLayout();
        showStage(stage, mainLayout);
    }

    /**
     * Configures the message container and adds Adrian's welcome message.
     */
    private void configureDialogContainer() {
        dialogContainer.setPadding(new Insets(14.0));
        dialogContainer.setSpacing(8.0);
        dialogContainer.getStyleClass().add("dialog-container");
        dialogContainer.getChildren().add(
                DialogBox.getAdrianDialog(adrian.getWelcomeMessage(), adrianImage));
    }

    /**
     * Configures the scrolling behavior for the message container.
     */
    private void configureScrollPane() {
        scrollPane.setContent(dialogContainer);
        scrollPane.getStyleClass().add("dialog-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Configures the text field and button used to submit commands.
     */
    private void configureInputControls() {
        userInput.setPromptText("Rocky, enter a mission command...");
        userInput.setPrefHeight(42.0);
        sendButton.setPrefSize(SEND_BUTTON_WIDTH, 42.0);
        userInput.setOnAction(event -> handleUserInput());
        sendButton.setOnAction(event -> handleUserInput());
    }

    /**
     * Creates and anchors the controls in the application's root layout.
     *
     * @return configured root layout.
     */
    private AnchorPane createMainLayout() {
        AnchorPane mainLayout = new AnchorPane();
        mainLayout.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainLayout.getStyleClass().add("main-layout");
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        AnchorPane.setTopAnchor(scrollPane, 12.0);
        AnchorPane.setRightAnchor(scrollPane, 12.0);
        AnchorPane.setBottomAnchor(scrollPane, 66.0);
        AnchorPane.setLeftAnchor(scrollPane, 12.0);

        AnchorPane.setRightAnchor(userInput, SEND_BUTTON_WIDTH + 24.0);
        AnchorPane.setBottomAnchor(userInput, 12.0);
        AnchorPane.setLeftAnchor(userInput, 12.0);

        AnchorPane.setRightAnchor(sendButton, 12.0);
        AnchorPane.setBottomAnchor(sendButton, 12.0);

        return mainLayout;
    }

    /**
     * Configures and displays the primary application window.
     *
     * @param stage primary window supplied by JavaFX.
     * @param mainLayout root layout to display.
     */
    private void showStage(Stage stage, AnchorPane mainLayout) {
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(getResource("/styles/dark-theme.css").toExternalForm());
        stage.setTitle("Adrian // Mission Control");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Processes the user's input and displays the command and Adrian's response.
     */
    private void handleUserInput() {
        String input = userInput.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        String response = adrian.getResponse(input);
        DialogBox userMessage = DialogBox.getUserDialog(input, userImage);
        DialogBox adrianMessage = response.startsWith("OOPS!!!")
                ? DialogBox.getErrorDialog(response, adrianImage)
                : DialogBox.getAdrianDialog(response, adrianImage);

        dialogContainer.getChildren().addAll(userMessage, adrianMessage);
        userInput.clear();

        if (adrian.isExitRequested()) {
            Platform.exit();
        }
    }

    /**
     * Loads an image bundled with the application.
     *
     * @param resourcePath classpath location of the image.
     * @return image loaded from the resource.
     * @throws IllegalStateException if the resource does not exist.
     */
    private static Image loadImage(String resourcePath) {
        return new Image(getResource(resourcePath).toExternalForm());
    }

    /**
     * Locates a resource bundled with the application.
     *
     * @param resourcePath classpath location of the resource.
     * @return URL identifying the resource.
     * @throws IllegalStateException if the resource does not exist.
     */
    private static URL getResource(String resourcePath) {
        URL resource = Main.class.getResource(resourcePath);

        if (resource == null) {
            throw new IllegalStateException("Missing application resource: " + resourcePath);
        }

        return resource;
    }
}
